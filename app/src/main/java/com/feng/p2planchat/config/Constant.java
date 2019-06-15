package com.feng.p2planchat.config;

import com.feng.p2planchat.R;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public class Constant {

    public static final String DATABASE_ACCOUNT = "Account.db";     //用户数据库
    public static final String TABLE_ACCOUNT = "account";   //用户表
    public static final String KEY_NAME = "name";           //用户表的用户名key
    public static final String KEY_PASSWORD = "password";   //用户表的密码key

    public static final int USER_PORT = 20000;  //获取用户信息的端口号
    public static final int UPDATE_PORT = 21000;    //更新用户信息的端口号

    //主页面底部tab的title
    public static final String [] MAIN_TAB_TITLES = {"用户列表", "我"};
    //主页面底部tab的icon
    public static final int [] MAIN_TAB_ICONS_BEFORE_PRESSED = {R.drawable.user_list_before_pressed,
        R.drawable.person_before_pressed};
    public static final int [] MAIN_TAB_ICONS_AFTER_PRESSED = {R.drawable.user_list_after_pressed,
        R.drawable.person_after_pressed};

    public static final String USER_FILE_NAME = "user.txt";     //该文件用于存储自己的用户信息
    public static final String OTHER_USER_IP_FILE_NAME = "otherUserIp.txt";   //该文件存储其他在线用户的IP地址

    //更新某种用户信息
    public static final int UPDATE_USER_NAME = 1;
    public static final int UPDATE_HEAD_IMAGE = 2;
}
