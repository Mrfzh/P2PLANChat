package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IPersonContract;
import com.feng.p2planchat.model.PersonModel;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/19
 */
public class PersonPresenter extends BasePresenter<IPersonContract.View>
        implements IPersonContract.Presenter {

    private IPersonContract.Model mModel;

    public PersonPresenter() {
        mModel = new PersonModel(this);
    }

    @Override
    public void logoutSuccess() {
        if (isAttachView()) {
            getMvpView().logoutSuccess();
        }
    }

    @Override
    public void logoutError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().logoutError(errorMsg);
        }
    }

    @Override
    public void logout(Context context) {
        mModel.logout(context);
    }

}
