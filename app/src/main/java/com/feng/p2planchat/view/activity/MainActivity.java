package com.feng.p2planchat.view.activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.adapter.MainViewPagerAdapter;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.entity.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.MainEvent;
import com.feng.p2planchat.view.fragment.PersonFragment;
import com.feng.p2planchat.view.fragment.UserListFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    private static final String TAG = "fzh";

    private TabLayout mBottomTabTv;
    private ViewPager mContentVp;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTabTitleList = new ArrayList<>();

    private List<User> mUserList;   //在线用户列表

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        mFragmentList.add(new UserListFragment());
        mFragmentList.add(new PersonFragment());

        mTabTitleList.add(Constant.MAIN_TAB_TITLES[0]);
        mTabTitleList.add(Constant.MAIN_TAB_TITLES[1]);
    }

    @Override
    protected void initView() {
        mContentVp = findViewById(R.id.vp_main_content);
        mContentVp.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager(), mFragmentList));

        mBottomTabTv = findViewById(R.id.tv_main_bottom_tab);
        mBottomTabTv.setupWithViewPager(mContentVp);    //将TabLayout和ViewPager关联起来
        //监听底部tab的点击
        mBottomTabTv.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //点击不同的tab时，改变各个tab的状态
                for (int i = 0; i < mBottomTabTv.getTabCount(); i++) {
                    try {
                        View view = mBottomTabTv.getTabAt(i).getCustomView();
                        ImageView tabIconImageView = view.findViewById(R.id.iv_main_tab_icon);
                        TextView tabTitleTextView = view.findViewById(R.id.tv_main_tab_title);
                        //选中的tab
                        if (i == mBottomTabTv.getSelectedTabPosition()) {
                            tabIconImageView.setImageResource(Constant.MAIN_TAB_ICONS_AFTER_PRESSED[i]);
                            tabTitleTextView.setTextColor(getResources()
                                    .getColor(R.color.color_main));
                        } else {    //未选中的tab
                            tabIconImageView.setImageResource(Constant.MAIN_TAB_ICONS_BEFORE_PRESSED[i]);
                            tabTitleTextView.setTextColor(getResources()
                                    .getColor(R.color.color_main_tab_text_before_pressed));
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //自定义底部tab的View
        Objects.requireNonNull(mBottomTabTv.getTabAt(0)).setCustomView(
                getTabView(this, 0, true));
        Objects.requireNonNull(mBottomTabTv.getTabAt(1)).setCustomView(
                getTabView(this, 1, false));
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    /**
     * 注册EventBus
     *
     * @return
     */
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCone(Event<MainEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.LOGIN_2_MAIN:
                mUserList = event.getData().getUserList();
                break;
            default:
                break;
        }
    }

    /**
     * 获取Tab显示的内容
     *
     * @param context  上下文
     * @param position Tab的索引
     * @param isSelected  是否为点击状态
     * @return 自定义的TabView
     */
    private View getTabView(Context context, int position, boolean isSelected) {

        View view = LayoutInflater.from(context).inflate(R.layout.main_tab_view, null);
        //设置图标
        ImageView tabIconImageView = view.findViewById(R.id.iv_main_tab_icon);
        if (isSelected) {
            tabIconImageView.setImageResource(Constant.MAIN_TAB_ICONS_AFTER_PRESSED[position]);
        } else {
            tabIconImageView.setImageResource(Constant.MAIN_TAB_ICONS_BEFORE_PRESSED[position]);
        }
        //设置文字
        TextView tabTitleTextView = view.findViewById(R.id.tv_main_tab_title);
        tabTitleTextView.setText(Constant.MAIN_TAB_TITLES[position]);
        if (isSelected) {
            tabTitleTextView.setTextColor(getResources().getColor(R.color.color_main));
        } else {
            tabTitleTextView.setTextColor(getResources()
                    .getColor(R.color.color_main_tab_text_before_pressed));
        }
        return view;
    }
}
