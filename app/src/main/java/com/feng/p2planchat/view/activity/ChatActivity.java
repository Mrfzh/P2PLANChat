package com.feng.p2planchat.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.feng.p2planchat.client.TransferFileClient;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.config.EventBusCode;
import com.feng.p2planchat.contract.IChatContract;
import com.feng.p2planchat.entity.eventbus.ChatDataEvent;
import com.feng.p2planchat.entity.serializable.ChatData;
import com.feng.p2planchat.entity.eventbus.Event;
import com.feng.p2planchat.entity.serializable.User;
import com.feng.p2planchat.presenter.ChatPresenter;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.FileUtil;
import com.feng.p2planchat.util.IpAddressUtil;
import com.feng.p2planchat.util.PictureUtil;
import com.feng.p2planchat.util.SoftKeyboardUtil;
import com.feng.p2planchat.util.TimeUtil;
import com.feng.p2planchat.util.UserUtil;
import com.feng.p2planchat.view.test.Test4Activity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends BaseActivity<ChatPresenter>
        implements View.OnClickListener, IChatContract.View {

    public static final String TAG = "fzh";
    private static final String KEY_UPDATE_INDEX = "key_update_index";
    private static final int REQUEST_CHOOSE_PHOTO_FROM_ALBUM = 1;
    private static final int REQUEST_CODE_CHOOSE_FILE = 2;
    private static final int MESSAGE_UPDATE_LIST = 10;
    private static final int MESSAGE_UPDATE_ITEM = 11;
    private static final int MESSAGE_TRANSFER_ERROR = 12;

    private TextView mTitleTv;
    private RecyclerView mChatRv;
    private RelativeLayout mMoreFunctionRv;     //更多功能布局，点击更多按钮时弹出
    private ImageView mMoreFunctionIv;          //更多按钮
    private TextView mSendTextTv;               //发送按钮（发送文字）
    private ImageView mBackIv;
    private EditText mInputEt;                  //文字输入
    private ImageView mSendPictureOfAlbumIv;    //从相册中选择图片发送
    private ImageView mSendFileIv;              //发送文件

    private String mOtherIp;        //对方的IP地址
    private String mOtherName;      //对方的用户名
    private List<ChatData> mChatDataList = new ArrayList<>();
    private ChatAdapter mChatAdapter;

    private static String LAST_TIME = "";   //上一发送消息的时间（双方对话间隔3分钟以上时才显示时间）

    private List<TransferFileClientThread> mTransferFileClientThreadList = new ArrayList<>();

    private List<Integer> mIndexList = new ArrayList<>();
//    private int refreshIndex;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_UPDATE_LIST:
                    mChatAdapter.notifyDataSetChanged();
                    //防止软键盘遮挡
                    mChatRv.scrollToPosition(mChatDataList.size() -1);
                    break;
                case MESSAGE_UPDATE_ITEM:
//                    mChatAdapter.notifyItemChanged(refreshIndex);
//                    Log.d(TAG, "handleMessage: msg.getData().getInt(KEY_UPDATE_INDEX) = "
//                            + msg.getData().getInt(KEY_UPDATE_INDEX));
                    mChatAdapter.notifyItemChanged(msg.getData().getInt(KEY_UPDATE_INDEX));
                    break;
                case MESSAGE_TRANSFER_ERROR:
                    showShortToast("传输失败");
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
        return R.layout.activity_chat;
    }

    @Override
    protected ChatPresenter getPresenter() {
        return new ChatPresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mTitleTv = findViewById(R.id.tv_chat_other_name);
        mTitleTv.setText(mOtherName);

        mChatRv = findViewById(R.id.rv_chat_list);
        mChatRv.setLayoutManager(new LinearLayoutManager(this));
        //如果软键盘弹出后遮挡了item，就将RV滑动到最后
        mChatRv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom){
                    mChatRv.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            mChatRv.scrollToPosition(mChatDataList.size() -1);
                        }
                    },100);
                }
            }
        });
        initAdapter();
        mChatRv.setAdapter(mChatAdapter);
        //消息滑动到最后
        mChatRv.scrollToPosition(mChatDataList.size() -1);

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

        mSendPictureOfAlbumIv = findViewById(R.id.iv_chat_more_function_album);
        mSendPictureOfAlbumIv.setOnClickListener(this);

        mSendFileIv = findViewById(R.id.iv_chat_more_function_file);
        mSendFileIv.setOnClickListener(this);
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected void onDestroy() {
        //销毁活动时结束线程
        for (int i = 0; i < mTransferFileClientThreadList.size(); i++) {
            mTransferFileClientThreadList.get(i).close();
        }
        //删除为传输完的文件信息
        Collections.sort(mIndexList);
        Log.d(TAG, "handleMessage: mIndexList = " + mIndexList);
        for (int i = mIndexList.size()-1; i >= 0; i--) {
            int deleteIndex = mIndexList.get(i);
            Log.d(TAG, "handleMessage: deleteIndex = " + deleteIndex);
            mChatDataList.remove((deleteIndex));
        }

        super.onDestroy();
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
        mChatAdapter.setOnChatAdapterListener(new ChatAdapter.OnChatAdapterListener() {
            @Override
            public void saveWholeBitmap(Bitmap bitmap) {
                //保持原图到本地
                if (BitmapUtil.save2Local(bitmap)) {
                    showShortToast("保存成功");
                } else {
                    showShortToast("保存失败");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_chat_more_function:
                //显示更多功能布局
                mMoreFunctionRv.setVisibility(View.VISIBLE);
                //隐藏软键盘
                SoftKeyboardUtil.hideSoftKeyboard(this);
                break;
            case R.id.iv_chat_back:
                SoftKeyboardUtil.hideSoftKeyboard(this);
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
            case R.id.iv_chat_more_function_album:
                //从相册中选择图片发送
                chooseFromAlbum();
                break;
            case R.id.iv_chat_more_function_file:
                //选择文件发送
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
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
        if (LAST_TIME.equals("") || TimeUtil.getTimeInterval(LAST_TIME, currTime) >= 3) {
            mChatDataList.add(new ChatData(chatData.getIp(), currTime));
        }
        LAST_TIME = currTime;
        //添加信息
        mChatDataList.add(chatData);
        mChatAdapter.notifyDataSetChanged();
        //防止软键盘遮挡
        mChatRv.scrollToPosition(mChatDataList.size() -1);
        //发送消息给用户列表界面
        Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.CHAT_2_USER_LIST,
                new ChatDataEvent(mOtherName, mOtherIp, mChatDataList));
        EventBusUtil.sendEvent(chatDataEvent);
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

    /**
     * 发送图片成功
     *
     * @param chatData
     */
    @Override
    public void sendPictureSuccess(ChatData chatData) {
        //更新列表
        //先判断是否需要显示时间
        String currTime = chatData.getTime();
        if (LAST_TIME.equals("") || TimeUtil.getTimeInterval(LAST_TIME, currTime) >= 3) {
            mChatDataList.add(new ChatData(chatData.getIp(), currTime));
        }
        LAST_TIME = currTime;
        //添加信息
        mChatDataList.add(chatData);
        mChatAdapter.notifyDataSetChanged();
        //防止软键盘遮挡
        mChatRv.scrollToPosition(mChatDataList.size() -1);
        //发送消息给用户列表界面
        Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.CHAT_2_USER_LIST,
                new ChatDataEvent(mOtherName, mOtherIp, mChatDataList));
        EventBusUtil.sendEvent(chatDataEvent);
    }

    /**
     * 发送图片失败
     *
     * @param errorMsg
     */
    @Override
    public void sendPictureError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyChatEventCome(Event<ChatDataEvent> event) {
        Log.d(TAG, "onStickyChatEventCome: run");
        switch (event.getCode()) {
            case EventBusCode.USER_LIST_2_CHAT:
                Log.d(TAG, "onStickyChatEventCome(USER_LIST_2_CHAT): run");
                mOtherName = event.getData().getName();
                mOtherIp = event.getData().getIp();
//                mChatDataList = new ArrayList<>(event.getData().getChatDataList());
                mChatDataList = event.getData().getChatDataList();
                if (!mChatDataList.isEmpty()) {
                    //获取上一次聊天时间
                    LAST_TIME = mChatDataList.get(mChatDataList.size() - 1).getTime();
                    //如果这时是在聊天界面，更新消息
                    if (mChatAdapter != null) {
                        Log.d(TAG, "onStickyChatEventCome: 更新列表");
                        mChatAdapter.notifyDataSetChanged();
                        //消息滑动到最后
                        mChatRv.scrollToPosition(mChatDataList.size() -1);
                    }
                } else {
                    Log.d(TAG, "onStickyChatEventCome: mChatDataList.isEmpty()");
                }

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
        User user = UserUtil.readFromInternalStorage(this);
        if (user != null) {
            return new ChatData(user.getIpAddress(), user.getUserName(), BitmapUtil
                    .bitmap2ByteArray(Objects.requireNonNull(BitmapUtil.readFromInternalStorage(
                            user.getUserName() + ".jpg", this))), content,
                    TimeUtil.getCurrTime(), ChatData.SEND_TEXT);
        } else {
            return null;
        }
    }

    /**
     * 得到发送图片消息的ChatData
     *
     * @param bitmap 发送的图片
     * @return
     */
    private ChatData getSendPictureChatData(Bitmap bitmap, Bitmap originalBitmap) {
        User user = UserUtil.readFromInternalStorage(this);
        if (user != null) {
            return new ChatData(user.getIpAddress(), user.getUserName(), BitmapUtil
                    .bitmap2ByteArray(Objects.requireNonNull(BitmapUtil.readFromInternalStorage(
                            user.getUserName() + ".jpg", this))),
                    TimeUtil.getCurrTime(), BitmapUtil.bitmap2ByteArray(bitmap),
                    BitmapUtil.bitmap2ByteArray(originalBitmap), ChatData.SEND_PICTURE);
        } else {
            return null;
        }
    }

    /**
     * 从相册中选择头像
     */
    private void chooseFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_PHOTO_FROM_ALBUM:
                String imagePath;
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号，根据版本号对图片选中的图片进行操作
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上版本
                        assert data != null;
                        imagePath = PictureUtil.handleImageOnKitKat(data, this);
                    } else {
                        assert data != null;
                        imagePath = PictureUtil.handleImageBeforeKitKat(data, this);
                    }
                    sendPicture(imagePath);
                }
                break;
            case REQUEST_CODE_CHOOSE_FILE:
                if (resultCode == RESULT_OK) {  //是否选择，没选择就不会继续
                    Uri uri = data.getData();   //得到uri，后面就是将uri转化成file的过程。
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    actualimagecursor.moveToFirst();
                    String filePath = actualimagecursor.getString(actual_image_column_index);
                    if (filePath == null) {
                        showShortToast("不支持该类型");
                    } else {
                        Log.d(TAG, "onActivityResult: filePath = " + filePath);
                        //获得路径后就是传输File了
                        TransferFileClientThread transferFileClientThread = new TransferFileClientThread(filePath);
                        mTransferFileClientThreadList.add(transferFileClientThread);
                        transferFileClientThread.start();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 发送图片给对方
     */
    private void sendPicture(final String imagePath) {
        //发送图片
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.sendPicture(ChatActivity.this, mOtherIp,
                        getSendPictureChatData(BitmapUtil.compressBitmap(imagePath),
                                BitmapFactory.decodeFile(imagePath)));
            }
        }, 100);
    }


    /**
     * 作为客户端发送文件
     */
    class TransferFileClientThread extends Thread {

        String filePath;
        ChatData chatData;
        TransferFileClient client;
        List<Integer> indexList = new ArrayList<>();    //跟当前文件有关的索引
        int chatDataIndex;

        public TransferFileClientThread(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {

                Socket socket = new Socket(mOtherIp, Constant.FILE_PORT);
                client = new TransferFileClient(socket, filePath);

                client.setOnTransferFileClientListener(new TransferFileClient
                        .OnTransferFileClientListener() {
                    @Override
                    public void currentProcess(int process) {
                        //设置当前进度
                        if (chatData != null) {
                            Log.d(TAG, "currentProcess: process =" + process);
                            chatData.setProcess(process);
//                            mHandler.obtainMessage(MESSAGE_UPDATE_ITEM).sendToTarget();
                            Message message = new Message();
                            message.what = MESSAGE_UPDATE_ITEM;
                            Bundle bundle = new Bundle();
                            bundle.putInt(KEY_UPDATE_INDEX, chatDataIndex);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void fileNameAndLength(String fileName, String fileSize) {
                        //得到文件名和文件长度
                        //先判断是否需要显示时间
                        Log.d(TAG, "fileNameAndLength: run");
                        String currTime = TimeUtil.getCurrTime();
                        if (LAST_TIME.equals("") || TimeUtil.getTimeInterval(LAST_TIME, currTime) >= 3) {
                            indexList.add(mChatDataList.size());
                            mChatDataList.add(new ChatData(UserUtil.readFromInternalStorage(
                                    ChatActivity.this).getIpAddress(), currTime));
                        }
                        LAST_TIME = currTime;
                        //发送文件
                        User user = UserUtil.readFromInternalStorage(ChatActivity.this);
                        if (user != null) {
                            chatData = new ChatData(user.getIpAddress(), user.getUserName(),
                                    user.getHeadImage(), currTime, fileName, fileSize,
                                    0, ChatData.SEND_FILE);
                        }
                        chatDataIndex = mChatDataList.size();
                        indexList.add(chatDataIndex);
                        mIndexList.addAll(indexList);   //添加进可能被删除的item列表
                        mChatDataList.add(chatData);
                        mHandler.obtainMessage(MESSAGE_UPDATE_LIST).sendToTarget();

                    }

                    @Override
                    public void transferSuccess() {
                        mIndexList.removeAll(indexList);    //从可能被删除的item列表移除
                        indexList.clear();
                        mHandler.obtainMessage(MESSAGE_UPDATE_LIST).sendToTarget();

                        //发送消息给用户列表界面
                        Event<ChatDataEvent> chatDataEvent = new Event<>(EventBusCode.CHAT_2_USER_LIST,
                                new ChatDataEvent(mOtherName, mOtherIp, mChatDataList));
                        EventBusUtil.sendEvent(chatDataEvent);
                    }

                    @Override
                    public void transferError() {
                        mHandler.obtainMessage(MESSAGE_TRANSFER_ERROR).sendToTarget();
                    }
                });
                new Thread(client).start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            if (client != null) {
                client.close();
            }
        }
    }
}
