package com.feng.p2planchat.view.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.client.TransferFileClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.service.HandleTransferFileService;
import com.feng.p2planchat.util.IpAddressUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class Test4Activity extends BaseActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int REQUEST_CODE_CHOOSE_FILE = 1;
    private static final String TAG = "fzh";

    private ImageView mHeadImageIv;
    private View mBadgeView;
    private Badge mBadge;
    private Button mOpenServiceBtn;
    private Button mChooseFileBtn;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test4;
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
        mHeadImageIv = findViewById(R.id.iv_test4_head_image);
        mBadgeView = findViewById(R.id.v_test4_badge);
        mBadge = new QBadgeView(this).bindTarget(mBadgeView)
                .setBadgeNumber(33).setBadgeGravity(Gravity.CENTER);

        mOpenServiceBtn = findViewById(R.id.btn_test4_open_service);
        mOpenServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动服务端，监听文件传输请求
                new TransferFileServiceThread().start();
            }
        });

        mChooseFileBtn = findViewById(R.id.btn_test4_choose_file);
        mChooseFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
            }
        });
    }


    @Override
    protected void doInOnCreate() {
        checkPermission();
    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        //如果没有WRITE_EXTERNAL_STORAGE权限，则需要动态申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (!(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
                    //如果用户不同意开启权限，提醒用户
                    Toast.makeText(Test4Activity.this, "请打开权限",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_FILE:
                if (resultCode == RESULT_OK) {  //是否选择，没选择就不会继续
                    Uri uri = data.getData();   //得到uri，后面就是将uri转化成file的过程。
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    actualimagecursor.moveToFirst();
                    String filePath = actualimagecursor.getString(actual_image_column_index);
                    if (filePath == null) {
                        showShortToast("不支持该类型");
                    } else {
                        Log.d(TAG, "onActivityResult: filePath = " + filePath);
                        //获得路径后就是传输File了
                        new TransferFileClientThread(filePath).start();
                    }
                }
                break;
            default:
                break;
        }
    }

    class TransferFileServiceThread extends Thread {
        @Override
        public void run() {
            try {
                //创建一个ServerSocket对象，用于监听客户端Socket的请求
                ServerSocket serverSocket = new ServerSocket(Constant.FILE_PORT);
                while (true) {
                    //接收到客户端Socket的请求时，服务端也会对应产生一个Socket
                    Socket socket = serverSocket.accept();
                    Log.d(TAG, "service: other ip = " + socket.getInetAddress().getHostAddress());
                    // 每接收到一个Socket就建立一个新的线程来处理它
                    new Thread(new HandleTransferFileService(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TransferFileClientThread extends Thread {

        String filePath;

        public TransferFileClientThread(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
//                //其他在线用户的IP地址
//                List<String> ipAddressList = IpAddressUtil
//                        .getIpAddressList(Test4Activity.this);
//                //分别发送文件
//                for (int i = 0; i < ipAddressList.size(); i++) {
//                    Socket socket = new Socket(ipAddressList.get(i), Constant.FILE_PORT);
//                    new Thread(new TransferFileClient(socket, filePath)).start();
//                }
                    Socket socket = new Socket("192.168.137.231", Constant.FILE_PORT);
                    new Thread(new TransferFileClient(socket, filePath)).start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
