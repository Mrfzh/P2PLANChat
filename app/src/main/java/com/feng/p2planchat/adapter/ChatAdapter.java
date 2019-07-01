package com.feng.p2planchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.p2planchat.R;
import com.feng.p2planchat.entity.serializable.ChatData;
import com.feng.p2planchat.util.BitmapUtil;
import com.feng.p2planchat.view.test.TestActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/16
 */
public class ChatAdapter extends RecyclerView.Adapter {

    public static final String TAG = "fzh";

    private Context mContext;
    private List<ChatData> mChatDataList;
    private OnChatAdapterListener mListener;

    public void setOnChatAdapterListener(OnChatAdapterListener onChatAdapterListener) {
        mListener = onChatAdapterListener;
    }

    public interface OnChatAdapterListener {
        void saveWholeBitmap(Bitmap bitmap);
    }

    public ChatAdapter(Context mContext, List<ChatData> mChatDataList) {
        this.mContext = mContext;
        this.mChatDataList = mChatDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ChatData.SEND_TEXT) {
            return new SendTextViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_send_text, null));
        } else if (i == ChatData.RECEIVE_TEXT) {
            return new ReceiveTextViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_receive_text, null));
        } else if (i == ChatData.TIME) {
            return new TimeViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_time, null));
        } else if (i == ChatData.SEND_PICTURE) {
            return new SendPictureViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_send_picture, null));
        } else if (i == ChatData.RECEIVE_PICTURE) {
            return new ReceivePictureViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_receive_picture, null));
        } else if (i == ChatData.SEND_FILE) {
            return new SendFileViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_send_file, null));
        } else if (i == ChatData.RECEIVE_FILE) {
            return new ReceiveFileViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat_receive_file, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
//        Log.d(TAG, "onBindViewHolder: run, i == " + i);
        if (viewHolder instanceof SendTextViewHolder) {
            SendTextViewHolder sendTextViewHolder = (SendTextViewHolder) viewHolder;
            sendTextViewHolder.headImage.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getHeadImage()));
            sendTextViewHolder.content.setText(mChatDataList.get(i).getContent());
        } else if (viewHolder instanceof ReceiveTextViewHolder) {
            ReceiveTextViewHolder receiveTextViewHolder = (ReceiveTextViewHolder) viewHolder;
            receiveTextViewHolder.headImage.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getHeadImage()));
            receiveTextViewHolder.content.setText(mChatDataList.get(i).getContent());
        } else if (viewHolder instanceof TimeViewHolder) {
            TimeViewHolder timeViewHolder = (TimeViewHolder) viewHolder;
            String time = mChatDataList.get(i).getTime();
            String when;
            int hour = Integer.parseInt(time.substring(0, 2));
            if (hour < 12 && hour >= 6) {
                when = "早上";
            } else if (hour < 18 && hour >= 12) {
                when = "下午";
            } else if (hour < 24 && hour >= 18) {
                when = "晚上";
            } else {
                when = "凌晨";
            }
            timeViewHolder.time.setText(when + (hour % 12) + time.substring(2, 5));
        } else if (viewHolder instanceof SendPictureViewHolder) {
            SendPictureViewHolder sendPictureViewHolder = (SendPictureViewHolder) viewHolder;
            sendPictureViewHolder.headImage.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getHeadImage()));
            sendPictureViewHolder.picture.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getPicture()));
        } else if (viewHolder instanceof ReceivePictureViewHolder) {
            ReceivePictureViewHolder receivePictureViewHolder = (ReceivePictureViewHolder) viewHolder;
            receivePictureViewHolder.headImage.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getHeadImage()));
            receivePictureViewHolder.picture.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getPicture()));
            //收到的图片长按可以保持原图
            final XPopup.Builder builder = new XPopup.Builder(mContext)
                    .watchView(receivePictureViewHolder.picture);
            receivePictureViewHolder.picture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    builder.asAttachList(new String[]{"保持原图"}, null,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    //保持原图
                                    mListener.saveWholeBitmap(BitmapUtil.byteArray2Bitmap(
                                            mChatDataList.get(i).getOriginalPicture()));
                                }
                            })
                            .show();
                    return false;
                }
            });
        } else if (viewHolder instanceof SendFileViewHolder) {
            SendFileViewHolder sendFileViewHolder = (SendFileViewHolder) viewHolder;
//            sendFileViewHolder.setIsRecyclable(false);  //禁止复用
            sendFileViewHolder.headImage.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getHeadImage()));
            sendFileViewHolder.fileName.setText(mChatDataList.get(i).getFileName());
            sendFileViewHolder.fileSize.setText(mChatDataList.get(i).getFileSize());

            int currProcess = mChatDataList.get(i).getProcess();
