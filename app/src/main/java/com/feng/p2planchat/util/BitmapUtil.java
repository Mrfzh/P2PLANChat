package com.feng.p2planchat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.feng.p2planchat.R;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class BitmapUtil {

    private static final String TAG = "fzh";

    /**
     * 将byte数组转化为bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap byteArray2Bitmap(byte [] data) {
//        //不应该这样设置
//        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(data));
//        return bitmap;

        //一开始返回null，是因为将原Bitmap转化为byte[]时错误，所以byte[]再转换回Bitmap时才会失败。
        //所以其实这句是没有错的，错的是另外一个方法（emmmmm....弄了好久，原来是自己搞错了方向）
        return BitmapFactory.decodeByteArray(data, 0, data.length);

    }

    /**
     * 将Bitmap转化为byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte [] bitmap2ByteArray(Bitmap bitmap) {
//        //一开始这样写，这是错的
//        ByteBuffer buf = ByteBuffer.allocate(bitmap.getByteCount());
//        bitmap.copyPixelsToBuffer(buf);
//        return buf.array();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 通过resId获取Bitmap
     *
     * @param resName
     * @param context
     * @return
     */
    public static Bitmap getBitmapByResId(String resName, Context context) {
        try {
            Field idField = R.drawable.class.getDeclaredField(resName);
            return BitmapFactory.decodeResource(context.getResources(), idField.getInt(idField));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
