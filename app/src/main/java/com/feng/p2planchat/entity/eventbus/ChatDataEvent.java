package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.serializable.ChatData;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/22
 */
public class ChatDataEvent {
    private ChatData chatData;

    public ChatDataEvent(ChatData chatData) {
        this.chatData = chatData;
    }

    public ChatData getChatData() {
        return chatData;
    }

    public void setChatData(ChatData chatData) {
        this.chatData = chatData;
    }
}
