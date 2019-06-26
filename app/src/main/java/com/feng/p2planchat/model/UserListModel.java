package com.feng.p2planchat.model;

import android.content.Context;
import android.util.Log;

import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IUserListContract;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.NetUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserListModel implements IUserListContract.Model {

    private static final String TAG = "fzh";

    private IUserListContract.Presenter mPresenter;

    private Context mContext;
    private List<User> mUserList = new ArrayList<>();   //用户列表
    private User mOwnInfo;          //自己的用户信息

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private int mUserNum = 0;   //在线用户数
    private boolean isFinish = false;

    public UserListModel(IUserListContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 查找其他在线用户
     *
     * @param user 自己的用户信息
     * @param context
     */
    @Override
    public void findOtherUser(User user, Context context) {
        //先检测网络
        if (!NetUtil.hasInternet(context)) {
            mPresenter.findOtherUserError("当前没有网络，请连接网络后重试");
            return;
        }

        //作为客户端，向其他在线用户发出广播
        mOwnInfo = user;
        mContext = context;
        Thread thread = new Thread(new LoginClientThread());
        thread.start();

        while (!isFinish) {
            //循环，等待线程结束
            Log.d(TAG, "findOtherUser: run 1");
        }

        mPresenter.findOtherUserSuccess(mUserList);
        mUserList = new ArrayList<>();      //清除信息
    }

    class LoginClientThread implements Runnable {

        @Override
        public void run() {
            try {
                //得到其他在线用户的ip地址
                List<String> ipAddressList = IpAddressUtil.getIpAddressList(mContext);

                //给每个在线用户发出请求
                for (int i = 0; i < ipAddressList.size(); i++) {
                    //注意：如果对方没有打开相应端口，会抛出IOException
                    Socket socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
                    mUserNum++;
                    LoginClient loginClient = new LoginClient(socket, mOwnInfo);
                    loginClient.setLoginClientListener(new LoginClient.LoginClientListener() {
                        @Override
                        public void getUserInfo(User user) {
                            mUserList.add(user);
                            mAtomicInteger.incrementAndGet();
                        }
                    });
                    new Thread(loginClient).start();
                }
                while (mUserNum != 0 && mAtomicInteger.get() < mUserNum) {
                    //循环，等待线程结束
                    Log.d(TAG, "run: run 2");
                }
                isFinish = true;
            } catch (UnknownHostException e) {
                isFinish = true;
            } catch (IOException e) {
                isFinish = true;
            }
        }
    }
}
