package com.feng.p2planchat.contract;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public interface IPersonalInfoContract {
    interface View {
        void modifyHeadImageSuccess();
        void modifyHeadImageError(String errorMsg);
    }
    interface Presenter {
        void modifyHeadImageSuccess();
        void modifyHeadImageError(String errorMsg);
        void modifyHeadImage(List<String> otherUserIpList, String ip, Bitmap newHeadImage, Context context);
    }
    interface Model {
        void modifyHeadImage(List<String> otherUserIpList, String ip, Bitmap newHeadImage, Context context);
    }
}
