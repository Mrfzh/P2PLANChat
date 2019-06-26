package com.feng.p2planchat.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/26
 */
public class ActivityUtil {

    /**
     * 判断某activity是否处于栈顶
     *
     * @return true在栈顶 false不在栈顶
     */
    public static boolean isActivityTop(Class cls, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }

}
