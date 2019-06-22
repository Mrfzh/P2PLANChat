package com.feng.p2planchat.client;

import com.feng.p2planchat.entity.serializable.ChatData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public class ChatClient implements Runnable {

    private static final String TAG = "fzh";

    private Socket mSocket;
    private ChatData mChatData;

    public ChatClient(Socket mSocket, ChatData mChatData) {
        this.mSocket = mSocket;
        this.mChatData = mChatData;
    }

    @Override
    public void run() {
        OutputStream os = null;
        try {
            os = mSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mChatData);
            mSocket.shutdownOutput();   //关闭客户端Socket的输出流
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mSocket != null) {
                    mSocket.close();
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
