package com.feng.p2planchat.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.feng.p2planchat.client.ChatClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IChatContract;
import com.feng.p2planchat.util.NetUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/21
 */
public class ChatModel implements IChatContract.Model {

    public static final String TAG = "fzh";
    private static final int MESSAGE_SEND_SUCCESS = 0;
    private static final int MESSAGE_SEND_ERROR = 1;

    private IChatContract.Presenter mPresenter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SEND_SUCCESS:
                    mPresenter.sendTextSuccess();
                    break;
                case MESSAGE_SEND_ERROR:
                    mPresenter.sendTextError("发送失败");
                    break;
                default:
                    break;
            }
        }
    };

    public ChatModel(IChatContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 发送文字消息
     *
     * @param otherIp 对方的IP地址
     * @param content 发送的消息
     */
    @Override
    public void sendText(Context context, String otherIp, String content) {
        if (!NetUtil.hasInternet(context)) {
            mPresenter.sendTextError("当前没有网络");
        }

        //发送消息
        new Thread(new ChatClientThread(otherIp, content)).start();
    }

    class ChatClientThread implements Runnable {

        private String otherIp;     //对方的IP地址
        private String content;     //发送的消息

        public ChatClientThread(String otherIp, String content) {
            this.otherIp = otherIp;
            this.content = content;
        }

        @Override
        public void run() {
            try {
                //注意：如果对方没有打开相应端口，会抛出IOException
                Socket socket = new Socket(otherIp, Constant.CHAT_PORT);

                ChatClient chatClient = new ChatClient(socket, content);
                new Thread(chatClient).start();

                mHandler.obtainMessage(MESSAGE_SEND_SUCCESS).sendToTarget();

            } catch (UnknownHostException e) {
                Log.d(TAG, "UnknownHostException : " + e.getMessage());
                mHandler.obtainMessage(MESSAGE_SEND_ERROR).sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "IOException : " + e.getMessage());
                mHandler.obtainMessage(MESSAGE_SEND_ERROR).sendToTarget();
            }
        }
    }
}
