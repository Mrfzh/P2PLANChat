package com.feng.p2planchat.view.fragment;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.adapter.UserAdapter;
import com.feng.p2planchat.base.BaseFragment;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.entity.data.UserData;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.eventbus.UserListEvent;
import com.feng.p2planchat.util.BitmapUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/9
 */
public class UserListFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "fzh";

    private TextView mNothingTv;
    private ImageView mRefreshIv;
    private RecyclerView mListRv;

    private List<User> mUserList = new ArrayList<>();
    private List<UserData> mUserDataList = new ArrayList<>();

    private UserAdapter mUserAdapter;

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
            String content = "";
            String time = "";
            mUserDataList.add(new UserData(headImage, name, content, time));
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
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_list_refresh:
                //更新用户列表
                refresh();
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
                mUserList = event.getData().getUserList();
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
     * 更新用户列表
     */
    private void refresh() {

    }
}
