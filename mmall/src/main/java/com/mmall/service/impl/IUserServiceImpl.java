package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        //调用dao层 获取是否存在用户(0:不存在;1:存在)
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return  ServerResponse.createByErrorMessage("用户名不存在");
        }
        //判断用户密码
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user == null){
            return  ServerResponse.createByErrorMessage("密码错误");
        }
        //都正确
        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess("登录成功",user);
    }

    /**
     * 用户注册
     */
    public ServerResponse<String> register(User user){
        ServerResponse checkValid = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!checkValid.isSuccess()){
            return checkValid;
        }
        checkValid = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!checkValid.isSuccess()){
            return checkValid;
        }

       /* //调用dao层 获取是否存在用户(0:不存在;1:存在)
        int resultCount = userMapper.checkUsername(user.getUsername());
        if(resultCount > 0){
            return  ServerResponse.createByErrorMessage("用户名已存在");
        }
        //调用dao层 获取邮箱是否存在 (0:不存在;1:存在)
        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }*/
        //给用户设置权限级别(普通用户)
        user.setRole(Const.role.ROLE_CUSTOMER);

        //MD5密码加密用户密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        //调用dao层写入数据库
        int resultCount= userMapper.insert(user);
        if(resultCount == 0){
            return  ServerResponse.createByErrorMessage("注册失败");
        }

        return  ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 判断用户名和邮箱,防止用户调用接口验证
     */
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNoneBlank(type)){
            //开始校验
            //用户名
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return  ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            //邮箱
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            //返回参数错误
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 忘记密码 , 查看是否有该用户
     */
    public ServerResponse selectQuestion(String username){
        ServerResponse<String> serverResponse = checkValid(username, Const.USERNAME);
        if(serverResponse.isSuccess()){
            //应该是为用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        //判断是否为空(正确的)
        if(StringUtils.isNoneBlank(question)){
            //成功
            return  ServerResponse.createBySuccess(question);
        }
        //错误 为空
        return ServerResponse.createByErrorMessage("找回密码的问题为空");

    }

    /**
     * 提示问题与答案
     */
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            //说明问题及问题答案是这个用户的,并且也是正确的

            //给其设置id(UUID)
            String forgetToken = UUID.randomUUID().toString();
            //本地缓存
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");

    }

    /**
     * 忘记密码的重置密码
     */
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        //先判断 forgetToken 是否为空
        if(StringUtils.isBlank(forgetToken)){
            //为空,返回错误信息
            return ServerResponse.createByErrorMessage("参数传递错误");
        }
        //判断用户名
        ServerResponse<String> serverResponse = checkValid(username, Const.USERNAME);
        if(serverResponse.isSuccess()){
            //应该是为用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        //获取缓存中存的信息 ,通过 key 找 value
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        //判断token 是否存在
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token过期或者无效");
        }

        //都判断完成后,就可以进行密码更新,操作dao层写入数据库
        //获得新密码
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取密码的重置token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登录状态下,修改密码
     * @param user
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew){
        //防止横向越权,要校验用户旧密码,一定要是这个用户的,
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount ==0){
            return  ServerResponse.createByErrorMessage("密码错误");
        }
        //把新密码存入user中
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }

        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 用户信息修改
     */
    public ServerResponse<User> update_infomation(User user){
        //username不能被更改
        //判断更改的email的地址不与当前用户的email 相同,如果相同就不正确,因为是别人已经注册过的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        //判断是否大于0 大于0 则提示用户邮箱已经占用
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("邮箱已被注册,换个邮箱试试");
        }
        //单独生成一个 更新user ,只为了更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        //更新操作
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人用户成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人用户信息失败");

    }
    /**
     * 获取用户详细信息
     */
    public ServerResponse<User> get_infomation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);

        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //将密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    /**
     *  后台 backend
     */

    /**
     * 校验是否为管理员
     * @param user
     * @return
     */

    public ServerResponse checkAdminRole(User user){
        //判断用户是否不为空和是否为管理员
        if(user != null && user.getRole().intValue() == Const.role.ROLE_ADMIN){
            //返回成功
            return  ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
