package com.feng.p2planchat.model;

import android.content.Context;
import android.util.Log;

import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IRegisterContract;
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
 * Created on 2019/6/12
 */
public class RegisterModel implements IRegisterContract.Model{

    private static final String TAG = "fzh";

    private IRegisterContract.Presenter mPresenter;
    private Context mContext;

    private User mOwnInfo;      //自己的用户信息
    private List<User> mUserList = new ArrayList<>();   //用户列表

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private int mUserNum = 0;   //在线用户数
    private boolean isFinish = false;

    public RegisterModel(IRegisterContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 注册
     *
     * @param user 自己的用户信息
     * @param context
     */
    @Override
    public void register(User user, Context context) {
        //先检测网络
        if (!NetUtil.hasInternet(context)) {
            mPresenter.registerError("当前没有网络，请连接网络后重试");
            return;
        }

        //作为客户端，向其他在线用户发出广播
        mOwnInfo = user;
        mContext = context;
        Thread thread = new Thread(new LoginClientThread());
        thread.start();

        //这里需要设定一个超时时间（因为一些未知原因，可能会一直循环）
        long startTime = System.currentTimeMillis();
        while (!isFinish && ((System.currentTimeMillis() - startTime) <= 15 * 1000)) {

        }

        if (!isFinish) {
            mPresenter.registerError("网络请求超时，请重新尝试");
        } else {
            mPresenter.registerSuccess(mUserList);
        }


    }

    class LoginClientThread implements Runnable {

        @Override
        public void run() {
//            try {
//                //得到其他在线用户的ip地址
//                List<String> ipAddressList = IpAddressUtil.getIpAddressList(mContext);
//
//                //给每个在线用户发出请求
//                for (int i = 0; i < ipAddressList.size(); i++) {
//                    //注意：如果对方没有打开相应端口，会抛出IOException
//                    Socket socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
//                    mUserNum++;
//                    LoginClient loginClient = new LoginClient(socket, mOwnInfo);
//                    loginClient.setLoginClientListener(new LoginClient.LoginClientListener() {
//                        @Override
//                        public void getUserInfo(User user) {
//                            mUserList.add(user);
//                            mAtomicInteger.incrementAndGet();
//                        }
//                    });
//                    new Thread(loginClient).start();
//                }
//                while (mUserNum != 0 && mAtomicInteger.get() < mUserNum) {
//                    //循环，等待线程结束
//                }
//                isFinish = true;
//            } catch (UnknownHostException e) {
//                isFinish = true;
//                e.printStackTrace();
//                Log.d(TAG, "UnknownHostException : " + e.getMessage());
//            } catch (IOException e) {
//                isFinish = true;
//                e.printStackTrace();
//                Log.d(TAG, "IOException : " + e.getMessage());
//            }


            //得到其他在线用户的ip地址
            List<String> ipAddressList = IpAddressUtil.getIpAddressList(mContext);

            //给每个在线用户发出请求
            for (int i = 0; i < ipAddressList.size(); i++) {
                //注意：如果对方没有打开相应端口，会抛出IOException
                Socket socket = null;
                try {
                    socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.d(TAG, "UnknownHostException : " + e.getMessage());
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "IOException : " + e.getMessage());
                    continue;
                }
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
            }
            isFinish = true;

        }
    }
}
