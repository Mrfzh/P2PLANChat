package com.feng.p2planchat.contract;

import android.content.Context;

import com.feng.p2planchat.entity.bean.User;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public interface IUserListContract {
    interface View {
        void findOtherUserSuccess(List<User> userList);
        void findOtherUserError(String errorMsg);
    }
    interface Presenter {
        void findOtherUserSuccess(List<User> userList);
        void findOtherUserError(String errorMsg);
        void findOtherUser(User user, Context context);
    }
    interface Model {
        void findOtherUser(User user, Context context);
    }
}
