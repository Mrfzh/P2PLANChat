package com.feng.p2planchat.view.test;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;

public class Test3Activity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";
    private EditText mMsgEt;
    private Button mSendBtn;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test3;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mMsgEt = findViewById(R.id.et_test3_msg);

        mSendBtn = findViewById(R.id.btn_test3_send);
        mSendBtn.setOnClickListener(this);
    }

    @Override
    protected void doInOnCreate() {
//        //开启服务端
//        new Thread(new ChatServiceThread()).start();
    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test3_send:
////                List<String> otherIpList = OtherUserIpUtil.
////                        readFromInternalStorage(this).getOtherUserIpList();
//                List<String> otherIpList = new ArrayList<>();
//                otherIpList.add("192.168.137.39");
//                otherIpList.add("192.168.137.153");
//                for (int i = 0; i < otherIpList.size(); i++) {
//                    new Thread(new ChatClientThread(otherIpList.get(i),
//                            mMsgEt.getText().toString())).start();
//                }
//
//                break;

                break;
            default:
                break;
        }
    }
//
//    class ChatServiceThread implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                //作为服务端，监听其他用户的登录信息
//                ServerSocket userServerSocket = new ServerSocket(Constant.CHAT_PORT);
//
//                //一直监听客户端
//                while (true) {
//                    Socket socket = userServerSocket.accept();
//                    //处理客户端的请求
//                    HandleChatService service = new HandleChatService(socket);
//                    service.setHandleChatServiceListener(new HandleChatService.HandleChatServiceListener() {
//                        @Override
//                        public void getMessage(String content) {
//                            Log.d(TAG, "getMessage: " + content);
//                        }
//                    });
//                    new Thread(service).start();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    class ChatClientThread implements Runnable {
//
//        private String otherIp;     //对方的IP地址
//        private String content;     //发送的消息
//
//        public ChatClientThread(String otherIp, String content) {
//            this.otherIp = otherIp;
//            this.content = content;
//        }
//
//        @Override
//        public void run() {
//            try {
//                //注意：如果对方没有打开相应端口，会抛出IOException
//                Socket socket = new Socket(otherIp, Constant.CHAT_PORT);
//
//                ChatClient chatClient = new ChatClient(socket, content);
//                new Thread(chatClient).start();
//
//            } catch (UnknownHostException e) {
//                Log.d(TAG, "UnknownHostException : " + e.getMessage());
//            } catch (IOException e) {
//                Log.d(TAG, "IOException : " + e.getMessage());
//            }
//        }
//    }
}
