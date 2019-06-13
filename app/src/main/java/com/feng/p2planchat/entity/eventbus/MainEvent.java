package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.bean.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class MainEvent {
    private List<User> userList;    //其他用户的信息
    private User ownInfo;   //自己的信息

    public MainEvent(List<User> userList, User ownInfo) {
        this.userList = userList;
        this.ownInfo = ownInfo;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User getOwnInfo() {
        return ownInfo;
    }

    public void setOwnInfo(User ownInfo) {
        this.ownInfo = ownInfo;
    }
}
