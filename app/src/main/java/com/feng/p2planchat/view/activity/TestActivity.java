package com.feng.p2planchat.view.activity;

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
import com.feng.p2planchat.client.ClientThread;
import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.service.HandleLoginService;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.NetUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TestActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";
    private static final int UPDATE_TEXT = 1;
    private static final int REACH_IP = 2;
    private static final int UPDATE_USER_LIST = 3;


    private Button mRequestBtn;
    private TextView mContentTv;
    private Button mGetIpBtn;
    private Button mCheckNetBtn;
    private Button mReachBtn;
    private Button mGetOtherUserInfoBtn;
    private Button mSendInfoBtn;

    private String mContent;
    private String mReachIp;
    private List<User> mUserList = new ArrayList<>();   //用户列表

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    mContentTv.setText(mContent);
                    break;
                case REACH_IP:
                    mContentTv.setText(mReachIp);
                    break;
                case UPDATE_USER_LIST:
                    Log.d(TAG, "handleMessage: UPDATE_USER_LIST");
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
        mRequestBtn = findViewById(R.id.btn_test_request);
        mRequestBtn.setOnClickListener(this);
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
            case R.id.btn_test_request:
                request();
                break;
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
//                scanIp();
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

    /**
     * 向服务器端发起请求
     */
    private void request() {
        ClientThread clientThread = new ClientThread("10.1.1.109");
        clientThread.setClientThreadListener(new ClientThread.ClientThreadListener() {
            @Override
            public void onFinish(String content) {
                //存下服务器发送的消息
                mContent = content; 
                //回到主线程更新UI
                Message message = new Message();
                message.what = UPDATE_TEXT;
                mHandler.sendMessage(message);
            }
        });
        new Thread(clientThread).start();
    }

    /**
     * 扫描当前局域网，获取当前局域网中的其他设备
     */
    private void scanIp() {

        String ipAddr = IpAddressUtil.getIpAddress(TestActivity.this);
        final String prefix = ipAddr.substring(0, ipAddr.lastIndexOf(".") + 1);

        //创建256个线程分别去ping
        for ( int i = 0; i < 256; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                public void run() {
                    //利用ping命令判断
                    String current_ip = prefix + finalI;
                    //要执行的ping命令，其中 -c 为发送的次数，-w 表示发送后等待响应的时间
                    String ping = "ping -c 1 -w 1 " + current_ip;
                    Process proc = null;
                    try {
                        proc = Runtime.getRuntime().exec(ping);
                        int result = proc.waitFor();
                        if (result == 0) {
                            Log.d(TAG, current_ip + ": true");
                        } else {
                            Log.d(TAG, current_ip + ": false");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    } finally {
                        assert proc != null;
                        proc.destroy();
                    }

                    //利用InetAddress的isReachable方法判断
//                    try {
//                        String testIp = "10.41.29." + finalI;
//                        InetAddress address = InetAddress.getByName(testIp);
//                        boolean reachable = address.isReachable(1000);
//                        Log.d(TAG, testIp + ": " + reachable);
//                    } catch (UnknownHostException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }).start();
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
                    "用户 " + new Random(100).nextInt());
            //一直监听客户端
            while (true) {
                Socket socket = userServerSocket.accept();
                //处理客户端的请求
                HandleLoginService service = new HandleLoginService(socket, user);
                service.setHandleLoginServiceListener(new HandleLoginService.HandleLoginServiceListener() {
                    @Override
                    public void getUserInfo(User user) {
                        Log.d(TAG, "getUserInfo: run");
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
                    "用户 " + new Random(100).nextInt());
            //给每个在线用户发出请求
            for (int i = 0; i < ipAddressList.size(); i++) {
                Log.d(TAG, "sendInfo: run1");
                Socket socket = new Socket(ipAddressList.get(i), Constant.USER_PORT);
                LoginClient loginClient = new LoginClient(socket, user);
                loginClient.setLoginClientListener(new LoginClient.LoginClientListener() {
                    @Override
                    public void getUserInfo(User user) {
                        Log.d(TAG, "getUserInfo: run2");
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
                        } else {
//                            Log.d(TAG, current_ip + ": false");
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

    class ScanIp implements Runnable {

        private String mPrefix;

        public ScanIp(String mPrefix) {
            this.mPrefix = mPrefix;
        }

        @Override
        public void run() {
            try {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < 256; i++) {
                    String testIp = mPrefix + String.valueOf(i);
                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(1000);
                    Log.d(TAG, testIp + ": " + reachable);

                    if (reachable) {
                        buffer.append(testIp);
                        buffer.append("\n");
                    }
                }

                mReachIp = buffer.toString();
                Message message = new Message();
                message.what = REACH_IP;
                mHandler.sendMessage(message);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
