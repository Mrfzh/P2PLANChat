package com.feng.p2planchat.entity.serializable;

import com.feng.p2planchat.config.Constant;

import java.io.Serializable;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String oldName;
    private String newName;
    private byte [] newHeadImage;
    //要更新的信息，可供选择的值：Constant.UPDATE_USER_NAME、Constant.UPDATE_HEAD_IMAGE
    private int updateWhat;

    public UpdateUser(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
        this.updateWhat = Constant.UPDATE_USER_NAME;
    }

    public UpdateUser(String oldName, byte[] newHeadImage) {
        this.oldName = oldName;
        this.newHeadImage = newHeadImage;
        this.updateWhat = Constant.UPDATE_HEAD_IMAGE;
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

    public byte[] getNewHeadImage() {
        return newHeadImage;
    }

    public void setNewHeadImage(byte[] newHeadImage) {
        this.newHeadImage = newHeadImage;
    }

    public int getUpdateWhat() {
        return updateWhat;
    }

    public void setUpdateWhat(int updateWhat) {
        this.updateWhat = updateWhat;
    }
}
