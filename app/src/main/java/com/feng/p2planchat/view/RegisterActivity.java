package com.feng.p2planchat.view;

import android.view.View;
import android.widget.ImageView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mBackIv;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
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
        mBackIv = findViewById(R.id.iv_register_back);
        mBackIv.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
