package com.feng.p2planchat.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/22
 */
public class TimeUtil {

    /**
     * 获取当前时间，时间格式为HH:mm:ss，例子09:28:43
     *
     * @return
     */
    public static String getCurrTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 计算两个时间的间隔，返回的单位为分钟
     *
     * @param oldTime 格式为HH:mm:ss
     * @param newTime 格式为HH:mm:ss
     * @return
     */
    public static int getTimeInterval(String oldTime, String newTime) {
        int oldHour = Integer.parseInt(oldTime.substring(0, 2));
        int newHour = Integer.parseInt(newTime.substring(0, 2));
        if (newHour < oldHour) {
            newHour += 12;
        }
        int oldMinute = Integer.parseInt(oldTime.substring(3, 5));
        int newMinute = Integer.parseInt(newTime.substring(3, 5));
        int oldSecond = Integer.parseInt(oldTime.substring(6, 8));
        int newSecond = Integer.parseInt(newTime.substring(6, 8));

        long oldTotalSecond = (oldHour * 60 + oldMinute) * 60 + oldSecond;
        long newTotalSecond = (newHour * 60 + newMinute) * 60 + newSecond;

        return (int) ((newTotalSecond - oldTotalSecond) / 60);
    }
}
