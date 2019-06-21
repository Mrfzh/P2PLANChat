package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IChatContract;
import com.feng.p2planchat.model.ChatModel;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public class ChatPresenter extends BasePresenter<IChatContract.View>
        implements IChatContract.Presenter {

    private IChatContract.Model mModel;

    public ChatPresenter() {
        mModel = new ChatModel(this);
    }

    @Override
    public void sendTextSuccess() {
        if (isAttachView()) {
            getMvpView().sendTextSuccess();
        }
    }

    @Override
    public void sendTextError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().sendTextError(errorMsg);
        }
    }

    @Override
    public void sendText(Context context, String otherIp, String content) {
        mModel.sendText(context, otherIp, content);
    }
}
