package com.feng.p2planchat.view.test;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.service.HandleLoginService;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.NetUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";
    private static final int UPDATE_USER_LIST = 3;

    private TextView mContentTv;
    private Button mGetIpBtn;
    private Button mCheckNetBtn;
    private Button mReachBtn;
    private Button mGetOtherUserInfoBtn;
    private Button mSendInfoBtn;

    private List<User> mUserList = new ArrayList<>();   //用户列表

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER_LIST:
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < mUserList.size(); i++) {
                        User curr = mUserList.get(i);
                        builder.append("用户名：");
                        builder.append(curr.getUserName());
                        builder.append(", 用户IP地址：");
                        builder.append(curr.getIpAddress());
                        builder.append("\n");
                    }
                    mContentTv.setText(builder.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mContentTv = findViewById(R.id.tv_test_content);
        mGetIpBtn = findViewById(R.id.btn_test_get_ip);
        mGetIpBtn.setOnClickListener(this);
        mCheckNetBtn = findViewById(R.id.btn_test_check_net);
        mCheckNetBtn.setOnClickListener(this);
        mReachBtn = findViewById(R.id.btn_test_is_reach);
        mReachBtn.setOnClickListener(this);
        mGetOtherUserInfoBtn = findViewById(R.id.btn_test_get_other_user_info);
        mGetOtherUserInfoBtn.setOnClickListener(this);
        mSendInfoBtn = findViewById(R.id.btn_test_send_user_info);
        mSendInfoBtn.setOnClickListener(this);
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_get_ip:
                //获取本机ip地址
                String ipAddr = IpAddressUtil.getIpAddress(TestActivity.this);
                //获取ip地址的前缀
                String prefix = ipAddr.substring(0, ipAddr.lastIndexOf(".") + 1);
                mContentTv.setText("ip地址：" + ipAddr + "\n" + "ip前缀：" + prefix);
                break;
            case R.id.btn_test_check_net:
                if (NetUtil.hasInternet(this)) {
                    mContentTv.setText("网络畅通");
                } else {
                    mContentTv.setText("没有连接网络");
                }
                break;
            case R.id.btn_test_is_reach:
                //判断当前局域网哪些ip可达
                List<String> list = getIpAddressList();
                mContentTv.setText(list.toString());
                break;
            case R.id.btn_test_get_other_user_info:
                new Thread(new LoginServiceThread()).start();
                break;
            case R.id.btn_test_send_user_info:
                new Thread(new LoginClientThread()).start();
                break;
            default:
                break;
        }
    }

    class LoginServiceThread implements Runnable {

        @Override
        public void run() {
            getOtherUserInfo();
        }
    }

    /**
     * 实现服务端的监听：获取其他在线用户的信息
     */
    private void getOtherUserInfo() {
        try {
            //先作为服务端，得到其他用户的信息
            ServerSocket userServerSocket = new ServerSocket(Constant.USER_PORT);
            //自己的用户信息
            User user = new User(IpAddressUtil.getIpAddress(this),
                    "用户 " + System.currentTimeMillis());
            //一直监听客户端
            while (true) {
                Socket socket = userServerSocket.accept();
                //处理客户端的请求
                HandleLoginService service = new HandleLoginService(socket, user);
                service.setHandleLoginServiceListener(new HandleLoginService.HandleLoginServiceListener() {
                    @Override
                    public void getUserInfo(User user) {
                        mUserList.add(user);
                        Message message = new Message();
                        message.what = UPDATE_USER_LIST;
                        mHandler.sendMessage(message);
                    }
                });
                new Thread(service).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class LoginClientThread implements Runnable {

        @Override
        public void run() {
            sendInfo();
        }
    }

    /**
     * 发送自己的用户信息给其他用户
     */
    private void sendInfo() {
        try {
            //得到其他在线用户的ip地址
            List<String> ipAddressList = getIpAddressList();
            //自己的用户信息
            User user = new User(IpAddressUtil.getIpAddress(this),
                    "用户 " + System.currentTimeMillis());
            //给每个在线用户发出请求
            for (int i = 0; i < ipAddressList.size(); i++) {
                Socket socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
                LoginClient loginClient = new LoginClient(socket, user);
                loginClient.setLoginClientListener(new LoginClient.LoginClientListener() {
                    @Override
                    public void getUserInfo(User user) {
                        mUserList.add(user);
                        Message message = new Message();
                        message.what = UPDATE_USER_LIST;
                        mHandler.sendMessage(message);
                    }
                });
                new Thread(loginClient).start();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取其他在线用户的ip地址
     *
     * @return
     */
    private List<String> getIpAddressList() {
        final List<String> ipAddressList = new ArrayList<>();

        final String ipAddr = IpAddressUtil.getIpAddress(TestActivity.this);
        final String prefix = ipAddr.substring(0, ipAddr.lastIndexOf(".") + 1);
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        //创建256个线程分别去ping
        for ( int i = 0; i < 256; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                public void run() {
                    //利用ping命令判断
                    String current_ip = prefix + finalI;
                    //要执行的ping命令，其中 -c 为发送的次数，-w 表示发送后等待响应的时间
                    String ping = "ping -c 1 -w 5 " + current_ip;
                    Process proc = null;
                    try {
                        proc = Runtime.getRuntime().exec(ping);
                        int result = proc.waitFor();
                        if (result == 0 && !current_ip.equals(ipAddr)) {
                            ipAddressList.add(current_ip);
                            Log.d(TAG, current_ip + ": true");
                        }
                        atomicInteger.incrementAndGet();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    } finally {
                        assert proc != null;
                        proc.destroy();
                    }
                }
            }).start();
        }

        while (atomicInteger.get() < 256) {

        }

        return ipAddressList;
    }

}
