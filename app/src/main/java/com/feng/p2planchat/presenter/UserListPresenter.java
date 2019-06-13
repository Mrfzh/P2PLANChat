package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IUserListContract;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.model.UserListModel;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserListPresenter extends BasePresenter<IUserListContract.View>
        implements IUserListContract.Presenter {

    private IUserListContract.Model mModel;

    public UserListPresenter() {
        mModel = new UserListModel(this);
    }

    @Override
    public void findOtherUserSuccess(List<User> userList) {
        if (isAttachView()) {
            getMvpView().findOtherUserSuccess(userList);
        }
    }

    @Override
    public void findOtherUserError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().findOtherUserError(errorMsg);
        }
    }

    @Override
    public void findOtherUser(User user, Context context) {
        mModel.findOtherUser(user, context);
    }
}
