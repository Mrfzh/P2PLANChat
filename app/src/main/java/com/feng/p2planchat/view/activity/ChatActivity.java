package com.feng.p2planchat.view.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.adapter.ChatAdapter;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IChatContract;
import com.feng.p2planchat.entity.eventbus.ChatDataEvent;
import com.feng.p2planchat.entity.serializable.ChatData;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.presenter.ChatPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.TimeUtil;
import com.feng.p2planchat.util.UserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity<ChatPresenter>
        implements View.OnClickListener, IChatContract.View {

    public static final String TAG = "fzh";

    private TextView mTitleTv;
    private RecyclerView mChatRv;
    private RelativeLayout mMoreFunctionRv;     //更多功能布局，点击更多按钮时弹出
    private ImageView mMoreFunctionIv;          //更多按钮
    private TextView mSendTextTv;               //发送按钮（发送文字）
    private ImageView mBackIv;
    private EditText mInputEt;                  //文字输入

    private Bitmap mOwnHeadImage;   //自己的头像
    private String mOtherIp;        //对方的IP地址
    private String mOtherName;      //对方的用户名
    private List<ChatData> mChatDataList = new ArrayList<>();
    private ChatAdapter mChatAdapter;

    private String mLastTime = "";   //上一发送消息的时间（双方对话间隔3分钟以上时才显示时间）

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected ChatPresenter getPresenter() {
        return new ChatPresenter();
    }

    @Override
    protected void initData() {
        mOwnHeadImage = BitmapUtil.readFromInternalStorage(UserUtil
                .readFromInternalStorage(this).getUserName() + ".jpg", this);
    }

    @Override
    protected void initView() {
        mTitleTv = findViewById(R.id.tv_chat_other_name);
        mTitleTv.setText(mOtherName);

        mChatRv = findViewById(R.id.rv_chat_list);
        mChatRv.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        mChatRv.setAdapter(mChatAdapter);

        mMoreFunctionRv = findViewById(R.id.rv_chat_more_function);

        mMoreFunctionIv = findViewById(R.id.iv_chat_more_function);
        mMoreFunctionIv.setOnClickListener(this);

        mSendTextTv = findViewById(R.id.tv_chat_send_text);
        mSendTextTv.setOnClickListener(this);

        mBackIv = findViewById(R.id.iv_chat_back);
        mBackIv.setOnClickListener(this);

        mInputEt = findViewById(R.id.et_chat_input_text);
        //监听输入
        mInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String currInput = s.toString();
                if (currInput.equals("")) {
                    //隐藏发送按钮
                    mSendTextTv.setVisibility(View.GONE);
                } else {
                    //隐藏更多功能布局
                    mMoreFunctionRv.setVisibility(View.GONE);
                    //显示发送按钮
                    mSendTextTv.setVisibility(View.VISIBLE);
                }
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_chat_more_function:
                //显示更多功能布局
                mMoreFunctionRv.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_chat_back:
                finish();
                break;
            case R.id.tv_chat_send_text:
                final String message = mInputEt.getText().toString();
                //清除输入
                mInputEt.setText("");
                //发送消息
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.sendText(ChatActivity.this, mOtherIp, getSendTextChatData(message));
                    }
                }, 100);
                break;
            default:
                break;
        }
    }

    /**
     * 发送消息成功
     */
    @Override
    public void sendTextSuccess(ChatData chatData) {
        //更新列表
        //先判断是否需要显示时间
        String currTime = chatData.getTime();
        if (mLastTime.equals("") || TimeUtil.getTimeInterval(mLastTime, currTime) >= 3) {
            mChatDataList.add(new ChatData(chatData.getIp(), currTime));
        }
        mLastTime = currTime;
        //添加信息
        mChatDataList.add(chatData);
        mChatAdapter.notifyDataSetChanged();
//        //发送消息给用户列表界面
//        Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.CHAT_2_USER_LIST,
//                new ChatDataEvent(mChatDataList));
//        EventBusUtil.sendEvent(chatDataEvent);
    }

    /**
     * 发送消息失败
     *
     * @param errorMsg
     */
    @Override
    public void sendTextError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyChatEventCome(Event<ChatDataEvent> event) {
        Log.d(TAG, "onStickyChatEventCome: run");
        Log.d(TAG, "onStickyChatEventCome: event.getCode()" + event.getCode());
        switch (event.getCode()) {
            case EventBusCode.USER_LIST_2_CHAT:
                Log.d(TAG, "onStickyChatEventCome: run 2");
                mOtherName = event.getData().getName();
                mOtherIp = event.getData().getIp();
                System.out.println("mOtherName = " + mOtherName + ", mOtherIp = " + mOtherIp);
                mChatDataList = event.getData().getChatDataList();
                break;
            default:
                break;
        }
    }

    /**
     * 得到发送文字消息的ChatData
     *
     * @param content 发送的文字消息
     * @return
     */
    private ChatData getSendTextChatData(String content) {
        return new ChatData(UserUtil.readFromInternalStorage(this).getIpAddress(),
                UserUtil.readFromInternalStorage(this).getUserName(),
                BitmapUtil.bitmap2ByteArray(mOwnHeadImage), content,
                TimeUtil.getCurrTime(), ChatData.SEND_TEXT);
    }
}
