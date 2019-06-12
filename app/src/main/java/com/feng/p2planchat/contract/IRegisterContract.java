package com.feng.p2planchat.contract;

import android.content.Context;

import com.feng.p2planchat.entity.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/12
 */
public interface IRegisterContract {
    interface View {
        void registerSuccess(List<User> userList);
        void registerError(String errorMsg);
    }
    interface Presenter {
        void registerSuccess(List<User> userList);
        void registerError(String errorMsg);
        void register(User user, Context context);
    }
    interface Model {
        void register(User user, Context context);
    }
}
