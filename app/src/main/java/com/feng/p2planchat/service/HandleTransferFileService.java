package com.feng.p2planchat.service;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.util.FileUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/29
 */
public class HandleTransferFileService implements Runnable{
    public static final String TAG = "fzh";
//    private static final int MESSAGE_SEND_FILE_NAME_AND_LENGTH = 0;
//    private static final int MESSAGE_UPDATE_PROCESS = 1;

    private Socket mSocket;
    private OnHandleTransferFileServiceListener mListener;

//    private String fileName = "";
//    private long fileLength = 0;
//    private int currProcess = 0;

//    private Handler mHandler;

    public void setOnHandleTransferFileServiceListener(
            OnHandleTransferFileServiceListener onHandleTransferFileServiceListener) {
        mListener = onHandleTransferFileServiceListener;
    }

    public interface OnHandleTransferFileServiceListener {
//        void currentProcess(int process);
//        void fileNameAndLength(String fileName, String fileSize);
        void finish(String fileName, String fileSize);
    }

    public HandleTransferFileService(Socket mSocket) {
        this.mSocket = mSocket;

//        Looper.prepare();//Looper初始化
//        mHandler = new Handler(Looper.myLooper()){
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what){
//                    case MESSAGE_SEND_FILE_NAME_AND_LENGTH:
//                        mListener.fileNameAndLength(fileName, FileUtil.getFormatFileSize(fileLength));
//                        break;
//                    case MESSAGE_UPDATE_PROCESS:
//                        mListener.currentProcess(currProcess);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
    }

    @Override
    public void run() {

        DataInputStream dis = null;
        FileOutputStream fos = null;
        try {
            dis = new DataInputStream(mSocket.getInputStream());

            // 文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            Log.d(TAG, "run: fileName = " + fileName);
            Log.d(TAG, "run: fileLength = " + FileUtil.getFormatFileSize(fileLength));
//            mListener.fileNameAndLength(fileName, FileUtil.getFormatFileSize(fileLength));

            //文件保存路径
            String path = Environment.getExternalStorageDirectory().getPath() + "/" +
                    Constant.LOCAL_FILE_FOLDER_NAME;
            Log.d(TAG, "service: path = " + path);
            File directory = new File(path);
            if(!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
            fos = new FileOutputStream(file);

            // 开始接收文件
            byte[] bytes = new byte[1024];
            int length;
            long currLength = 0;
            while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
                currLength += length;
                int currProcess = (int) (100 * currLength / fileLength);
//                mListener.currentProcess(currProcess);
                Log.d(TAG, "service: currProcess = " + currProcess);
            }

            if (currLength == fileLength) {
                Log.d(TAG, "run: 文件接收完毕");
                mListener.finish(fileName, FileUtil.getFormatFileSize(fileLength));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (dis != null) {
                    dis.close();
                }
                if (mSocket != null) {
                    mSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
