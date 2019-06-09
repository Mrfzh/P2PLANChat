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
import com.feng.p2planchat.thread.ClientThread;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.NetUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";
    private static final int UPDATE_TEXT = 1;
    private static final int REACH_IP = 2;

    private Button mRequestBtn;
    private TextView mContentTv;
    private Button mGetIpBtn;
    private Button mCheckNetBtn;
    private Button mReachBtn;

    private String mContent;
    private String mReachIp;

//    private String ping = "ping -c 1 -w 0.5 " ;

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
                scanIp();
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
        //创建256个线程分别去ping
        for ( int i = 0; i < 256; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                public void run() {
                    //利用ping命令判断
                    String current_ip = "10.41.29." + finalI;
                    //要执行的ping命令，其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
                    String ping = "ping -c 1 -w 0.5 " + current_ip;
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
