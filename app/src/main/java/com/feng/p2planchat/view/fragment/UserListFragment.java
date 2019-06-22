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
import com.feng.p2planchat.entity.eventbus.ChatEvent;
import com.feng.p2planchat.entity.eventbus.DeleteUserEvent;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.entity.serializable.OtherUserIp;
import com.feng.p2planchat.entity.data.UserData;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UpdateOtherHeadImageEvent;
import com.feng.p2planchat.entity.eventbus.UpdateOtherNameEvent;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.presenter.UserListPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.OtherUserIpUtil;
import com.feng.p2planchat.util.UserUtil;
import com.feng.p2planchat.view.activity.ChatActivity;

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
    private List<UserData> mUserDataList = new ArrayList<>();

    private UserAdapter mUserAdapter;
    //保存当前在线的用户名
    private HashSet<String> mUserNameSet = new HashSet<>();

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
            String content = "";
            String time = "";
            mUserDataList.add(new UserData(headImage, name, ip, content, time));
            mUserNameSet.add(name);
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
        switch (event.getCode()) {
            case EventBusCode.MAIN_2_USER_LIST:
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
                String oldName = event.getData().getOldName();
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    if (curr.getName().equals(oldName)) {
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
                String oldName = event.getData().getOldName();
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    if (curr.getName().equals(oldName)) {
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
                //删除在线用户
                String deleteName = event.getData().getName();
                for (int i = 0; i < mUserDataList.size(); i++) {
                    UserData curr = mUserDataList.get(i);
                    if (curr.getName().equals(deleteName)) {
                        mUserDataList.remove(i);
                        break;
                    }
                }
                //将该用户从在线用户名字集合中删除
                mUserNameSet.remove(deleteName);
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
        switch (event.getCode()) {
            case EventBusCode.CHAT_DATA:
                //收到新消息后
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
                Event<ChatEvent> chatEvent = new Event<>(EventBusCode.USER_LIST_2_CHAT,
                        new ChatEvent(mUserDataList.get(position).getName(),
                                mUserDataList.get(position).getIp()));
                EventBusUtil.sendStickyEvent(chatEvent);
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
        //如果该用户不在列表，添加进列表
        if (!mUserNameSet.contains(user.getUserName())) {
            //更新用户列表
            Bitmap headImage = BitmapUtil.byteArray2Bitmap(user.getHeadImage());
            String name = user.getUserName();
            String ip = user.getIpAddress();
            mUserDataList.add(new UserData(headImage, name, ip, "", ""));
            mUserAdapter.notifyDataSetChanged();
            //隐藏掉无人页面
            mNothingTv.setVisibility(View.GONE);
            //添加到用户名集合中
            mUserNameSet.add(name);

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

        if (userList.size() == 0) {
            mUserDataList = new ArrayList<>();
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
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            if (curr == null) {
                continue;
            }
            if (!mUserNameSet.contains(curr.getUserName())) {
                Bitmap headImage = BitmapUtil.byteArray2Bitmap(curr.getHeadImage());
                String name = curr.getUserName();
                String ip = curr.getIpAddress();
                mUserDataList.add(new UserData(headImage, name, ip, "", ""));
                //添加到用户名集合中
                mUserNameSet.add(name);
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
