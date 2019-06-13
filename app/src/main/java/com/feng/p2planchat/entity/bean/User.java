package com.feng.p2planchat.entity.bean;

import java.io.Serializable;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/10
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ipAddress;
    private String userName;
    private byte [] headImage;

    public User(String ipAddress, String userName) {
        this.ipAddress = ipAddress;
        this.userName = userName;
    }

    public User(String ipAddress, String userName, byte[] headImage) {
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.headImage = headImage;
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

    public byte[] getHeadImage() {
        return headImage;
    }

    public void setHeadImage(byte[] headImage) {
        this.headImage = headImage;
    }

    public String show() {
        return "ip地址 = " + ipAddress + ", 用户名 = " + userName;
    }
}
