package com.feng.p2planchat.view.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class Test4Activity extends BaseActivity {

    private ImageView mHeadImageIv;
    private View mBadgeView;
    private Badge mBadge;

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
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }
}
