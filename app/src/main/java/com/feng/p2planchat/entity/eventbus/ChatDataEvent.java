package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.serializable.ChatData;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/22
 */
public class ChatDataEvent {
    private String name;    //对方用户名
    private String ip;      //对方IP地址
    private ChatData chatData;
    private List<ChatData> chatDataList;
    private boolean isOneData;      //是否只有一条信息

    public ChatDataEvent(ChatData chatData) {
        this.chatData = chatData;
        this.isOneData = true;
    }

    public ChatDataEvent(List<ChatData> chatDataList) {
        this.chatDataList = chatDataList;
        this.isOneData = false;
    }

    public ChatDataEvent(String name, String ip, ChatData chatData) {
        this.name = name;
        this.ip = ip;
        this.chatData = chatData;
        this.isOneData = true;
    }

    public ChatDataEvent(String name, String ip, List<ChatData> chatDataList) {
        this.name = name;
        this.ip = ip;
        this.chatDataList = chatDataList;
        this.isOneData = false;
    }

    public ChatData getChatData() {
        return chatData;
    }

    public void setChatData(ChatData chatData) {
        this.chatData = chatData;
    }

    public List<ChatData> getChatDataList() {
        return chatDataList;
    }

    public void setChatDataList(List<ChatData> chatDataList) {
        this.chatDataList = chatDataList;
    }

    public boolean isOneData() {
        return isOneData;
    }

    public void setOneData(boolean oneData) {
        isOneData = oneData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
