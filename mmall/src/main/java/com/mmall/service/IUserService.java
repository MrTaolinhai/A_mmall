package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    //登录
    ServerResponse<User> login(String username, String password);
    //注册
    public ServerResponse<String> register(User user);
    //检测邮箱和用户名(校验接口)
    public ServerResponse<String> checkValid(String str,String type);
    //忘记密码,的问题
    public ServerResponse selectQuestion(String username);
    //提示问题与答案
    public ServerResponse<String> checkAnswer(String username,String question,String answer);
    //忘记密码的重置密码
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken);
    //登录状态下,修改密码
    ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew);
    //修改个人用户信息
    public ServerResponse<User> update_infomation(User user);
    //获取用户详细信息
    public ServerResponse<User> get_infomation(Integer userId);
}
