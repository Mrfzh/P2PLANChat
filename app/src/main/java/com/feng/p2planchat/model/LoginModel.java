package com.feng.p2planchat.model;

import android.content.Context;
import android.util.Log;

import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.ILoginContract;
import com.feng.p2planchat.entity.bean.User;
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
 * Created on 2019/6/11
 */
public class LoginModel implements ILoginContract.Model {

    private static final String TAG = "fzh";

    private ILoginContract.Presenter mPresenter;
    private Context mContext;

    private List<User> mUserList = new ArrayList<>();   //用户列表
    private User mOwnInfo;          //自己的用户信息

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private int mUserNum = 0;   //在线用户数
    private boolean isFinish = false;

    public LoginModel(ILoginContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 登录
     *
     * @param user 自己的用户信息
     * @param context
     */
    @Override
    public void login(User user, Context context) {
        //先检测网络
        if (!NetUtil.hasInternet(context)) {
            mPresenter.loginError("当前没有网络，请连接网络后重试");
            return;
        }

        Log.d(TAG, "login: run 1");

        //作为客户端，向其他在线用户发出广播
        mOwnInfo = user;
        mContext = context;
        Thread thread = new Thread(new LoginClientThread());
        thread.start();

        while (!isFinish) {
            //循环，等待线程结束
        }

        mPresenter.loginSuccess(mUserList);
    }

    class LoginClientThread implements Runnable {

        @Override
        public void run() {
            try {
                //得到其他在线用户的ip地址
                List<String> ipAddressList = IpAddressUtil.getIpAddressList(mContext);

                //给每个在线用户发出请求
                for (int i = 0; i < ipAddressList.size(); i++) {
                    Log.d(TAG, "LoginClientThread:run run");

                    //注意：如果对方没有打开相应端口，会抛出IOException
                    Socket socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
                    mUserNum++;
                    LoginClient loginClient = new LoginClient(socket, mOwnInfo);
                    loginClient.setLoginClientListener(new LoginClient.LoginClientListener() {
                        @Override
                        public void getUserInfo(User user) {
                            //这里没有回调？？？

                            Log.d(TAG, "getUserInfo: run");
                            Log.d(TAG, "getUserInfo: user = " + user.show());
                            mUserList.add(user);
                            mAtomicInteger.incrementAndGet();
                        }
                    });
                    new Thread(loginClient).start();
                }
                while (mUserNum != 0 && mAtomicInteger.get() < mUserNum) {
                    //循环，等待线程结束
                }
                isFinish = true;
            } catch (UnknownHostException e) {
                isFinish = true;
                e.printStackTrace();
                Log.d(TAG, "UnknownHostException : " + e.getMessage());
            } catch (IOException e) {
                isFinish = true;
                e.printStackTrace();
                Log.d(TAG, "IOException : " + e.getMessage());
            }
        }
    }
}
