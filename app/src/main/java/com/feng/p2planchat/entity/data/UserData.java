package com.feng.p2planchat.entity.data;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.feng.p2planchat.entity.serializable.ChatData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserData {
    private Bitmap headImage;      //对方用户头像
    private String name;        //对方用户名
    private String ip;          //对方IP地址
    private String content;     //最近聊天内容
    private String time;        //最近聊天时间
    private List<ChatData> chatDataList;    //聊天消息集合

    public UserData(Bitmap headImage, String name, String ip) {
        this.headImage = headImage;
        this.name = name;
        this.ip = ip;
        this.content = "";
        this.time = "";
        this.chatDataList = new ArrayList<>();
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<ChatData> getChatDataList() {
        return chatDataList;
    }

    public void setChatDataList(List<ChatData> chatDataList) {
        this.chatDataList = chatDataList;
    }

    @Override
    public String toString() {
        return "time = " + this.time + ", content = " + this.content;
    }
}
