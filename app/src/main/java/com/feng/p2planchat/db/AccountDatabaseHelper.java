package com.feng.p2planchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.feng.p2planchat.config.Constant;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/11
 */
public class AccountDatabaseHelper extends SQLiteOpenHelper {
    //建表语句
    private static final String CREATE_ACCOUNT = "create table " + Constant.TABLE_ACCOUNT +
            " (id integer primary key autoincrement, " + Constant.KEY_NAME + " text, " +
            Constant.KEY_PASSWORD + " text)";

    public AccountDatabaseHelper(Context context, String name,
             SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
