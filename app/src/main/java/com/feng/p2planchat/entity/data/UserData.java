package com.feng.p2planchat.entity.data;

import android.graphics.Bitmap;
import android.widget.TextView;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserData {
    private Bitmap headImage;      //对方用户头像
    private String name;        //对方用户名
    private String content;     //最近聊天内容
    private String time;        //最近聊天时间

    public UserData(Bitmap headImage, String name, String content, String time) {
        this.headImage = headImage;
        this.name = name;
        this.content = content;
        this.time = time;
    }

    public Bitmap getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Bitmap headImage) {
        this.headImage = headImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
