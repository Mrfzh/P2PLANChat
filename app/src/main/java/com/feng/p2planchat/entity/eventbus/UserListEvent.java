package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.bean.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserListEvent {
    private List<User> userList;    //用户信息列表

    public UserListEvent(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
