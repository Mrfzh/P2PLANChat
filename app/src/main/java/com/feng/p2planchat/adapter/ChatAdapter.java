package com.feng.p2planchat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.entity.data.ChatData;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/16
 */
public class ChatAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ChatData> mChatDataList;

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
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SendTextViewHolder) {
            SendTextViewHolder sendTextViewHolder = (SendTextViewHolder) viewHolder;
            sendTextViewHolder.headImage.setImageBitmap(mChatDataList.get(i).getHeadImage());
            sendTextViewHolder.content.setText(mChatDataList.get(i).getContent());
        } else if (viewHolder instanceof ReceiveTextViewHolder) {
            ReceiveTextViewHolder receiveTextViewHolder = (ReceiveTextViewHolder) viewHolder;
            receiveTextViewHolder.headImage.setImageBitmap(mChatDataList.get(i).getHeadImage());
            receiveTextViewHolder.content.setText(mChatDataList.get(i).getContent());
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
}
