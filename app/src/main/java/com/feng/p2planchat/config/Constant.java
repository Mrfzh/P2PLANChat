package com.feng.p2planchat.config;

import com.feng.p2planchat.R;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public class Constant {

    public static final int USER_PORT = 20000;  //获取用户信息的端口号

    //主页面底部tab的title
    public static final String [] MAIN_TAB_TITLES = {"用户列表", "我"};
    //主页面底部tab的icon
    public static final int [] MAIN_TAB_ICONS_BEFORE_PRESSED = {R.drawable.user_list_before_pressed,
        R.drawable.person_before_pressed};
    public static final int [] MAIN_TAB_ICONS_AFTER_PRESSED = {R.drawable.user_list_after_pressed,
        R.drawable.person_after_pressed};

}
