package com.feng.p2planchat.entity.serializable;

import java.io.Serializable;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/16
 */
public class ChatData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ip;          //发送消息的用户IP地址
    private String name;        //发送消息的用户
    private byte[] headImage;   //发送消息的用户头像
    private String content;     //文字消息的内容
    private String time;        //发送时间
    private byte[] picture;     //图片消息的内容
    private int type;   //发送的消息类型

    //消息类型
    public static final int SEND_TEXT = 1;
    public static final int RECEIVE_TEXT = 2;
    public static final int SEND_PICTURE = 3;
    public static final int RECEIVE_PICTURE = 4;
    public static final int TIME = 5;

    //发送或接收文字信息
    public ChatData(String ip, String name, byte[] headImage, String content,
                    String time, int type) {
        this.ip = ip;
        this.name = name;
        this.headImage = headImage;
        this.content = content;
        this.time = time;
        this.type = type;
    }

    //发送或接收图片信息
    public ChatData(String ip, String name, byte[] headImage, String time,
                    byte[] picture, int type) {
        this.ip = ip;
        this.name = name;
        this.headImage = headImage;
        this.time = time;
        this.picture = picture;
        this.type = type;
    }

    //时间
    public ChatData(String ip, String time) {
        this.ip = ip;
        this.time = time;
        this.type = TIME;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public byte[] getHeadImage() {
        return headImage;
    }

    public void setHeadImage(byte[] headImage) {
        this.headImage = headImage;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "name = " + name + ", content = " + content +
                ", time = " + time + ", type = " + type;
    }
}