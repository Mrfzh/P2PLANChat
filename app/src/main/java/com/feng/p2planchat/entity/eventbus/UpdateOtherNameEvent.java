package com.feng.p2planchat.entity.eventbus;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateOtherNameEvent {
    private String ip;
    private String newName;

    public UpdateOtherNameEvent(String ip, String newName) {
        this.ip = ip;
        this.newName = newName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
