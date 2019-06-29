package com.feng.p2planchat.presenter;

import android.content.Context;

import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.contract.IChatContract;
import com.feng.p2planchat.entity.serializable.ChatData;
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
    public void sendTextSuccess(ChatData chatData) {
        if (isAttachView()) {
            getMvpView().sendTextSuccess(chatData);
        }
    }

    @Override
    public void sendTextError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().sendTextError(errorMsg);
        }
    }

    @Override
    public void sendPictureSuccess(ChatData chatData) {
        if (isAttachView()) {
            getMvpView().sendPictureSuccess(chatData);
        }
    }

    @Override
    public void sendPictureError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().sendPictureError(errorMsg);
        }
    }

    @Override
    public void sendText(Context context, String otherIp, ChatData chatData) {
        mModel.sendText(context, otherIp, chatData);
    }

    @Override
    public void sendPicture(Context context, String otherIp, ChatData chatData) {
        mModel.sendPicture(context, otherIp, chatData);
    }
}
