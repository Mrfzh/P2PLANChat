package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.ILoginContract;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.model.LoginModel;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class LoginPresenter extends BasePresenter<ILoginContract.View>
        implements ILoginContract.Presenter{

    private ILoginContract.Model mModel;

    public LoginPresenter() {
        mModel = new LoginModel(this);
    }

    @Override
    public void loginSuccess(List<User> userList) {
        if (isAttachView()) {
            getMvpView().loginSuccess(userList);
        }
    }

    @Override
    public void loginError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().loginError(errorMsg);
        }
    }

    @Override
    public void login(String name, String password, Context context) {
        mModel.login(name, password, context);
    }
}
