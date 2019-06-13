package com.feng.p2planchat.client;

import android.util.Log;

import com.feng.p2planchat.entity.bean.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/10
 */
public class LoginClient implements Runnable {

    private static final String TAG = "fzh";
    private Socket mSocket;
    private User mUser;

    private LoginClientListener mListener;

    public interface LoginClientListener {
        void getUserInfo(User user);
    }

    public void setLoginClientListener(LoginClientListener listener) {
        mListener = listener;
    }

    public LoginClient(Socket mSocket, User mUser) {
        this.mSocket = mSocket;
        this.mUser = mUser;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        try {
//            Log.d(TAG, "run: run 1");
            os = mSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mUser);     //写入User对象，里面包含自己的用户信息
            mSocket.shutdownOutput();   //关闭客户端Socket的输出流

            is = mSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
//            Log.d(TAG, "run: run 2");
            User user = (User) ois.readObject();    //读取服务端传来的User对象
            mListener.getUserInfo(user);        //回调得到的User对象
            mSocket.shutdownInput();        //关闭客户端Socket的输入流
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mSocket != null) {
                    mSocket.close();
                }
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
