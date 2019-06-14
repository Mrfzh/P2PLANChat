package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IModifyNameContract;
import com.feng.p2planchat.model.ModifyNameModel;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public class ModifyNamePresenter extends BasePresenter<IModifyNameContract.View>
        implements IModifyNameContract.Presenter {

    private IModifyNameContract.Model mModel;

    public ModifyNamePresenter() {
        mModel = new ModifyNameModel(this);
    }

    @Override
    public void modifyNameSuccess() {
        if (isAttachView()) {
            getMvpView().modifyNameSuccess();
        }
    }

    @Override
    public void modifyNameError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().modifyNameError(errorMsg);
        }
    }

    @Override
    public void modifyName(String oldName, String newName, Context context) {
        mModel.modifyName(oldName, newName, context);
    }
}
