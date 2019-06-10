package com.feng.p2planchat.entity;

import java.io.Serializable;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/10
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ipAddress;
    private String userName;

    public User(String ipAddress, String userName) {
        this.ipAddress = ipAddress;
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
