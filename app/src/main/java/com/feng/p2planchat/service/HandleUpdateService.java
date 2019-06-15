package com.feng.p2planchat.service;

import com.feng.p2planchat.entity.serializable.UpdateUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class HandleUpdateService implements Runnable {

    private Socket mSocket;

    private HandleUpdateServiceListener mListener;

    public interface HandleUpdateServiceListener {
        void getUpdateUserInfo(UpdateUser updateUser);    //回调得到的用户更新信息
    }

    public void setHandleUpdateServiceListener(HandleUpdateServiceListener
                                                       handleUpdateServiceListener) {
        mListener = handleUpdateServiceListener;
    }

    public HandleUpdateService(Socket mSocket) {
        this.mSocket = mSocket;
    }

    @Override
    public void run() {
        InputStream is = null;
        try {
            is = mSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            UpdateUser updateUser = (UpdateUser) ois.readObject();
            mListener.getUpdateUserInfo(updateUser);
            mSocket.shutdownInput();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
