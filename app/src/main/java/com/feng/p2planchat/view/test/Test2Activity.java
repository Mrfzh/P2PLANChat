package com.feng.p2planchat.view.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.client.LoginClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.service.HandleLoginService;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.PictureUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test2Activity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int REQUEST_CHOOSE_PHOTO_FROM_ALBUM = 1;
    private static final int UPDATE_USER_LIST = 3;

//    private Button mLoadPictureBtn;
    private ImageView mPictureIv;
    private Button mShowOwnHeadImageBtn;
    private Button mReceivePictureBtn;
    private Button mSendPictureBtn;
    private Button mBytesAndBitmapBtn;
    private Button mSavePictureBtn;
    private Button mLoadPictureBtn;

    private String mImagePath;  //选择的图片路径
    private Bitmap mBitmap;     //选中图片的bitmap
    private Bitmap mOwnBitmap;  //自己头像的Bitmap
    private byte [] mOwnPicBytes;  //自己头像的byte数组

    private List<User> mUserList = new ArrayList<>();   //用户列表

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER_LIST:
                    for (int i = 0; i < mUserList.size(); i++) {
                        User curr = mUserList.get(i);
//                        Log.d(TAG, "handleMessage: bytes = " + Arrays.toString(curr.getHeadImage()));
//                        Log.d(TAG, "handleMessage: IpAddress = " + curr.getIpAddress());
                        byte [] headImage = curr.getHeadImage();
//                        Log.d(TAG, "handleMessage: bytes = " + Arrays.toString(headImage));
                        mPictureIv.setImageBitmap(BitmapUtil.byteArray2Bitmap(headImage));
                    }
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
        return R.layout.activity_test2;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        int seed = (int) (System.currentTimeMillis() % 5);
//        int seed = 1;
        mOwnBitmap = BitmapUtil.getBitmapByResId("head_image" + seed, this);
        assert mOwnBitmap != null;
        mOwnPicBytes = BitmapUtil.bitmap2ByteArray(mOwnBitmap);
    }

    @Override
    protected void initView() {
//        mLoadPictureBtn = findViewById(R.id.btn_test2_load_picture);
//        mLoadPictureBtn.setOnClickListener(this);
        mPictureIv = findViewById(R.id.iv_test2_picture);
        mShowOwnHeadImageBtn = findViewById(R.id.btn_test2_show_own_headImage);
        mShowOwnHeadImageBtn.setOnClickListener(this);
        mReceivePictureBtn = findViewById(R.id.btn_test2_receive_picture);
        mReceivePictureBtn.setOnClickListener(this);
        mSendPictureBtn = findViewById(R.id.btn_test2_send_picture);
        mSendPictureBtn.setOnClickListener(this);
        mBytesAndBitmapBtn = findViewById(R.id.btn_test2_bytes_and_bitmap);
        mBytesAndBitmapBtn.setOnClickListener(this);
        mSavePictureBtn = findViewById(R.id.btn_test2_save_picture);
        mSavePictureBtn.setOnClickListener(this);
        mLoadPictureBtn = findViewById(R.id.btn_test2_load_picture);
        mLoadPictureBtn.setOnClickListener(this);
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
//            case R.id.btn_test2_load_picture:
//                //从相册中选择
//                chooseFromAlbum();
//                break;
            case R.id.btn_test2_show_own_headImage:
                //显示自己的头像
                mPictureIv.setImageBitmap(mOwnBitmap);
                break;
            case R.id.btn_test2_receive_picture:
                //打开服务器，随时接收客户端发来的图片
                new Thread( new ReceivePictureThread()).start();
                break;
            case R.id.btn_test2_send_picture:
                //先服务端发送图片
                new Thread( new SendPictureThread()).start();
                break;
            case R.id.btn_test2_bytes_and_bitmap:
                //bytes和bitmap的转换
                mOwnPicBytes = BitmapUtil.bitmap2ByteArray(mOwnBitmap);
                Log.d(TAG, "pre: mOwnPicBytes = " + Arrays.toString(mOwnPicBytes));
                Bitmap bitmap = BitmapUtil.byteArray2Bitmap(mOwnPicBytes);
                mPictureIv.setImageBitmap(bitmap);
                Log.d(TAG, "bitmap = " + bitmap);
                byte [] next = BitmapUtil.bitmap2ByteArray(bitmap);
                Log.d(TAG, "after: mOwnPicBytes = " + Arrays.toString(next));
                break;
            case R.id.btn_test2_save_picture:
                //存储图片到内部存储
                BitmapUtil.save2InternalStorage(mOwnBitmap, "测试1.jpg", this);
                showShortToast("存储成功");
                break;
            case R.id.btn_test2_load_picture:
                //从内部存储加载图片
                Bitmap bitmap1 = BitmapUtil.readFromInternalStorage("测试1.jpg", this);
                mPictureIv.setImageBitmap(bitmap1);
                break;
            default:
                break;
        }
    }

    /**
     * 从相册中选择图片
     */
    private void chooseFromAlbum() {
        //如果没有WRITE_EXTERNAL_STORAGE权限，则需要动态申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            openAlbum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {    //用户同意权限
                    openAlbum();
                } else {
                    showShortToast("用户不同意权限");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_PHOTO_FROM_ALBUM:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号，根据版本号对图片选中的图片进行操作
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上版本
                        assert data != null;
                        mImagePath = PictureUtil.handleImageOnKitKat(data, this);
                    } else {
                        assert data != null;
                        mImagePath = PictureUtil.handleImageBeforeKitKat(data, this);
                    }
                    mBitmap = BitmapFactory.decodeFile(mImagePath);
                    mPictureIv.setImageBitmap(mBitmap);
                }
                break;
            default:
                break;
        }
    }

    class ReceivePictureThread implements Runnable {

        @Override
        public void run() {
            try {
                //先作为服务端，得到其他用户的信息
                ServerSocket userServerSocket = new ServerSocket(Constant.USER_PORT);
                //自己的用户信息
                User user = new User(IpAddressUtil.getIpAddress(Test2Activity.this),
                        "用户 " + System.currentTimeMillis(), mOwnPicBytes);
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
    }

    class SendPictureThread implements Runnable {

        @Override
        public void run() {
            try {
                //得到其他在线用户的ip地址
                List<String> ipAddressList = IpAddressUtil.getIpAddressList(Test2Activity.this);
                //自己的用户信息
                User user = new User(IpAddressUtil.getIpAddress(Test2Activity.this),
                        "用户 " + System.currentTimeMillis(), mOwnPicBytes);
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
    }
}
