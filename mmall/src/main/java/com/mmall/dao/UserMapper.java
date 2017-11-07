package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    //忘记密码,重置
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    //检测用户的密码
    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    //通过id查询是否为当前email
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}