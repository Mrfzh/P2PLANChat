package com.feng.p2planchat.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
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
import com.feng.p2planchat.entity.eventbus.UpdateHeadImageEvent;
import com.feng.p2planchat.entity.eventbus.UpdateNameEvent;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.PictureUtil;
import com.feng.p2planchat.util.UserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener{

    private static final int REQUEST_CHOOSE_PHOTO_FROM_ALBUM = 1;
    
    private ImageView mBackIv;
    private CircleImageView mHeadImageIv;
    private RelativeLayout mNameRv;
    private TextView mNameTv;
    private RelativeLayout mIpAddressRv;
    private TextView mIpAddressTv;
    private RelativeLayout mPasswordRv;

    private Bitmap mNewBitmap;  //新头像的Bitmap

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

        User own = UserUtil.readFromInternalStorage(this);
        if (own != null) {
            mNameTv.setText(own.getUserName());
            mHeadImageIv.setImageBitmap(BitmapUtil.byteArray2Bitmap(own.getHeadImage()));
            mIpAddressTv.setText(own.getIpAddress());
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
                //编辑头像：从相册中选择头像
                chooseFromAlbum();
                break;
            case R.id.rv_personal_info_name_layout:
                //更改用户名
                jumpToNewActivity(ModifyNameActivity.class);
                break;
            case R.id.rv_personal_info_ip_address_layout:
                //更新IP地址
                String newIpAddress = IpAddressUtil.getIpAddress(this);
                if (newIpAddress != null && !newIpAddress.equals(
                        mIpAddressTv.getText().toString())) {
                    //如果最新的IP地址和原来的不一样，则更新IP地址
                    mIpAddressTv.setText(newIpAddress);
                    showShortToast("更改为最新的IP地址");
                }
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
            case EventBusCode.UPDATE_NAME:
                mNameTv.setText(event.getData().getNewName());
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
                    mNewBitmap = BitmapFactory.decodeFile(imagePath);
                    //更新头像
                    updateHeadImage();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 更新头像
     */
    private void updateHeadImage() {
        //更新本地用户信息
        User user = UserUtil.readFromInternalStorage(this);
        user.setHeadImage(BitmapUtil.bitmap2ByteArray(mNewBitmap));
        UserUtil.write2InternalStorage(user, this);
        //更新保存在本地的头像
        BitmapUtil.save2InternalStorage(mNewBitmap, user.getUserName() + ".jpg", this);

        //更新当前页面的头像
        mHeadImageIv.setImageBitmap(mNewBitmap);

        //更新个人页面的头像
        Event<UpdateHeadImageEvent> updateHeadImageEvent = new Event<>(
                EventBusCode.UPDATE_HEAD_IMAGE, new UpdateHeadImageEvent(mNewBitmap));
        EventBusUtil.sendEvent(updateHeadImageEvent);
    }
}
