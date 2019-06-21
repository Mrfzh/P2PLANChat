package com.feng.p2planchat.contract;

import android.content.Context;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public interface IChatContract {
    interface View {
        void sendTextSuccess();
        void sendTextError(String errorMsg);
    }
    interface Presenter {
        void sendTextSuccess();
        void sendTextError(String errorMsg);
        void sendText(Context context, String otherIp, String content);
    }
    interface Model {
        void sendText(Context context, String otherIp, String content);
    }
}
