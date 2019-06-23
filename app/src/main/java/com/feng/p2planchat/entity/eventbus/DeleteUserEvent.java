package com.feng.p2planchat.entity.eventbus;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/19
 */
public class DeleteUserEvent {
    private String ip;

    public DeleteUserEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
