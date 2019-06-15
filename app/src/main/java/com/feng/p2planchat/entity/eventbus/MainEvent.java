package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.serializable.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class MainEvent {
    private List<User> userList;    //其他用户的信息

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
