package com.feng.p2planchat.entity.eventbus;

import android.graphics.Bitmap;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class UpdateOtherHeadImageEvent {

    private String ip;
    private Bitmap newHeadImage;

    public UpdateOtherHeadImageEvent(String ip, Bitmap newHeadImage) {
        this.ip = ip;
        this.newHeadImage = newHeadImage;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Bitmap getNewHeadImage() {
        return newHeadImage;
    }

    public void setNewHeadImage(Bitmap newHeadImage) {
        this.newHeadImage = newHeadImage;
    }
}
