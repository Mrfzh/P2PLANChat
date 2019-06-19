package com.feng.p2planchat.contract;

import android.content.Context;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/19
 */
public interface IPersonContract {
    interface View {
        void logoutSuccess();
        void logoutError(String errorMsg);
    }
    interface Presenter {
        void logoutSuccess();
        void logoutError(String errorMsg);
        void logout(Context context);
    }
    interface Model {
        void logout(Context context);
    }
}
