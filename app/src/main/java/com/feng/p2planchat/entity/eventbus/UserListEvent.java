package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.serializable.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserListEvent {
    private List<User> userList;    //用户信息列表
    private User newUser;       //新上线的用户
    private boolean isOneUser;  //是否为更新一个用户信息

    private UserListEvent(List<User> userList, User newUser, boolean isOneUser) {
        this.userList = userList;
        this.newUser = newUser;
        this.isOneUser = isOneUser;
    }

    public UserListEvent(List<User> userList) {
        this(userList, null, false);
    }

    public UserListEvent(User newUser) {
        this(new ArrayList<User>(), newUser, true);
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public boolean isOneUser() {
        return isOneUser;
    }

    public void setOneUser(boolean oneUser) {
        isOneUser = oneUser;
    }

}
