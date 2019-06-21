package com.feng.p2planchat.entity.eventbus;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public class ChatEvent {
    private String otherName;   //对方的用户名
    private String otherIp;     //对方的IP地址

    public ChatEvent(String otherName, String otherIp) {
        this.otherName = otherName;
        this.otherIp = otherIp;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getOtherIp() {
        return otherIp;
    }

    public void setOtherIp(String otherIp) {
        this.otherIp = otherIp;
    }
}
