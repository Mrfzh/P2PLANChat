package com.feng.p2planchat.view.fragment;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.adapter.UserAdapter;
import com.feng.p2planchat.base.BaseFragment;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IUserListContract;
import com.feng.p2planchat.entity.eventbus.ChatDataEvent;
import com.feng.p2planchat.entity.eventbus.DeleteUserEvent;
import com.feng.p2planchat.entity.serializable.ChatData;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.entity.serializable.OtherUserIp;
import com.feng.p2planchat.entity.data.UserData;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UpdateOtherHeadImageEvent;
import com.feng.p2planchat.entity.eventbus.UpdateOtherNameEvent;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.presenter.UserListPresenter;
import com.feng.p2planchat.util.ActivityUtil;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.OtherUserIpUtil;
import com.feng.p2planchat.util.TimeUtil;
import com.feng.p2planchat.util.UserUtil;
import com.feng.p2planchat.view.activity.ChatActivity;
import com.feng.p2planchat.view.activity.MainActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/9
 */
public class UserListFragment extends BaseFragment<UserListPresenter>
        implements View.OnClickListener, IUserListContract.View {

    private static final String TAG = "fzh";

    private TextView mNothingTv;
    private ImageView mRefreshIv;
    private RecyclerView mListRv;
    private ProgressBar mProgressBar;

    private List<User> mUserList = new ArrayList<>();

    //用户聊天信息集合
    private List<UserData> mUserDataList = new ArrayList<>();

    private UserAdapter mUserAdapter;
    //保存当前在线的用户名的IP地址
    private HashSet<String> mUserIpSet = new HashSet<>();

    @Override
    protected void initData() {

        //初始化用户列表数据
        for (int i = 0; i < mUserList.size(); i++) {
            User currUser = mUserList.get(i);
            if (currUser == null) {
                break;
            }
            Bitmap headImage = BitmapUtil.byteArray2Bitmap(currUser.getHeadImage());
            String name = currUser.getUserName();
            String ip = currUser.getIpAddress();
            mUserDataList.add(new UserData(headImage, name, ip));
            mUserIpSet.add(ip);
        }
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected void initView() {
        mRefreshIv = getActivity().findViewById(R.id.iv_user_list_refresh);
        Log.d(TAG, "mRefreshIv: " + mRefreshIv);
        mRefreshIv.setOnClickListener(this);

        mListRv = getActivity().findViewById(R.id.rv_user_list_list);
        mListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        initAdapter();
        mListRv.setAdapter(mUserAdapter);

        mNothingTv = getActivity().findViewById(R.id.tv_user_list_nobody);
        if (mUserDataList.size() == 0) {
            mNothingTv.setVisibility(View.VISIBLE);
        }

        mProgressBar = getActivity().findViewById(R.id.pb_user_list_progress_bar);
    }

    @Override
    protected UserListPresenter getPresenter() {
        return new UserListPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_list_refresh:
                //更新用户列表
                mProgressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 300);
                break;
            default:
                break;
        }
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
    public void onStickyUserListEventCome(Event<UserListEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.MAIN_2_USER_LIST:
                if (!event.getData().isOneUser()) {
                    //其他用户列表
                    mUserList = event.getData().getUserList();
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserListEventCome(Event<UserListEvent> event) {
        Log.d(TAG, "onUserListEventCome: run");
        switch (event.getCode()) {
            case EventBusCode.MAIN_2_USER_LIST:
                Log.d(TAG, "onUserListEventCome(EventBusCode.MAIN_2_USER_LIST): run");
                if (event.getData().isOneUser()) {
                    //有新用户上线
                    hasNewUser(event.getData().getNewUser());
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOtherNameEventCome(Event<UpdateOtherNameEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.UPDATE_OTHER_NAME:
                //更新在线用户的用户名
                String ip = event.getData().getIp();
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    if (curr.getIp().equals(ip)) {
                        curr.setName(event.getData().getNewName());
                        break;
                    }
                }
                mUserAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOtherHeadImageEventCome(Event<UpdateOtherHeadImageEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.UPDATE_OTHER_HEAD_IMAGE:
                //更新在线用户的头像
                String ip = event.getData().getIp();
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    if (curr.getIp().equals(ip)) {
                        curr.setHeadImage(event.getData().getNewHeadImage());
                        break;
                    }
                }
                mUserAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteUserEventCome(Event<DeleteUserEvent> event) {
        switch (event.getCode()) {
            case EventBusCode.DELETE_USER:
                //根据ip地址删除在线用户
                String deleteIp = event.getData().getIp();
                //将该用户从在线用户名字集合中删除
                mUserIpSet.remove(deleteIp);
                for (int i = 0; i < mUserDataList.size(); i++) {
                    if (mUserDataList.get(i).getIp().equals(deleteIp)) {
                        mUserDataList.remove(i);
                        break;
                    }
                }
                //更新列表
                mUserAdapter.notifyDataSetChanged();
                if (mUserDataList.size() == 0) {
                    //显示无人页面
                    mNothingTv.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatDataEventCome(Event<ChatDataEvent> event) {
        Log.d(TAG, "onChatDataEventCome: run");
        switch (event.getCode()) {
            case EventBusCode.MAIN_2_USER_LIST_CHAT_DATA:
                Log.d(TAG, "onChatDataEventCome(case EventBusCode.MAIN_2_USER_LIST): run");
                //接收单条消息
                if (!event.getData().isOneData()) {
                    break;
                }
                //收到新消息后（文本信息）
                ChatData chatData = event.getData().getChatData();
                //更新用户列表的最新消息和时间
                String ip = chatData.getIp();
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    if (curr.getIp().equals(ip)) {
                        Log.d(TAG, "onChatDataEventCome: chatData.getTime() = " + chatData.getTime());
                        Log.d(TAG, "onChatDataEventCome: chatData.getContent() = " + chatData.getContent());
                        curr.setTime(chatData.getTime());
                        curr.setContent(chatData.getContent());

                        //只要当前界面不是聊天界面，都更新消息提醒数
                        if (ActivityUtil.isActivityTop(ChatActivity.class, getContext())) {
                            curr.setUnreadMessageNum(0);
                        } else {
                            curr.setUnreadMessageNum(curr.getUnreadMessageNum() + 1);
                        }

                        //更新聊天消息
                        List<ChatData> list = curr.getChatDataList();
                        //判断是否要显示时间
                        if (list.size() == 0 || TimeUtil.getTimeInterval(list.get(list.size()-1).getTime()
                                , chatData.getTime()) >= 3) {
                            list.add(new ChatData(chatData.getIp(), chatData.getTime()));
                        }
                        //将消息设置为接收类型
                        chatData.setType(ChatData.RECEIVE_TEXT);
                        list.add(chatData);

                        mUserAdapter.notifyDataSetChanged();

                        Log.d(TAG, "onChatDataEventCome: run 2");

                        Log.d(TAG, "onChatDataEventCome: curr.getContent = " + curr.getContent());
                        Log.d(TAG, "onChatDataEventCome: curr.getTime = " + curr.getTime());

                        //将新消息发送给聊天界面
                        Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.USER_LIST_2_CHAT,
                                new ChatDataEvent(chatData.getName(), chatData.getIp(), list));
                        EventBusUtil.sendStickyEvent(chatDataEvent);

                        break;
                    }
                }
//                mUserAdapter.notifyDataSetChanged();
                break;
            case EventBusCode.CHAT_2_USER_LIST:
                Log.d(TAG, "onChatDataEventCome(case EventBusCode.CHAT_2_USER_LIST): run");
                //接收多条消息
                if (event.getData().isOneData()) {
                    break;
                }
                //收到新消息后（文本信息）
                List<ChatData> chatDataList = event.getData().getChatDataList();
                //更新用户列表的最新消息和时间
                ChatData newChatData = chatDataList.get(chatDataList.size() - 1);
                Log.d(TAG, "onChatDataEventCome: newIp = " + event.getData().getIp());
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    //找到当前聊天对象后，更新其用户信息
                    if (curr.getIp().equals(event.getData().getIp())) {
                        curr.setTime(newChatData.getTime());
                        curr.setContent(newChatData.getContent());
                        curr.setChatDataList(chatDataList);
                        break;
                    }
                }
                mUserAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mUserAdapter = new UserAdapter(getContext(), mUserDataList);
        
        mUserAdapter.setOnClickListener(new UserAdapter.OnClickListener() {
            @Override
            public void clickItem(int position) {
                //点击item后，和对方进行聊天

                UserData userData = mUserDataList.get(position);
                Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.USER_LIST_2_CHAT,
                        new ChatDataEvent(userData.getName(), userData.getIp(), userData.getChatDataList()));
                EventBusUtil.sendStickyEvent(chatDataEvent);

                //清除消息提醒
                mUserDataList.get(position).setUnreadMessageNum(0);
                mUserAdapter.notifyDataSetChanged();

                jump2Activity(ChatActivity.class);
            }
        });
    }

    /**
     * 有新用户上线，更新用户列表
     *
     * @param user 用户信息
     */
    private void hasNewUser(User user) {
        Log.d(TAG, "hasNewUser: run");
        Log.d(TAG, "hasNewUser: user = " + user);
        Log.d(TAG, "hasNewUser: mUserIpSet = " + mUserIpSet);
        Log.d(TAG, "hasNewUser: user.getIpAddress = " + user.getIpAddress());
        //如果该用户不在列表，添加进列表
        if (!mUserIpSet.contains(user.getIpAddress())) {
            Log.d(TAG, "hasNewUser: run 1");
            //更新用户列表
            Bitmap headImage = BitmapUtil.byteArray2Bitmap(user.getHeadImage());
            String name = user.getUserName();
            String ip = user.getIpAddress();
            UserData userData = new UserData(headImage, name, ip);
            Log.d(TAG, "hasNewUser: userData = " + userData);
            mUserDataList.add(userData);
            mUserAdapter.notifyDataSetChanged();
            //隐藏掉无人页面
            mNothingTv.setVisibility(View.GONE);
            //添加到用户名集合中
            mUserIpSet.add(ip);

            //将新用户的IP地址写入本地
            List<String> otherUserIpList = OtherUserIpUtil
                    .readFromInternalStorage(getContext()).getOtherUserIpList();
            otherUserIpList.add(user.getIpAddress());
            OtherUserIpUtil.write2InternalStorage(new OtherUserIp(otherUserIpList), getContext());
        }
    }

    /**
     * 重新发起请求，查找在线用户，并更新用户列表
     */
    private void refresh() {
       mPresenter.findOtherUser(UserUtil.readFromInternalStorage(
               Objects.requireNonNull(getContext())), getContext());
    }

    /**
     * 查找其他在线用户成功
     *
     * @param userList 其他在线用户
     */
    @Override
    public void findOtherUserSuccess(List<User> userList) {
        mProgressBar.setVisibility(View.GONE);

        Log.d(TAG, "findOtherUserSuccess: userList = " + userList);

        if (userList.size() == 0) {
            //重置集合
            mUserDataList.clear();
            mUserIpSet.clear();
            //更新列表
            mUserAdapter.notifyDataSetChanged();
            //显示无人页面
            mNothingTv.setVisibility(View.VISIBLE);
            return;
        }

        //将其他用户的IP地址写入本地
        List<String> otherUserIpList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            if (curr == null) {
                continue;
            }
            otherUserIpList.add(curr.getIpAddress());
        }
        OtherUserIpUtil.write2InternalStorage(new OtherUserIp(otherUserIpList), getContext());

        //将不在列表的用户添加进列表
        Log.d(TAG, "findOtherUserSuccess: mUserIpSet = " + mUserIpSet);
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            if (curr == null) {
                continue;
            }
            if (!mUserIpSet.contains(curr.getIpAddress())) {
                Bitmap headImage = BitmapUtil.byteArray2Bitmap(curr.getHeadImage());
                String name = curr.getUserName();
                String ip = curr.getIpAddress();
                mUserDataList.add(new UserData(headImage, name, ip));
                //添加到用户名集合中
                mUserIpSet.add(name);
            }
        }

        //更新用户列表
        mUserAdapter.notifyDataSetChanged();
        //隐藏掉无人页面
        mNothingTv.setVisibility(View.GONE);
    }

    /**
     * 查找其他在线用户失败
     *
     * @param errorMsg
     */
    @Override
    public void findOtherUserError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        showShortToast(errorMsg);
    }
}
