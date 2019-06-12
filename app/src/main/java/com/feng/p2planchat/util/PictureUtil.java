package com.feng.p2planchat.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class PictureUtil {

    /**
     * 处理4.4及以上版本的本地图片
     * @param data onActivityResult方法的第三个参数
     * @return 图片路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String handleImageOnKitKat(Intent data, Context context) {
        String imagePath = null;    //图片路径
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context);
            } else if("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null, context);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri, null, context);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /**
     * 处理4.4以下版本的本地图片
     * @param data onActivityResult方法的第三个参数
     * @return 图片路径
     */
    public static String handleImageBeforeKitKat(Intent data, Context context) {
        Uri uri = data.getData();
        return getImagePath(uri, null, context);
    }

    /**
     * 通过Uri和selection来获取真实的图片路径
     * @param uri
     * @param selection
     * @return
     */
    private static String getImagePath(Uri uri, String selection, Context context) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
