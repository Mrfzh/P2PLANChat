package com.feng.p2planchat.service;

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
public class HandleLoginService implements Runnable{

    private Socket mSocket;
    private User mUser;

    private HandleLoginServiceListener mListener;

    public interface HandleLoginServiceListener {
        void getUserInfo(User user);    //回调得到的用户信息
    }

    public void setHandleLoginServiceListener(HandleLoginServiceListener listener) {
        mListener = listener;
    }

    public HandleLoginService(Socket mSocket, User mUser) {
        this.mSocket = mSocket;
        this.mUser = mUser;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = mSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            User user = (User) ois.readObject();    //读取客户端传来的User对象
            mListener.getUserInfo(user);        //回调得到的User对象
            mSocket.shutdownInput();    //关闭服务端Socket的输入流

            os = mSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mUser);     //写入User对象，里面包含自己的用户信息
            mSocket.shutdownOutput();   //关闭服务端Socket的输出流
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
