package com.feng.p2planchat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.feng.p2planchat.R;
import com.feng.p2planchat.config.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    /**
     * 将图片存储到内部存储中
     *
     * @param bitmap 图片的bitmap
     * @param name 图片的名字
     */
    public static void save2InternalStorage(Bitmap bitmap, String name, Context context) {
        if (bitmap == null) {
            Log.d(TAG, "save2InternalStorage: bitmap = null");
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d(TAG, "save2InternalStorage: run");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "FileNotFoundException: " + e.toString());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException: " + e.toString());
            }
        }
    }

    /**
     * 从内部存储读取图片
     *
     * @param name 图片名
     * @param context
     * @return 图片的Bitmap
     */
    public static Bitmap readFromInternalStorage(String name, Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(name);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩Bitmap
     *
     * @param imagePath 图片路径
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressBitmap(String imagePath) {
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(imagePath, options);
        int height = options.outHeight;
        int width= options.outWidth;
        int inSampleSize = 1;   // 像素压缩比例，默认不压缩
        int minLen = Math.min(height, width); // 原图的最小边长
        if(minLen > 175) { // 如果原始图像的最小边长大于200dp（此处单位我认为是dp，而非px）
            float ratio = (float)minLen / 175.0f; // 计算像素压缩比例
            inSampleSize = (int)ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例

        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     * @return
     */
    public static boolean save2Local(Bitmap bitmap) {
        //图片保存路径
        String path = Environment.getExternalStorageDirectory().getPath() + "/" +
                Constant.LOCAL_PICTURE_FOLDER_NAME;
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + "/" +
                    System.currentTimeMillis() + ".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
