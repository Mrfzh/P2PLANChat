package com.feng.p2planchat.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/29
 */
public class FileUtil {

    /**
     * 格式化文件大小
     *
     * @param length
     * @return
     */
    public static String getFormatFileSize(long length) {
        DecimalFormat df;
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);

        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }
}
