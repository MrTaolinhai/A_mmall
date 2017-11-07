package com.mmall.common;

public class Const {
    //定义当前用户常量
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    //定义级别类接口
    public interface  role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

}
