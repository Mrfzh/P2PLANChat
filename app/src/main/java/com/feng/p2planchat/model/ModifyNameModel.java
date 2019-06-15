package com.feng.p2planchat.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.feng.p2planchat.client.UpdateClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IModifyNameContract;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.entity.serializable.UpdateUser;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UpdateNameEvent;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.NetUtil;
import com.feng.p2planchat.util.UserUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public class ModifyNameModel implements IModifyNameContract.Model {

    private static final String TAG = "fzh";

    private IModifyNameContract.Presenter mPresenter;
    private AccountOperation mAccountOperation;

    private UpdateUser mUpdateInfo;
    private Context mContext;
    private List<String> mOtherUserIpList = new ArrayList<>();

    public ModifyNameModel(IModifyNameContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }


    /**
     * 更改自己的用户名
     *
     * @param otherUserIpList 其他在线用户的IP地址
     * @param oldName 旧的用户名
     * @param newName 新的用户名
     * @param context
     */
    @Override
    public void modifyName(List<String> otherUserIpList,
                           String oldName, String newName, Context context) {
        AccountDatabaseHelper helper = new AccountDatabaseHelper(context,
                Constant.DATABASE_ACCOUNT, null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mAccountOperation = new AccountOperation(db);

        //从数据库中删除旧用户名，并添加新用户名
        Log.d(TAG, "pre : database = " + mAccountOperation.showAll());
        String password = mAccountOperation.getPassword(oldName);
        mAccountOperation.deleteByName(oldName);
        mAccountOperation.add(newName, password);
        Log.d(TAG, "after : database = " + mAccountOperation.showAll());

        //更新本地信息
        User user = UserUtil.readFromInternalStorage(context);
        user.setUserName(newName);
        UserUtil.write2InternalStorage(user, context);
        //更新本地图片
        Bitmap bitmap = BitmapUtil.readFromInternalStorage(oldName + ".jpg", context);
        BitmapUtil.save2InternalStorage(bitmap, newName + ".jpg", context);

        //更新个人界面和个人信息界面的用户名
        Event<UpdateNameEvent> updateNameEvent = new Event<>(EventBusCode.UPDATE_NAME,
                new UpdateNameEvent(newName));
        EventBusUtil.sendEvent(updateNameEvent);

        //通知其他在线用户，更新自己的用户名
        notifyOtherUser(otherUserIpList, oldName, newName, context);

        mPresenter.modifyNameSuccess();
    }

    private void notifyOtherUser(List<String> otherUserIpList,
                                 String oldName, String newName, Context context) {
        //先检测网络
        if (!NetUtil.hasInternet(context)) {
            mPresenter.modifyNameError("当前没有网络，未能通知其他用户更新自己的用户名");
            return;
        }

        //作为客户端，向其他在线用户发出广播
        mOtherUserIpList = otherUserIpList;
        mUpdateInfo = new UpdateUser(oldName, newName);
        mContext = context;
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

            } catch (UnknownHostException e) {
                Log.d(TAG, "UnknownHostException : " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "IOException : " + e.getMessage());
            }
        }
    }

}
