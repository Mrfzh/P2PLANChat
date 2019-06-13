package com.feng.p2planchat.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.MainEvent;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.service.HandleLoginService;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.view.fragment.PersonFragment;
import com.feng.p2planchat.view.fragment.UserListFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    private static final String TAG = "fzh";
    private static final int UPDATE_USER_LIST = 1;

    private TabLayout mBottomTabTv;
    private ViewPager mContentVp;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTabTitleList = new ArrayList<>();

    private List<User> mUserList = new ArrayList<>();   //在线用户列表
    private User mOwnInfo;      //自己的用户信息

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER_LIST:
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < mUserList.size(); i++) {
                        User curr = mUserList.get(i);
                        builder.append("用户名：");
                        builder.append(curr.getUserName());
                        builder.append(", 用户IP地址：");
                        builder.append(curr.getIpAddress());
                        builder.append("\n");
                    }
                    Log.d(TAG, "handleMessage: " + builder.toString());
                    break;
                default:
                    break;
            }
        }
    };

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
        //发送在线用户信息给用户列表页面
        Event<UserListEvent> userListEvent = new Event<>(EventBusCode.MAIN_2_USER_LIST,
                new UserListEvent(mUserList));
        EventBusUtil.sendStickyEvent(userListEvent);

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
        //打开服务器端，随时接收其他用户的登录信息
        new Thread(new LoginServiceThread()).start();

        User curr = mOwnInfo;
        String builder1 = null;
        if (curr != null) {
            builder1 = "用户名：" +
                    curr.getUserName() +
                    ", 用户IP地址：" +
                    curr.getIpAddress() +
                    "\n";
        }
        Log.d(TAG, "ownInfo: " + builder1);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mUserList.size(); i++) {
            User curr1 = mUserList.get(i);
            if (curr == null) {
                continue;
            }
            builder.append("用户名：");
            builder.append(curr1.getUserName());
            builder.append(", 用户IP地址：");
            builder.append(curr1.getIpAddress());
            builder.append("\n");
        }
        Log.d(TAG, "otherInfo: " + builder.toString());
    }

    class LoginServiceThread implements Runnable {

        @Override
        public void run() {
            try {
                //作为服务端，监听其他用户的登录信息
                ServerSocket userServerSocket = new ServerSocket(Constant.USER_PORT);

                //一直监听客户端
                while (true) {
                    Socket socket = userServerSocket.accept();
                    //处理客户端的请求
                    HandleLoginService service = new HandleLoginService(socket, mOwnInfo);
                    service.setHandleLoginServiceListener(new HandleLoginService.HandleLoginServiceListener() {
                        @Override
                        public void getUserInfo(User user) {
                            //添加新用户
                            mUserList.add(user);
                            //在主线程更新列表
                            Message message = new Message();
                            message.what = UPDATE_USER_LIST;
                            mHandler.sendMessage(message);
                        }
                    });
                    new Thread(service).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event<MainEvent> event) {
//        Log.d(TAG, "onStickyEventBusCome: urn");
//        Log.d(TAG, "onStickyEventBusCome: event.getCode() = " + event.getCode());
//        Log.d(TAG, "onStickyEventBusCome: event.getData() = " + event.getData());
        switch (event.getCode()) {
            case EventBusCode.LOGIN_2_MAIN:
            case EventBusCode.REGISTER_2_MAIN:
                Log.d(TAG, "onEventCone: run");
                mOwnInfo = event.getData().getOwnInfo();
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
