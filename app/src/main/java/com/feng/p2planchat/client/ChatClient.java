package com.feng.p2planchat.client;

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
    private String mContent;

    public ChatClient(Socket mSocket, String mContent) {
        this.mSocket = mSocket;
        this.mContent = mContent;
    }

    @Override
    public void run() {
        OutputStream os = null;
        try {
            os = mSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mContent);
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
