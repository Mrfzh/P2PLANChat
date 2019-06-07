package com.feng.p2planchat.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public class ToastUtil {

    private static Toast toast = null;

    //多次点击只会显示最新一次的Toast
    public static void showToast(Context context,String text) {
        if (toast == null) {
            toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
            toast.setText(text);    //这样写是为了解决小米手机显示app名的问题
            toast.show();
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
