package com.feng.p2planchat.view;

import android.view.View;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private TextView mLoginTv;
    private TextView mRegisterTv;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
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
        mLoginTv = findViewById(R.id.tv_login_login);
        mLoginTv.setOnClickListener(this);
        mRegisterTv = findViewById(R.id.tv_login_register);
        mRegisterTv.setOnClickListener(this);
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
                jumpToNewActivity(MainActivity.class);
                break;
            //进行注册
            case R.id.tv_login_register:
                jumpToNewActivity(RegisterActivity.class);
                break;
            default:
                break;
        }
    }
}
