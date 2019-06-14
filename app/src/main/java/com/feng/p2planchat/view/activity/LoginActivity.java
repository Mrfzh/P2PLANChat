package com.feng.p2planchat.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.ILoginContract;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.MainEvent;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.presenter.LoginPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.UserUtil;
import com.feng.p2planchat.widget.TipDialog;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends BaseActivity<LoginPresenter>
        implements View.OnClickListener, ILoginContract.View {

    private static final String TAG = "fzh";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private EditText mNameEt;
    private EditText mPasswordEt;
    private TextView mLoginTv;
    private TextView mRegisterTv;
    private ProgressBar mProgressBar;

    private User mOwnInfo;      //自己的用户信息
    private AccountOperation mAccountOperation;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter();
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
        mNameEt = findViewById(R.id.et_login_user);
        mPasswordEt = findViewById(R.id.et_login_password);
        mLoginTv = findViewById(R.id.tv_login_login);
        mLoginTv.setOnClickListener(this);
        mRegisterTv = findViewById(R.id.tv_login_register);
        mRegisterTv.setOnClickListener(this);
        mProgressBar = findViewById(R.id.pb_login_progress_bar);
    }

    @Override
    protected void doInOnCreate() {
        //先检查权限
        checkPermission();
    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击登录
            case R.id.tv_login_login:
                //显示进度
                mProgressBar.setVisibility(View.VISIBLE);
                //延时登录，不然会直接卡住
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login(mNameEt.getText().toString(), mPasswordEt.getText().toString());
                    }
                }, 300);
                break;
            //进行注册
            case R.id.tv_login_register:
                jumpToNewActivity(RegisterActivity.class);
                break;
            default:
                break;
        }
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
                    TipDialog tipDialog = new TipDialog.Builder(LoginActivity.this)
                            .setContent("由于此权限影响应用的正常使用，不开启的话将不能使用本应用。")
                            .setEnsure("开启")
                            .setCancel("算了")
                            .setOnClickListener(new TipDialog.OnClickListener() {
                                @Override
                                public void clickEnsure() {
                                    ActivityCompat.requestPermissions(LoginActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_WRITE_EXTERNAL_STORAGE);
                                }

                                @Override
                                public void clickCancel() {
                                    finish();
                                }
                            })
                            .build();
                    tipDialog.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void login(String name, String password) {
        //先去掉空格
        name = name.replaceAll(" ", "");
        password = password.replaceAll(" ", "");

        //确定输入没问题，才进行登录操作
        if (check(name, password)) {
            //保存自己的用户信息
            mOwnInfo = new User(IpAddressUtil.getIpAddress(this), name,
                    BitmapUtil.bitmap2ByteArray(Objects.requireNonNull(
                            BitmapUtil.readFromInternalStorage(name + ".jpg", this))));
            //进行登录操作
            mPresenter.login(mOwnInfo, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 检查用户输入
     *
     * @param name
     * @param password
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
        //判断该用户是否存在
        if (!mAccountOperation.hasName(name)) {
            showShortToast("该用户不存在，请先注册");
            return false;
        }
        //检查密码是否正确
        if (!password.equals(mAccountOperation.getPassword(name))) {
            showShortToast("密码不正确，请重试");
            return false;
        }

        return true;
    }

    /**
     * 登录成功
     *
     * @param userList 在线用户信息
     */
    @Override
    public void loginSuccess(List<User> userList) {
        mProgressBar.setVisibility(View.GONE);

        if (mOwnInfo == null) {
            Log.d(TAG, "mOwnInfo: null");
        } else {
            Log.d(TAG, "mOwnInfo: " + mOwnInfo.show());
        }

        //将自己的用户信息写入本地
        UserUtil.write2InternalStorage(mOwnInfo, this);

        //发送在线用户信息给主活动
        Event<MainEvent> mainEvent = new Event<>(EventBusCode.LOGIN_2_MAIN,
                new MainEvent(userList));
        EventBusUtil.sendStickyEvent(mainEvent);
//        //发送在线用户信息给用户列表页面
//        Event<UserListEvent> userListEvent = new Event<>(EventBusCode.LOGIN_2_USER_LIST,
//                new UserListEvent(userList));
//        EventBusUtil.sendStickyEvent(userListEvent);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            if (curr == null) {
                continue;
            }
            builder.append("用户名：");
            builder.append(curr.getUserName());
            builder.append(", 用户IP地址：");
            builder.append(curr.getIpAddress());
            builder.append("\n");
        }
        Log.d(TAG, "loginSuccess: " + builder.toString());

        //跳转到主活动
        jumpToNewActivity(MainActivity.class);
        finish();
    }

    /**
     * 登录失败
     *
     * @param errorMsg
     */
    @Override
    public void loginError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        showShortToast(errorMsg);
    }
}
