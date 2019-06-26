package com.feng.p2planchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.entity.data.UserData;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/13
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private List<UserData> mUserDataList;
    private List<Badge> mBadgeList = new ArrayList<>();

    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void clickItem(int position);   //点击item
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public UserAdapter(Context mContext, List<UserData> mUserDataList) {
        this.mContext = mContext;
        this.mUserDataList = mUserDataList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_user, null));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder,
                                 @SuppressLint("RecyclerView") final int i) {
        //点击item
        userViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.clickItem(i);
            }
        });
        //设置文本和图片
        userViewHolder.headImage.setImageBitmap(mUserDataList.get(i).getHeadImage());
        userViewHolder.name.setText(mUserDataList.get(i).getName());
        userViewHolder.content.setText(mUserDataList.get(i).getContent());
        //设置时间
        String time = mUserDataList.get(i).getTime();
        if (time.equals("")) {
            userViewHolder.time.setText(time);
        } else {
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
            userViewHolder.time.setText(when + (hour % 12) + time.substring(2, 5));
        }

        //绑定角标
        if (mBadgeList.size() == i) {
            mBadgeList.add(new QBadgeView(mContext).bindTarget(userViewHolder.badge)
                    .setBadgeNumber(mUserDataList.get(i).getUnreadMessageNum())
                    .setBadgeGravity(Gravity.CENTER));
        } else {
            mBadgeList.get(i).setBadgeNumber(mUserDataList.get(i).getUnreadMessageNum());
        }

    }

    @Override
    public int getItemCount() {
        return mUserDataList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        ImageView headImage;
        TextView name;
        TextView content;
        TextView time;
        View badge;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.rv_item_user_layout);
            headImage = itemView.findViewById(R.id.iv_item_user_head_image);
            name = itemView.findViewById(R.id.tv_item_user_name);
            content = itemView.findViewById(R.id.tv_item_user_content);
            time = itemView.findViewById(R.id.tv_item_user_time);
            badge = itemView.findViewById(R.id.v_item_user_badge);
        }
    }
}
