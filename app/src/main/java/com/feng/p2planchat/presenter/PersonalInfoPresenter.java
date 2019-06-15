package com.feng.p2planchat.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IPersonalInfoContract;
import com.feng.p2planchat.model.PersonalInfoModel;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class PersonalInfoPresenter extends BasePresenter<IPersonalInfoContract.View>
        implements IPersonalInfoContract.Presenter {

    private IPersonalInfoContract.Model mModel;

    public PersonalInfoPresenter() {
        mModel = new PersonalInfoModel(this);
    }

    @Override
    public void modifyHeadImageSuccess() {
        if (isAttachView()) {
            getMvpView().modifyHeadImageSuccess();
        }
    }

    @Override
    public void modifyHeadImageError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().modifyHeadImageError(errorMsg);
        }
    }

    @Override
    public void modifyHeadImage(List<String> otherUserIpList,
                                String oldName, Bitmap newHeadImage, Context context) {
        mModel.modifyHeadImage(otherUserIpList, oldName, newHeadImage, context);
    }
}
