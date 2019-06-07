package com.feng.p2planchat.view;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.thread.ClientThread;
import com.feng.p2planchat.util.IpAddressUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";
    private static final int UPDATE_TEXT = 1;

    private Button mRequestBtn;
    private TextView mContentTv;
    private Button mGetIpBtn;

    private String mContent;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    mContentTv.setText(mContent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
        mRequestBtn = findViewById(R.id.btn_main_request);
        mRequestBtn.setOnClickListener(this);
        mContentTv = findViewById(R.id.tv_main_content);
        mGetIpBtn = findViewById(R.id.btn_main_get_ip);
        mGetIpBtn.setOnClickListener(this);
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_request:
                request();
                break;
            case R.id.btn_main_get_ip:
                //获取本机ip地址
                mContentTv.setText(IpAddressUtil.getIpAddress(MainActivity.this));
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
}
