package com.feng.p2planchat.entity.eventbus;

import android.graphics.Bitmap;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public class UpdateHeadImageEvent {

    private Bitmap newHeadImage;

    public UpdateHeadImageEvent(Bitmap newHeadImage) {
        this.newHeadImage = newHeadImage;
    }

    public Bitmap getNewHeadImage() {
        return newHeadImage;
    }

    public void setNewHeadImage(Bitmap newHeadImage) {
        this.newHeadImage = newHeadImage;
    }
}
