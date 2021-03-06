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
import com.feng.p2planchat.client.UpdateClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.entity.eventbus.ChatDataEvent;
import com.feng.p2planchat.entity.eventbus.DeleteUserEvent;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.FileEvent;
import com.feng.p2planchat.entity.eventbus.LogoutEvent;
import com.feng.p2planchat.entity.eventbus.MainEvent;
import com.feng.p2planchat.entity.eventbus.UpdateOtherHeadImageEvent;
import com.feng.p2planchat.entity.eventbus.UpdateOtherNameEvent;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.entity.serializable.ChatData;
import com.feng.p2planchat.entity.serializable.UpdateUser;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.service.HandleChatService;
import com.feng.p2planchat.service.HandleLoginService;
import com.feng.p2planchat.service.HandleTransferFileService;
import com.feng.p2planchat.service.HandleUpdateService;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.OtherUserIpUtil;
import com.feng.p2planchat.util.TimeUtil;
import com.feng.p2planchat.util.UserUtil;
import com.feng.p2planchat.view.fragment.PersonFragment;
import com.feng.p2planchat.view.fragment.UserListFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    private static final String TAG = "fzh";
//    public static boolean IS_FINISH = false;

    private TabLayout mBottomTabTv;
    private ViewPager mContentVp;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTabTitleList = new ArrayList<>();

    private List<User> mUserList = new ArrayList<>();   //在线用户列表

    private LoginServiceThread mLoginServiceThread;
    private UpdateServiceThread mUpdateServiceThread;
    private ChatServiceThread mChatServiceThread;
    private TransferFileServiceThread mTransferFileServiceThread;

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
        mLoginServiceThread = new LoginServiceThread();
        mLoginServiceThread.start();

        //打开服务器端，随机接收其他用户的信息更新
        mUpdateServiceThread = new UpdateServiceThread();
        mUpdateServiceThread.start();

        //打开服务器，随时接受其他用户的聊天信息
        mChatServiceThread = new ChatServiceThread();
        mChatServiceThread.start();

        //打开服务器，随时接受其他用户传来的文件
        mTransferFileServiceThread = new TransferFileServiceThread();
        mTransferFileServiceThread.start();

        showInfo();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: run");
        //退出主活动时，通知其他用户
        logoutAndNotify();
        //结束创建的线程
        mLoginServiceThread.close();
        mUpdateServiceThread.close();
        mChatServiceThread.close();
        mTransferFileServiceThread.close();

        super.onDestroy();
    }

    /**
     * 显示当前信息（测试方法，可删除）
     */
    private void showInfo() {
        User curr = UserUtil.readFromInternalStorage(this);
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

    class LoginServiceThread extends Thread {

        private ServerSocket userServerSocket;

        @Override
        public void run() {
            try {

                //作为服务端，监听其他用户的登录信息
                userServerSocket = new ServerSocket(Constant.USER_PORT);

                //一直监听客户端
                while (true) {
                    Socket socket = userServerSocket.accept();
                    Log.d(TAG, "MainActivity: userServerSocket.accept()");
                    //处理客户端的请求
                    HandleLoginService service = new HandleLoginService(socket,
                            UserUtil.readFromInternalStorage(MainActivity.this));
                    service.setHandleLoginServiceListener(new HandleLoginService.HandleLoginServiceListener() {
                        @Override
                        public void getUserInfo(User user) {
                            //通知用户列表有新用户上线
                            Event<UserListEvent> userListEvent = new Event<>(
                                    EventBusCode.MAIN_2_USER_LIST, new UserListEvent(user));
                            EventBusUtil.sendEvent(userListEvent);
                        }
                    });
                    new Thread(service).start();
                }

            } catch (IOException e) {
                Log.d(TAG, "MainActivity.LoginServiceThread.IOException = " + e.toString());
            }
        }

        public void close() {
            Log.d(TAG, "MainActivity.LoginServiceThread.close: run");
            try {
                userServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class UpdateServiceThread extends Thread {

        private ServerSocket updateServerSocket;

        @Override
        public void run() {
            try {
                //作为服务端，监听其他用户的登录信息
                updateServerSocket = new ServerSocket(Constant.UPDATE_PORT);

                //一直监听客户端
                while (true) {
                    Socket socket = updateServerSocket.accept();
                    //处理客户端的请求
                    HandleUpdateService service = new HandleUpdateService(socket);
                    Log.d(TAG, "UpdateServiceThread: run");
                    service.setHandleUpdateServiceListener(new HandleUpdateService.HandleUpdateServiceListener() {
                        @Override
                        public void getUpdateUserInfo(UpdateUser updateUser) {
                            //通知用户列表界面更新相关信息
                            switch (updateUser.getUpdateWhat()) {
                                case Constant.UPDATE_USER_NAME:
                                    //更新用户名
                                    Event<UpdateOtherNameEvent> updateOtherNameEvent = new Event<>(
                                            EventBusCode.UPDATE_OTHER_NAME, new UpdateOtherNameEvent(
                                                    updateUser.getIp(), updateUser.getNewName()));
                                    EventBusUtil.sendEvent(updateOtherNameEvent);
                                    break;
                                case Constant.UPDATE_HEAD_IMAGE:
                                    //更新头像
                                    Event<UpdateOtherHeadImageEvent> updateOtherHeadImageEvent = new Event<>(
                                            EventBusCode.UPDATE_OTHER_HEAD_IMAGE, new UpdateOtherHeadImageEvent(
                                            updateUser.getIp(), BitmapUtil.byteArray2Bitmap(updateUser.getNewHeadImage())));
                                    EventBusUtil.sendEvent(updateOtherHeadImageEvent);
                                    break;
                                case Constant.DELETE_USER:
                                    //删除用户
                                    Event<DeleteUserEvent> deleteUserEvent = new Event<>(
                                            EventBusCode.DELETE_USER, new DeleteUserEvent(updateUser.getIp()));
                                    EventBusUtil.sendEvent(deleteUserEvent);
                                default:
                                    break;
                            }
                        }
                    });

                    new Thread(service).start();
                }
            } catch (IOException e) {
                Log.d(TAG, "UpdateServiceThread.IOException: " + e.toString());
            }
        }

        public void close() {
            try {
                updateServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ChatServiceThread extends Thread {

        private ServerSocket chatServerSocket;

        @Override
        public void run() {
            try {
                //作为服务端，监听其他用户的登录信息
                chatServerSocket = new ServerSocket(Constant.CHAT_PORT);

                //一直监听客户端
                while (true) {
                    Socket socket = chatServerSocket.accept();
                    //处理客户端的请求
                    HandleChatService service = new HandleChatService(socket);
                    service.setHandleChatServiceListener(new HandleChatService.HandleChatServiceListener() {
                        @Override
                        public void getMessage(ChatData chatData) {
                            Log.d(TAG, "getMessage: " + chatData);
                            //将新消息发送给用户列表界面
                            Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.MAIN_2_USER_LIST_CHAT_DATA,
                                    new ChatDataEvent(chatData));
                            EventBusUtil.sendEvent(chatDataEvent);
                        }
                    });
                    new Thread(service).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                chatServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TransferFileServiceThread extends Thread {

        private ServerSocket transferFileServerSocket;

        @Override
        public void run() {
            try {
                transferFileServerSocket = new ServerSocket(Constant.FILE_PORT);
                while (true) {
                    Socket socket = transferFileServerSocket.accept();
                    //对方的IP地址和用户名
                    final String otherIp = socket.getInetAddress().getHostAddress();
                    // 每接收到一个Socket就建立一个新的线程来处理它
                    HandleTransferFileService service = new HandleTransferFileService(socket);
//                    final String[] currFileName = new String[1];
//                    service.setOnHandleTransferFileServiceListener(new HandleTransferFileService
//                            .OnHandleTransferFileServiceListener() {
//                        @Override
//                        public void currentProcess(int process) {
//                            //更新当前进度
//                            Event<FileEvent> fileEvent = new Event<>(EventBusCode
//                                    .MAIN_2_USER_LIST_FILE_DATA,
//                                    new FileEvent(otherIp, currFileName[0], process));
//                            EventBusUtil.sendEvent(fileEvent);
//                        }
//
//                        @Override
//                        public void fileNameAndLength(String fileName, String fileSize) {
//                            Log.d(TAG, "fileNameAndLength: run");
//                            //有新文件传来，发送给用户列表界面
//                            currFileName[0] = fileName;
//                            ChatData chatData;
//                            User user = UserUtil.readFromInternalStorage(MainActivity.this);
//                            if (user != null) {
//                                chatData = new ChatData(otherIp , TimeUtil.getCurrTime(), fileName,
//                                        fileSize, 0, ChatData.RECEIVE_FILE);
//                            } else {
//                                chatData = null;
//                            }
//                            Event<FileEvent> fileEvent = new Event<>(EventBusCode
//                                    .MAIN_2_USER_LIST_FILE_DATA, new FileEvent(chatData));
//                            EventBusUtil.sendEvent(fileEvent);
//                        }
//                    });
                    service.setOnHandleTransferFileServiceListener(new HandleTransferFileService
                            .OnHandleTransferFileServiceListener() {
                        @Override
                        public void finish(String fileName, String fileSize) {
                            //文件接收完毕
                            ChatData chatData = new ChatData(otherIp, TimeUtil.getCurrTime(), fileName,
                                    fileSize, 100, ChatData.RECEIVE_FILE);
                            Event<FileEvent> fileEvent = new Event<>(EventBusCode
                                    .MAIN_2_USER_LIST_FILE_DATA, new FileEvent(chatData));
                            EventBusUtil.sendEvent(fileEvent);
                        }
                    });
                    new Thread(service).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                transferFileServerSocket.close();
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
        switch (event.getCode()) {
            case EventBusCode.LOGIN_2_MAIN:
            case EventBusCode.REGISTER_2_MAIN:
                mUserList = event.getData().getUserList();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEventCome(Event<LogoutEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.LOGOUT:
                finish();
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

    /**
     * 退出登录并通知其他用户
     */
    private void logoutAndNotify() {
        new Thread(new UpdateClientThread(OtherUserIpUtil.readFromInternalStorage(this)
                .getOtherUserIpList(), new UpdateUser(UserUtil
                .readFromInternalStorage(this).getIpAddress()))).start();
    }

    class UpdateClientThread implements Runnable {

        private List<String> mOtherUserIpList = new ArrayList<>();
        private UpdateUser mUpdateInfo;

        public UpdateClientThread(List<String> mOtherUserIpList, UpdateUser mUpdateInfo) {
            this.mOtherUserIpList = mOtherUserIpList;
            this.mUpdateInfo = mUpdateInfo;
        }

        @Override
        public void run() {
            try {
                //给每个在线用户发出请求
                for (int i = 0; i < mOtherUserIpList.size(); i++) {

                    Log.d(TAG, "UpdateClientThread: " + mOtherUserIpList.get(i));

                    //注意：如果对方没有打开相应端口，会抛出IOException
                    Socket socket = new Socket(mOtherUserIpList.get(i), Constant.UPDATE_PORT);
                    UpdateClient updateClient = new UpdateClient(socket, mUpdateInfo);
                    new Thread(updateClient).start();
                }
            } catch (UnknownHostException e) {
                Log.d(TAG, "UnknownHostException : " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "IOException : " + e.getMessage());
            }
        }
    }
}
