package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class MainEvent {
    private List<User> userList;

    public MainEvent(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}