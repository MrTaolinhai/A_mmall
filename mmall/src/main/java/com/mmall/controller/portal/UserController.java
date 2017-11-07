package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody //json序列化
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> userLogin = iUserService.login(username, password);
        if(userLogin.isSuccess()){
            //如果成功,将用户名存入session中
            session.setAttribute(Const.CURRENT_USER,userLogin.getData());
        }
        return userLogin;
    }

    /**
     * 用户登出
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){

        return iUserService.register(user);
    }

    /**
     * 校验
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获得当前用户信息");
    }



    /**
     * 忘记密码 (获取提示信息)
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 提示问题与答案
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return  iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 忘记密码,重置密码
     */
    @RequestMapping(value = "forget_rest_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){

        return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }
    /**
     * 登录状态下的重置密码
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        //从session中获取用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user,passwordOld,passwordNew);
    }

    /**
     * 更新个人用户信息
     */
    @RequestMapping(value = "update_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_infomation(HttpSession session,User user){
        //从session中获取用户
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //因为登录时候user信息中 userId 和 usernamne 要为当前登录的用户的这两个值
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        //调用service层方法
        ServerResponse<User> response = iUserService.update_infomation(user);
        if(response.isSuccess()){
            //如果正确的,更新session
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return  response;
    }
    /**
     * 获取用户详细信息
     */
    @RequestMapping(value = "get_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_infomation(HttpSession session){
        //从session中获取用户
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录需要强制登录status=10");

        }
        //这样返回的就是所有信息,并且把密码置空
        return iUserService.get_infomation(currentUser.getId());
    }

}
