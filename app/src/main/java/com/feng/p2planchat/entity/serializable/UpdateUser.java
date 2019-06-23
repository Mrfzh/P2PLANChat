package com.feng.p2planchat.entity.serializable;

import com.feng.p2planchat.config.Constant;

import java.io.Serializable;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ip;          //更新用户的ip地址
    private String newName;
    private byte [] newHeadImage;
    //要更新的信息，
    //可供选择的值：Constant.UPDATE_USER_NAME、Constant.UPDATE_HEAD_IMAGE、Constant.DELETE_USER
    private int updateWhat;

    //更新用户名
    public UpdateUser(String ip, String newName) {
        this.ip = ip;
        this.newName = newName;
        this.updateWhat = Constant.UPDATE_USER_NAME;
    }

    //更新用户头像
    public UpdateUser(String ip, byte[] newHeadImage) {
        this.ip = ip;
        this.newHeadImage = newHeadImage;
        this.updateWhat = Constant.UPDATE_HEAD_IMAGE;
    }

    //删除用户（该用户退出登录）
    public UpdateUser(String ip) {
        this.ip = ip;
        this.updateWhat = Constant.DELETE_USER;
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
