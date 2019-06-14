package com.feng.p2planchat.entity.eventbus;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public class UpdateNameEvent {
    private String newName;

    public UpdateNameEvent(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
