package com.feng.p2planchat.util;

import android.content.Context;

import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.entity.serializable.OtherUserIp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class OtherUserIpUtil {

    /**
     * 将其他在线用户的IP地址写入到本地
     */
    public static void write2InternalStorage(OtherUserIp otherUserIp, Context context) {
        if (otherUserIp == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(Constant.OTHER_USER_IP_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(otherUserIp);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从本地读取其他在线用户的IP地址
     *
     * @param context
     * @return
     */
    public static OtherUserIp readFromInternalStorage(Context context) {
        FileInputStream fis = null;
        OtherUserIp otherUserIp = null;
        try {
            fis = context.openFileInput(Constant.OTHER_USER_IP_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            otherUserIp = (OtherUserIp) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return otherUserIp;
    }
}
