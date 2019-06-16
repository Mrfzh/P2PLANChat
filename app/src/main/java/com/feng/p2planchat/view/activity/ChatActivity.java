package com.feng.p2planchat.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.adapter.ChatAdapter;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.entity.data.ChatData;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity {

    private RecyclerView mChatRv;

    private List<ChatData> mChatDataList = new ArrayList<>();

    private ChatAdapter mChatAdapter;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_image2);
        String content = "是宽带连接弗兰克萨积分立刻就开始减肥了房间圣诞快乐老司机快递费收到了科技的烦死了收到了熟练度";
        for (int i = 0; i < 5; i++) {
            mChatDataList.add(new ChatData(bitmap, content, ChatData.SEND_TEXT));
            mChatDataList.add(new ChatData(bitmap, content, ChatData.RECEIVE_TEXT));
        }
    }

    @Override
    protected void initView() {
        mChatRv = findViewById(R.id.rv_chat_list);
        mChatRv.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        mChatRv.setAdapter(mChatAdapter);
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mChatAdapter = new ChatAdapter(this, mChatDataList);
    }
}
