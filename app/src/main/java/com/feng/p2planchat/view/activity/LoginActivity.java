package com.feng.p2planchat.view.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.ILoginContract;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.MainEvent;
import com.feng.p2planchat.presenter.LoginPresenter;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.view.test.TestActivity;

import java.util.List;

public class LoginActivity extends BaseActivity<LoginPresenter>
        implements View.OnClickListener, ILoginContract.View {

    private static final String TAG = "fzh";

    private EditText mNameEt;
    private EditText mPasswordEt;
    private TextView mLoginTv;
    private TextView mRegisterTv;
    private ProgressBar mProgressBar;

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
                jumpToNewActivity(TestActivity.class);
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
        //判空
        if (name.equals("")) {
            showShortToast("用户名不能为空");
        } else if (password.equals("")) {
            showShortToast("密码不能为空");
        }
        //判断该用户是否存在
        if (!mAccountOperation.hasName(name)) {
            showShortToast("该用户不存在，请先注册");
        }

        //进行登录操作
        mPresenter.login(name, password, this);
    }

    /**
     * 登录成功
     *
     * @param userList 在线用户信息
     */
    @Override
    public void loginSuccess(List<User> userList) {
        mProgressBar.setVisibility(View.GONE);
        //发送在线用户信息给主活动
        Event<MainEvent> mainEvent = new Event<>(EventBusCode.LOGIN_2_MAIN, new MainEvent(userList));
        EventBusUtil.sendStickyEvent(mainEvent);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
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
