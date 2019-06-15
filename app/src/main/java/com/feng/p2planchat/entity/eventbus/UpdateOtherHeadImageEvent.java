package com.feng.p2planchat.entity.eventbus;

import android.graphics.Bitmap;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateOtherHeadImageEvent {

    private String oldName;
    private Bitmap newHeadImage;

    public UpdateOtherHeadImageEvent(String oldName, Bitmap newHeadImage) {
        this.oldName = oldName;
        this.newHeadImage = newHeadImage;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Bitmap getNewHeadImage() {
        return newHeadImage;
    }

    public void setNewHeadImage(Bitmap newHeadImage) {
        this.newHeadImage = newHeadImage;
    }
}
