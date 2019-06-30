package com.feng.p2planchat.client;

import android.util.Log;

import com.feng.p2planchat.util.FileUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/29
 */
public class TransferFileClient extends Thread {
    private static final String TAG = "fzh";

    private Socket mSocket;
    private String mFilePath;
    private OnTransferFileClientListener mListener;
    private boolean stop = false;

    public void setOnTransferFileClientListener(
            OnTransferFileClientListener onTransferFileClientListener) {
        mListener = onTransferFileClientListener;
    }

    public interface OnTransferFileClientListener {
        void currentProcess(int process);
        void fileNameAndLength(String fileName, String fileSize);
        void transferSuccess();
    }

    public TransferFileClient(Socket mSocket, String mFilePath) {
        this.mSocket = mSocket;
        this.mFilePath = mFilePath;
    }

    @Override
    public void run() {
        FileInputStream fis = null;
        DataOutputStream dos = null;

        try {

            File file = new File(mFilePath);
            if(file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(mSocket.getOutputStream());

                // 文件名和长度
                String fileName = file.getName();
                dos.writeUTF(fileName);
                dos.flush();
                long fileLength = file.length();
                dos.writeLong(fileLength);
                dos.flush();
                Log.d(TAG, "client: fileName = " + file.getName());
                Log.d(TAG, "client: fileLength = " + FileUtil.getFormatFileSize(file.length()));
                mListener.fileNameAndLength(fileName, FileUtil.getFormatFileSize(file.length()));

                // 开始传输文件
                byte[] bytes = new byte[1024];
                int length;
                long currLength = 0;
                while(!stop && (length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    currLength += length;
                    int currProcess = (int) (100 * currLength / file.length());
                    mListener.currentProcess(currProcess);
//                    if (currProcess % 5 == 0) {
//                        Log.d(TAG, "client: currProcess = " + currProcess);
//                    }
                }

                if (!stop) {
                    mListener.transferSuccess();
                    Log.d(TAG, "文件发送完毕");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (mSocket != null) {
                    mSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        stop = true;
    }
}
