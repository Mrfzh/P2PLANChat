package com.feng.p2planchat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feng.p2planchat.config.Constant;

import java.util.HashMap;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class AccountOperation {
    private SQLiteDatabase mDatabase;

    public AccountOperation(SQLiteDatabase mDatabase) {
        this.mDatabase = mDatabase;
    }

    /**
     * 添加一组账号密码
     *
     * @param name 账户名
     * @param password 密码
     */
    public void add(String name, String password) {
        ContentValues cv = new ContentValues();
        cv.put(Constant.KEY_NAME, name);
        cv.put(Constant.KEY_PASSWORD, password);
        mDatabase.insert(Constant.TABLE_ACCOUNT, null, cv);
        cv.clear();
    }

    /**
     * 表中是否已有该账户
     *
     * @param name 账户名
     * @return
     */
    public boolean hasName(String name) {
        Cursor cursor = mDatabase.query(Constant.TABLE_ACCOUNT, new String[]{Constant.KEY_NAME},
                Constant.KEY_NAME + " like ? ", new String[]{name}, null, null, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    /**
     * 显示表中的所有数据
     *
     * @return 返回的HashMap：key为name，value为password
     */
    public HashMap<String, String> showAll() {
        Cursor cursor = mDatabase.query(Constant.TABLE_ACCOUNT, null, null,
                null, null, null, null);
        HashMap<String, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(Constant.KEY_NAME));
                String password = cursor.getString(cursor.getColumnIndex(Constant.KEY_PASSWORD));
                result.put(name, password);
            } while (cursor.moveToNext());
        }
        //记得关闭Cursor
        cursor.close();

        return result;
    }
}
