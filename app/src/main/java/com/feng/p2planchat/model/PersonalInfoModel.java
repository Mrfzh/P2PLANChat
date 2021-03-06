package com.feng.p2planchat.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.feng.p2planchat.client.UpdateClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IPersonalInfoContract;
import com.feng.p2planchat.entity.serializable.UpdateUser;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.NetUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class PersonalInfoModel implements IPersonalInfoContract.Model {

    private static final String TAG = "PersonalInfoModel";
    private IPersonalInfoContract.Presenter mPresenter;

    private UpdateUser mUpdateInfo;
    private List<String> mOtherUserIpList = new ArrayList<>();

    public PersonalInfoModel(IPersonalInfoContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 更新头像
     *
     * @param otherUserIpList
     * @param ip
     * @param newHeadImage
     * @param context
     */
    @Override
    public void modifyHeadImage(List<String> otherUserIpList,
                                String ip, Bitmap newHeadImage, Context context) {
        //先检测网络
        if (!NetUtil.hasInternet(context)) {
            mPresenter.modifyHeadImageError("当前没有网络，未能通知其他用户更新自己的头像");
            return;
        }

        //作为客户端，向其他在线用户发出广播
        mOtherUserIpList = otherUserIpList;
        mUpdateInfo = new UpdateUser(ip, BitmapUtil.bitmap2ByteArray(newHeadImage));
        new Thread(new UpdateClientThread()).start();

        mPresenter.modifyHeadImageSuccess();
    }

    class UpdateClientThread implements Runnable {

        @Override
        public void run() {
//            try {
//                //给每个在线用户发出请求
//                for (int i = 0; i < mOtherUserIpList.size(); i++) {
//
//                    Log.d(TAG, "UpdateClientThread: " + mOtherUserIpList.get(i));
//
//                    //注意：如果对方没有打开相应端口，会抛出IOException
//                    Socket socket = new Socket(mOtherUserIpList.get(i), Constant.UPDATE_PORT);
//                    UpdateClient updateClient = new UpdateClient(socket, mUpdateInfo);
//                    new Thread(updateClient).start();
//                }
//
//            } catch (UnknownHostException e) {
//                Log.d(TAG, "UnknownHostException : " + e.getMessage());
//            } catch (IOException e) {
//                Log.d(TAG, "IOException : " + e.getMessage());
//            }


            //给每个在线用户发出请求
            for (int i = 0; i < mOtherUserIpList.size(); i++) {

                Log.d(TAG, "UpdateClientThread: " + mOtherUserIpList.get(i));

                //注意：如果对方没有打开相应端口，会抛出IOException
                Socket socket = null;
                try {
                    socket = new Socket(mOtherUserIpList.get(i), Constant.UPDATE_PORT);
                } catch (UnknownHostException e) {
                    Log.d(TAG, "UnknownHostException : " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    Log.d(TAG, "IOException : " + e.getMessage());
                    continue;
                }
                UpdateClient updateClient = new UpdateClient(socket, mUpdateInfo);
                new Thread(updateClient).start();
            }

        }
    }
}
