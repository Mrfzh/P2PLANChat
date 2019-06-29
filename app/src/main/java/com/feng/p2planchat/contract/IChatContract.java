package com.feng.p2planchat.contract;

import android.content.Context;

import com.feng.p2planchat.entity.serializable.ChatData;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public interface IChatContract {
    interface View {
        void sendTextSuccess(ChatData chatData);
        void sendTextError(String errorMsg);
        void sendPictureSuccess(ChatData chatData);
        void sendPictureError(String errorMsg);
    }
    interface Presenter {
        void sendTextSuccess(ChatData chatData);
        void sendTextError(String errorMsg);
        void sendPictureSuccess(ChatData chatData);
        void sendPictureError(String errorMsg);
        void sendText(Context context, String otherIp, ChatData chatData);
        void sendPicture(Context context, String otherIp, ChatData chatData);
    }
    interface Model {
        void sendText(Context context, String otherIp, ChatData chatData);
        void sendPicture(Context context, String otherIp, ChatData chatData);
    }
}
