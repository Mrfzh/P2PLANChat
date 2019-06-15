package com.feng.p2planchat.client;

import com.feng.p2planchat.entity.serializable.UpdateUser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateClient implements Runnable {

    private static final String TAG = "fzh";
    private Socket mSocket;
    private UpdateUser mUpdateUser;

    public UpdateClient(Socket mSocket, UpdateUser mUpdateUser) {
        this.mSocket = mSocket;
        this.mUpdateUser = mUpdateUser;
    }

    @Override
    public void run() {
        OutputStream os = null;
        try {
            os = mSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mUpdateUser);
            mSocket.shutdownOutput();
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
