package com.feng.p2planchat.model;

import android.content.Context;
import android.util.Log;

import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.ILoginContract;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.util.IpAddressUtil;

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

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private int mUserNum = 0;   //在线用户数
    private boolean isFinish = false;

    public LoginModel(ILoginContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 进行登录操作
     *
     * @param name 用户名
     * @param password 密码
     */
    @Override
    public void login(String name, String password, Context context) {
        //作为客户端，向其他在线用户发出广播
        mContext = context;
        Thread thread = new Thread(new LoginClientThread());
        thread.start();

        while (!isFinish) {
            //循环，等待线程结束
        }

        if (mUserList.size() != 0) {
            mPresenter.loginSuccess(mUserList);
        } else {
            mPresenter.loginError("当前没有其他在线用户");
        }
    }

    class LoginClientThread implements Runnable {

        @Override
        public void run() {
            try {
                //得到其他在线用户的ip地址
                List<String> ipAddressList = IpAddressUtil.getIpAddressList(mContext);
                //自己的用户信息
                User user = new User(IpAddressUtil.getIpAddress(mContext),
                        "用户 " + System.currentTimeMillis());
                //给每个在线用户发出请求
                for (int i = 0; i < ipAddressList.size(); i++) {
                    //注意：如果对方没有打开相应端口，会抛出IOException
                    Socket socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
                    mUserNum++;
                    LoginClient loginClient = new LoginClient(socket, user);
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
                }
                isFinish = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d(TAG, "UnknownHostException : " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException : " + e.getMessage());
            }
        }
    }
}
