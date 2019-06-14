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
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IUserListContract;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.entity.data.UserData;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.presenter.UserListPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.UserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.FloatBuffer;
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

    private User mOwnInfo;      //自己的用户信息
    private List<User> mUserList = new ArrayList<>();
    private List<UserData> mUserDataList = new ArrayList<>();

    private UserAdapter mUserAdapter;
    private HashSet<String> mUserNameSet = new HashSet<>();

    @Override
    protected void initData() {
        //从本地获取用户信息
        mOwnInfo = UserUtil.readFromInternalStorage(Objects.requireNonNull(getContext()));

        //初始化用户列表数据
        for (int i = 0; i < mUserList.size(); i++) {
            User currUser = mUserList.get(i);
            if (currUser == null) {
                break;
            }
            Bitmap headImage = BitmapUtil.byteArray2Bitmap(currUser.getHeadImage());
            String name = currUser.getUserName();
            String content = "";
            String time = "";
            mUserDataList.add(new UserData(headImage, name, content, time));
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
    public void onStickyEventBusCome(Event<UserListEvent> event) {
        Log.d(TAG, "onStickyEventBusCome(userlist): run");
        Log.d(TAG, "onStickyEventBusCome(userlist): event.getCode() = " + event.getCode());
        Log.d(TAG, "onStickyEventBusCome(userlist): event.getData() = " + event.getData());
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
    public void onEventBusCome(Event<UserListEvent> event) {
        Log.d(TAG, "onEventBusCome(userlist): run");
        Log.d(TAG, "onEventBusCome(userlist): event.getCode() = " + event.getCode());
        Log.d(TAG, "onEventBusCome(userlist): event.getData() = " + event.getData());
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

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mUserAdapter = new UserAdapter(getContext(), mUserDataList);
        mUserAdapter.setOnClickListener(new UserAdapter.OnClickListener() {
            @Override
            public void clickItem(int position) {
                //item的点击事件
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
            mUserDataList.add(new UserData(headImage, name, "", ""));
            mUserAdapter.notifyDataSetChanged();
            //隐藏掉无人页面
            mNothingTv.setVisibility(View.GONE);
            //添加到用户名集合中
            mUserNameSet.add(name);
        }
    }

    /**
     * 重新发起请求，查找在线用户，并更新用户列表
     */
    private void refresh() {
       mPresenter.findOtherUser(mOwnInfo, getContext());
    }

    /**
     * 查找其他在线用户成功
     *
     * @param userList 其他在线用户
     */
    @Override
    public void findOtherUserSuccess(List<User> userList) {
        mProgressBar.setVisibility(View.GONE);
        //将不在列表的用户添加进列表
        for (int i = 0; i < userList.size(); i++) {
            User curr = userList.get(i);
            if (curr == null) {
                continue;
            }
            if (!mUserNameSet.contains(curr.getUserName())) {
                Bitmap headImage = BitmapUtil.byteArray2Bitmap(curr.getHeadImage());
                String name = curr.getUserName();
                mUserDataList.add(new UserData(headImage, name, "", ""));
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