//            if (currProcess != 100) {
//                sendFileViewHolder.process.setProgress(currProcess);
//            } else {
//                sendFileViewHolder.process.setVisibility(View.GONE);
//                sendFileViewHolder.fileSize.setVisibility(View.VISIBLE);
//            }

            sendFileViewHolder.process.setProgress(currProcess);
            sendFileViewHolder.processTv.setText(currProcess + "%");
//            Log.d(TAG, "onBindViewHolder: sendFileViewHolder.process = " + sendFileViewHolder.process.getProgress());
//            if (currProcess == 100) {
//                Log.d(TAG, "onBindViewHolder: mChatDataList.get(i).getProcess() = " + mChatDataList.get(i).getProcess());
//            }
        } else if (viewHolder instanceof ReceiveFileViewHolder) {
            ReceiveFileViewHolder receiveFileViewHolder = (ReceiveFileViewHolder) viewHolder;
            receiveFileViewHolder.headImage.setImageBitmap(BitmapUtil
                    .byteArray2Bitmap(mChatDataList.get(i).getHeadImage()));
            receiveFileViewHolder.fileName.setText(mChatDataList.get(i).getFileName());
            receiveFileViewHolder.fileSize.setText(mChatDataList.get(i).getFileSize());
//            int currProcess = mChatDataList.get(i).getProcess();
//            if (currProcess != 100) {
//                receiveFileViewHolder.process.setProgress(currProcess);
//            } else {
//                receiveFileViewHolder.process.setVisibility(View.GONE);
//                receiveFileViewHolder.fileSize.setVisibility(View.VISIBLE);
//            }
        }
    }

    @Override
    public int getItemCount() {
        return mChatDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //返回该位置的消息类型
        return mChatDataList.get(position).getType();
    }

    class SendTextViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView content;

        public SendTextViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.iv_item_chat_send_text_head_image);
            content = itemView.findViewById(R.id.tv_item_chat_send_text_content);
        }
    }

    class ReceiveTextViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView content;

        public ReceiveTextViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.iv_item_chat_receive_text_head_image);
            content = itemView.findViewById(R.id.tv_item_chat_receive_text_content);
        }
    }

    class TimeViewHolder extends RecyclerView.ViewHolder {

        TextView time;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_item_chat_time_time);
        }
    }

    class SendPictureViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        ImageView picture;

        public SendPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.iv_item_chat_send_picture_head_image);
            picture = itemView.findViewById(R.id.iv_item_chat_send_picture_picture);
        }
    }

    class ReceivePictureViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        ImageView picture;

        public ReceivePictureViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.iv_item_chat_receive_picture_head_image);
            picture = itemView.findViewById(R.id.iv_item_chat_receive_picture_picture);
        }
    }

    class SendFileViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView fileName;
        TextView fileSize;
        ProgressBar process;
        TextView processTv;

        public SendFileViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.iv_item_chat_send_file_head_image);
            fileName = itemView.findViewById(R.id.tv_item_chat_send_file_file_name);
            fileSize = itemView.findViewById(R.id.tv_item_chat_send_file_file_size);
            process = itemView.findViewById(R.id.pb_item_chat_send_file_progress);
            processTv = itemView.findViewById(R.id.tv_item_chat_send_file_process);
        }
    }

    class ReceiveFileViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView fileName;
        TextView fileSize;
//        ProgressBar process;

        public ReceiveFileViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = itemView.findViewById(R.id.iv_item_chat_receive_file_head_image);
            fileName = itemView.findViewById(R.id.tv_item_chat_receive_file_file_name);
            fileSize = itemView.findViewById(R.id.tv_item_chat_receive_file_file_size);
//            process = itemView.findViewById(R.id.pb_item_chat_receive_file_progress);
        }
    }
}
