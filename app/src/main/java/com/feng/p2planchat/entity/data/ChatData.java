package com.feng.p2planchat.entity.data;

import android.graphics.Bitmap;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/16
 */
public class ChatData {
    private Bitmap headImage;
    private String content;
    private Bitmap picture;
    private int type;   //发送的消息类型

    //消息类型
    public static final int SEND_TEXT = 1;
    public static final int RECEIVE_TEXT = 2;
    public static final int SEND_PICTURE = 3;
    public static final int RECEIVE_PICTURE = 4;
    public static final int TIME = 5;

    //发送或接收文字信息
    public ChatData(Bitmap headImage, String content, int type) {
        this.headImage = headImage;
        this.content = content;
        this.picture = null;
        this.type = type;
    }

    //发送或接收图片信息
    public ChatData(Bitmap headImage, Bitmap picture, int type) {
        this.headImage = headImage;
        this.content = "";
        this.picture = picture;
        this.type = type;
    }

    //发送时间
    public ChatData(String time) {
        this.content = time;
        this.headImage = null;
        this.picture = null;
        this.type = TIME;
    }

    public Bitmap getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Bitmap headImage) {
        this.headImage = headImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
