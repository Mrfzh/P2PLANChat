package com.feng.p2planchat.util;

import android.content.Context;

import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.entity.bean.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public class UserUtil {
    /**
     * 将自己的用户信息写入到本地
     *
     * @param user
     */
    public static void write2InternalStorage(User user, Context context) {
        if (user == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(Constant.USER_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
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
     * 从本地读取自己的用户信息
     *
     * @param context
     * @return
     */
    public static User readFromInternalStorage(Context context) {
        FileInputStream fis = null;
        User user = null;
        try {
            fis = context.openFileInput(Constant.USER_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (User) ois.readObject();
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

        return user;
    }
}
