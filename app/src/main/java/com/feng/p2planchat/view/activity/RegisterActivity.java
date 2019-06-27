package com.feng.p2planchat.view.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IRegisterContract;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.entity.serializable.OtherUserIp;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.MainEvent;
import com.feng.p2planchat.presenter.RegisterPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.OtherUserIpUtil;
import com.feng.p2planchat.util.PictureUtil;
import com.feng.p2planchat.util.SoftKeyboardUtil;
import com.feng.p2planchat.util.UserUtil;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity<RegisterPresenter>
        implements View.OnClickListener, IRegisterContract.View {

    private static final String TAG = "fzh";
    private static final int REQUEST_CHOOSE_PHOTO_FROM_ALBUM = 1;

    private ImageView mBackIv;
    private ImageView mHeadImageIv;
    private EditText mNameEt;
    private EditText mPasswordEt;
    private TextView mEnterTv;
    private ProgressBar mProgressBar;

    private Bitmap mBitmap;     //选中头像的bitmap
    private User mOwnInfo;      //自己的用户信息

    private AccountOperation mAccountOperation;     //数据库操作
    private boolean mIsEnter = false;       //是否正在登入

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected RegisterPresenter getPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected void initData() {
        AccountDatabaseHelper helper = new AccountDatabaseHelper(this,
                Constant.DATABASE_ACCOUNT, null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mAccountOperation = new AccountOperation(db);
    }

    @Override
    protected void initView() {
        mBackIv = findViewById(R.id.iv_register_back);
        mBackIv.setOnClickListener(this);
        mHeadImageIv = findViewById(R.id.iv_register_head_image);
        mHeadImageIv.setOnClickListener(this);
        mNameEt = findViewById(R.id.et_register_user_name);
        mPasswordEt = findViewById(R.id.et_register_password);
        mEnterTv = findViewById(R.id.tv_register_enter);
        mEnterTv.setOnClickListener(this);
        mProgressBar = findViewById(R.id.pb_register_progress_bar);
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
            //点击返回按钮
            case R.id.iv_register_back:
                finish();
                break;
            //点击头像
            case R.id.iv_register_head_image:
                //从相册中选择头像
                chooseFromAlbum();
                break;
            //点击进入
            case R.id.tv_register_enter:
                if (!mIsEnter) {
                    mIsEnter = true;
                    mProgressBar.setVisibility(View.VISIBLE);
                    //隐藏软键盘
                    SoftKeyboardUtil.hideSoftKeyboard(this);
                    //延时注册，不然会直接卡住
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            register(mNameEt.getText().toString(), mPasswordEt.getText().toString());
                        }
                    }, 300);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 从相册中选择头像
     */
    private void chooseFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_PHOTO_FROM_ALBUM:
                String imagePath;
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号，根据版本号对图片选中的图片进行操作
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上版本
                        assert data != null;
                        imagePath = PictureUtil.handleImageOnKitKat(data, this);
                    } else {
                        assert data != null;
                        imagePath = PictureUtil.handleImageBeforeKitKat(data, this);
                    }
                    mBitmap = BitmapFactory.decodeFile(imagePath);
                    mHeadImageIv.setImageBitmap(mBitmap);
                }
                break;
            default:
                break;
        }
    }

    private void register(String name, String password) {
        //先去掉空格
        name = name.replaceAll(" ", "");
        password = password.replaceAll(" ", "");

        if (check(name, password)) {
            //将新用户的用户名和密码写入数据库中
            mAccountOperation.add(name, password);
            if (mBitmap == null) {
                //使用默认头像
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_head_image);
            }
            //将新用户的头像保存到本地
            BitmapUtil.save2InternalStorage(mBitmap, name + ".jpg", this);

            //保存自己的用户信息
            mOwnInfo = new User(IpAddressUtil.getIpAddress(this), name,
                    BitmapUtil.bitmap2ByteArray(mBitmap));

            //进行注册操作
            mPresenter.register(mOwnInfo, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 检查用户输入
     *
     * @return
     */
    private boolean check(String name, String password) {
        //判空
        if (name.equals("")) {
            showShortToast("用户名不能为空");
            return false;
        } else if (password.equals("")) {
            showShortToast("密码不能为空");
            return false;
        }
        //判断该用户是否已存在
        if (mAccountOperation.hasName(name)) {
            showShortToast("该用户已存在");
            return false;
        }

        return true;
    }

    /**
     * 注册成功
     *
     * @param userList 在线用户列表
     */
    @Override
    public void registerSuccess(List<User> userList) {
        mProgressBar.setVisibility(View.GONE);

        //将其他用户的IP地址写入本地
        List<String> otherUserIpList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            if (curr == null) {
                continue;
            }
            otherUserIpList.add(curr.getIpAddress());
        }
        OtherUserIpUtil.write2InternalStorage(new OtherUserIp(otherUserIpList), this);

        //将自己的用户信息写入本地
        UserUtil.write2InternalStorage(mOwnInfo, this);

        //发送在线用户信息给主活动
        Event<MainEvent> mainEvent = new Event<>(EventBusCode.REGISTER_2_MAIN,
                new MainEvent(userList));
        EventBusUtil.sendStickyEvent(mainEvent);
//        //发送在线用户信息给用户列表页面
//        Event<UserListEvent> userListEvent = new Event<>(EventBusCode.REGISTER_2_USER_LIST,
//                new UserListEvent(userList));
//        EventBusUtil.sendStickyEvent(userListEvent);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            builder.append("用户名：");
            builder.append(curr.getUserName());
            builder.append(", 用户IP地址：");
            builder.append(curr.getIpAddress());
            builder.append("\n");
        }
        Log.d(TAG, "registerSuccess: " + builder.toString());

        mIsEnter = false;

        //跳转到主活动
        jumpToNewActivity(MainActivity.class);
        finish();
    }

    /**
     * 注册失败
     *
     * @param errorMsg
     */
    @Override
    public void registerError(String errorMsg) {
        mIsEnter = false;
        mProgressBar.setVisibility(View.GONE);
        showShortToast(errorMsg);
    }
}
