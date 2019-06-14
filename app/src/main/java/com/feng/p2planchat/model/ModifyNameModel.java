package com.feng.p2planchat.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IModifyNameContract;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UpdateNameEvent;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.UserUtil;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public class ModifyNameModel implements IModifyNameContract.Model {

    private static final String TAG = "fzh";

    private IModifyNameContract.Presenter mPresenter;
    private AccountOperation mAccountOperation;

    public ModifyNameModel(IModifyNameContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }


    /**
     * 更改自己的用户名
     *
     * @param oldName 旧的用户名
     * @param newName 新的用户名
     * @param context
     */
    @Override
    public void modifyName(String oldName, String newName, Context context) {
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

        //更新个人界面和个人信息界面的用户名
        Event<UpdateNameEvent> updateNameEvent = new Event<>(EventBusCode.MODIFY_NAME_2_UPDATE_NAME,
                new UpdateNameEvent(newName));
        EventBusUtil.sendEvent(updateNameEvent);

        //通知其他在线用户，更新自己的用户名

        mPresenter.modifyNameSuccess();
    }
}
