package com.feng.p2planchat.service;

import android.os.Environment;
import android.util.Log;

import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.entity.serializable.ChatData;
import com.feng.p2planchat.util.FileUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public class HandleChatService implements Runnable{

    public static final String TAG = "fzh";

    private Socket mSocket;

    private HandleChatServiceListener mListener;

    public interface HandleChatServiceListener {
        void getMessage(ChatData chatData);    //回调得到的消息
    }

    public void setHandleChatServiceListener(HandleChatServiceListener listener) {
        mListener = listener;
    }

    public HandleChatService(Socket mSocket) {
        this.mSocket = mSocket;
    }

    @Override
    public void run() {
        InputStream is = null;
        try {
            is = mSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            ChatData chatData = (ChatData) ois.readObject();
            mListener.getMessage(chatData);
            mSocket.shutdownInput();    //关闭服务端Socket的输入流
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

