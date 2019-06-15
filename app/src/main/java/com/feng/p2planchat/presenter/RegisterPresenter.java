package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IRegisterContract;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.model.RegisterModel;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/12
 */
public class RegisterPresenter extends BasePresenter<IRegisterContract.View>
        implements IRegisterContract.Presenter{

    private IRegisterContract.Model mModel;

    public RegisterPresenter() {
        mModel = new RegisterModel(this);
    }

    @Override
    public void registerSuccess(List<User> userList) {
        if (isAttachView()) {
            getMvpView().registerSuccess(userList);
        }
    }

    @Override
    public void registerError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().registerError(errorMsg);
        }
    }

    @Override
    public void register(User user, Context context) {
        mModel.register(user, context);
    }
}
