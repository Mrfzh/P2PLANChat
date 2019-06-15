package com.feng.p2planchat.entity.eventbus;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateOtherNameEvent {
    private String oldName;
    private String newName;

    public UpdateOtherNameEvent(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
