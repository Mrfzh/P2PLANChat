package com.feng.p2planchat.client;

import com.feng.p2planchat.config.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public class ClientThread implements Runnable {

    private String mIpAddress;
    private ClientThreadListener mClientThreadListener;

    public interface ClientThreadListener {
        void onFinish(String content);  //线程执行完毕后返回数据
    }

    public ClientThread(String mIpAddress) {
        this.mIpAddress = mIpAddress;
    }

    @Override
    public void run() {
        try {
            //向服务器端发送一个请求（不能在主线程中发起请求）
            Socket socket = new Socket(mIpAddress, Constant.PORT);
            //将Socket对应的输入流包装成BufferedReader
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            //读取数据
            StringBuilder builder = new StringBuilder();
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                builder.append(content);
                builder.append("\n");
            }
            //回调消息
            mClientThreadListener.onFinish(builder.toString());
            //关闭输入流和Socket
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClientThreadListener(ClientThreadListener clientThreadListener) {
        mClientThreadListener = clientThreadListener;
    }

}
