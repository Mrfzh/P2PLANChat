package com.feng.p2planchat.model;

import android.content.Context;
import android.util.Log;

import com.feng.p2planchat.client.UpdateClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IPersonContract;
import com.feng.p2planchat.entity.serializable.UpdateUser;
import com.feng.p2planchat.util.NetUtil;
import com.feng.p2planchat.util.OtherUserIpUtil;
import com.feng.p2planchat.util.UserUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/19
 */
public class PersonModel implements IPersonContract.Model {

    private static final String TAG = "fzh";

    private IPersonContract.Presenter mPresenter;

    private List<String> mOtherUserIpList = new ArrayList<>();
    private UpdateUser mUpdateInfo;

    public PersonModel(IPersonContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(Context context) {
        //先检测网络
        if (!NetUtil.hasInternet(context)) {
            mPresenter.logoutError("当前没有网络，未能通知其他用户更新信息");
            return;
        }

        //作为客户端，向其他在线用户发出广播
        mOtherUserIpList = OtherUserIpUtil.readFromInternalStorage(context).getOtherUserIpList();
        mUpdateInfo = new UpdateUser(UserUtil.readFromInternalStorage(context).getIpAddress());
        new Thread(new UpdateClientThread()).start();
    }

    class UpdateClientThread implements Runnable {

        @Override
        public void run() {
            try {
                //给每个在线用户发出请求
                for (int i = 0; i < mOtherUserIpList.size(); i++) {

                    Log.d(TAG, "UpdateClientThread: " + mOtherUserIpList.get(i));

                    //注意：如果对方没有打开相应端口，会抛出IOException
                    Socket socket = new Socket(mOtherUserIpList.get(i), Constant.UPDATE_PORT);
                    UpdateClient updateClient = new UpdateClient(socket, mUpdateInfo);
                    new Thread(updateClient).start();
                }
                mPresenter.logoutSuccess();
            } catch (UnknownHostException e) {
                Log.d(TAG, "UnknownHostException : " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "IOException : " + e.getMessage());
            }
        }
    }
}
