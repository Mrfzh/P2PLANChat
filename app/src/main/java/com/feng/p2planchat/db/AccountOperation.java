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
                Constant.KEY_NAME + " like ? ", new String[]{name},
                null, null, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    /**
     * 通过账户名获取对应的密码
     *
     * @param name 账户名
     * @return
     */
    public String getPassword(String name) {
        Cursor cursor = mDatabase.query(Constant.TABLE_ACCOUNT, null,
                Constant.KEY_NAME + " like ? ", new String[]{name},
                null, null, null);
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(Constant.KEY_PASSWORD));
        cursor.close();

        return password;
    }

    /**
     * 根据用户名删除对应行
     *
     * @param name
     */
    public void deleteByName(String name) {
        mDatabase.delete(Constant.TABLE_ACCOUNT, Constant.KEY_NAME + " like ? ",
                new String[]{name});
    }

    /**
     * 根据用户名，更新其对应的密码
     *
     * @param name 要修改密码的用户名
     * @param newPassword 新密码
     */
    public void updatePasswordByName(String name, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(Constant.KEY_PASSWORD, newPassword);
        mDatabase.update(Constant.TABLE_ACCOUNT, values,
                Constant.KEY_NAME + " like ? ", new String[]{name});
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
