package com.feng.p2planchat.view.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UpdateNameEvent;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.UserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mBackIv;
    private CircleImageView mHeadImageIv;
    private RelativeLayout mNameRv;
    private TextView mNameTv;
    private RelativeLayout mIpAddressRv;
    private TextView mIpAddressTv;
    private RelativeLayout mPasswordRv;

    private User mOwnInfo;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        //从本地获取用户信息
        mOwnInfo = UserUtil.readFromInternalStorage(this);
    }

    @Override
    protected void initView() {
        mBackIv = findViewById(R.id.iv_personal_info_back);
        mBackIv.setOnClickListener(this);

        mHeadImageIv = findViewById(R.id.iv_personal_info_head_image);
        mHeadImageIv.setOnClickListener(this);

        mNameRv = findViewById(R.id.rv_personal_info_name_layout);
        mNameRv.setOnClickListener(this);

        mNameTv = findViewById(R.id.tv_personal_info_name);

        mIpAddressRv = findViewById(R.id.rv_personal_info_ip_address_layout);
        mIpAddressRv.setOnClickListener(this);

        mIpAddressTv = findViewById(R.id.tv_personal_info_ip_address);

        mPasswordRv = findViewById(R.id.rv_personal_info_password_layout);
        mPasswordRv.setOnClickListener(this);

        if (mOwnInfo != null) {
            mNameTv.setText(mOwnInfo.getUserName());
            mHeadImageIv.setImageBitmap(BitmapUtil.byteArray2Bitmap(mOwnInfo.getHeadImage()));
            mIpAddressTv.setText(mOwnInfo.getIpAddress());
        }
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
            case R.id.iv_personal_info_back:
                finish();
                break;
            case R.id.iv_personal_info_head_image:
                //编辑头像
                break;
            case R.id.rv_personal_info_name_layout:
                //更改用户名
                jumpToNewActivity(ModifyNameActivity.class);
                break;
            case R.id.rv_personal_info_ip_address_layout:
                //更新IP地址
                break;
            case R.id.rv_personal_info_password_layout:
                //修改密码
                jumpToNewActivity(ModifyPasswordActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateNameEventCome(Event<UpdateNameEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.MODIFY_NAME_2_UPDATE_NAME:
                mNameTv.setText(event.getData().getNewName());
                break;
            default:
                break;
        }
    }
}
