package com.feng.p2planchat.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.feng.p2planchat.client.ChatClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IChatContract;
import com.feng.p2planchat.entity.serializable.ChatData;
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

    private ChatData mChatData;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SEND_SUCCESS:
                    mPresenter.sendTextSuccess(mChatData);
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
     */
    @Override
    public void sendText(Context context, String otherIp, ChatData chatData) {
        if (chatData == null) {
            mPresenter.sendTextError("发送失败");
        }
        mChatData = chatData;

        if (!NetUtil.hasInternet(context)) {
            mPresenter.sendTextError("当前没有网络");
        }

        //发送文字消息
        new Thread(new ChatClientThread(otherIp, chatData)).start();
    }

    /**
     * 发送图片
     */
    @Override
    public void sendPicture(Context context, String otherIp, ChatData chatData) {
        if (chatData == null) {
            mPresenter.sendPictureError("发送失败");
        }
        mChatData = chatData;

        if (!NetUtil.hasInternet(context)) {
            mPresenter.sendTextError("当前没有网络");
        }

        //发送图片
        new Thread(new ChatClientThread(otherIp, chatData)).start();
    }

    class ChatClientThread implements Runnable {

        private String otherIp;     //对方的IP地址
        private ChatData chatData;     //发送的消息

        public ChatClientThread(String otherIp, ChatData chatData) {
            this.otherIp = otherIp;
            this.chatData = chatData;
        }

        @Override
        public void run() {
            try {
                Log.d(TAG, "run: otherIp = " + otherIp);
                //注意：如果对方没有打开相应端口，会抛出IOException
                Socket socket = new Socket(otherIp, Constant.CHAT_PORT);

                ChatClient chatClient = new ChatClient(socket, chatData);
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
