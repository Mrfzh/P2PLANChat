package com.feng.p2planchat.contract;

import android.content.Context;

import com.feng.p2planchat.entity.bean.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public interface ILoginContract {
    interface View {
        void loginSuccess(List<User> userList);
        void loginError(String errorMsg);
    }
    interface Presenter {
        void loginSuccess(List<User> userList);
        void loginError(String errorMsg);
        void login(User user, Context context);
    }
    interface Model {
        void login(User user, Context context);
    }
}
