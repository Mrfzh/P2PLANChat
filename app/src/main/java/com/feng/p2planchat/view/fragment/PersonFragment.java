package com.feng.p2planchat.view.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseFragment;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IPersonContract;
import com.feng.p2planchat.entity.eventbus.LogoutEvent;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UpdateHeadImageEvent;
import com.feng.p2planchat.entity.eventbus.UpdateNameEvent;
import com.feng.p2planchat.presenter.PersonPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.UserUtil;
import com.feng.p2planchat.view.activity.LoginActivity;
import com.feng.p2planchat.view.activity.PersonalInfoActivity;
import com.feng.p2planchat.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/9
 */
public class PersonFragment extends BaseFragment<PersonPresenter>
        implements View.OnClickListener, IPersonContract.View {

    private static final String TAG = "fzh";

    private CircleImageView mHeadImageIv;
    private TextView mNameTv;
    private RelativeLayout mPersonalInfoRv;
    private RelativeLayout mLogoutRv;

    @Override
    protected void initData() {

    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void initView() {
        mHeadImageIv = getActivity().findViewById(R.id.iv_person_head_image);
        mHeadImageIv.setOnClickListener(this);

        mNameTv = getActivity().findViewById(R.id.tv_person_name);

        User own = UserUtil.readFromInternalStorage(Objects.requireNonNull(getContext()));
        if (own != null) {
            mHeadImageIv.setImageBitmap(BitmapUtil.byteArray2Bitmap(own.getHeadImage()));
            mNameTv.setText(own.getUserName());
        }

        mPersonalInfoRv = getActivity().findViewById(R.id.rv_person_personal_info_layout);
        mPersonalInfoRv.setOnClickListener(this);

        mLogoutRv = getActivity().findViewById(R.id.rv_person_logout_layout);
        mLogoutRv.setOnClickListener(this);
    }

    @Override
    protected PersonPresenter getPresenter() {
        return new PersonPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_person_head_image:
            case R.id.rv_person_personal_info_layout:
                //进入个人信息页面
                jump2Activity(PersonalInfoActivity.class);
                break;
            case R.id.rv_person_logout_layout:
                //退出登录
                final TipDialog tipDialog = new TipDialog.Builder(getContext())
                        .setContent("是否退出登录，退出登录后，之前的聊天记录将会消失")
                        .setEnsure("是")
                        .setCancel("否")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                //通知其他在线用户更新用户列表
                                mPresenter.logout(getContext());
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateHeadImageEventCome(Event<UpdateHeadImageEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.UPDATE_HEAD_IMAGE:
                mHeadImageIv.setImageBitmap(event.getData().getNewHeadImage());
                break;
            default:
                break;
        }
    }

    /**
     * 退出登录成功
     */
    @Override
    public void logoutSuccess() {
        //通知主活动退出
        Event<LogoutEvent> logoutEvent = new Event<>(EventBusCode.LOGOUT, new LogoutEvent());
        EventBusUtil.sendEvent(logoutEvent);
        //跳转到登录活动
        jump2Activity(LoginActivity.class);
    }

    /**
     * 退出登录失败
     *
     * @param errorMsg
     */
    @Override
    public void logoutError(String errorMsg) {
        showShortToast(errorMsg);
        //通知主活动退出
        Event<LogoutEvent> logoutEvent = new Event<>(EventBusCode.LOGOUT, new LogoutEvent());
        EventBusUtil.sendEvent(logoutEvent);
        //跳转到登录活动
        jump2Activity(LoginActivity.class);
    }
}
