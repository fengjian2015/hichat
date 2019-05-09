package com.wewin.hichat.androidlib.datamanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Darren on 2018/8/1.
 */

public class DBHelper extends SQLiteOpenHelper {

    //数据库名称
    private static final String DB_NAME = "cache.db";
    //版本
    private static final int VERSION = 1;
    private OnDbUpgradeListener mUpgradeListener;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public interface OnDbUpgradeListener{
        void dbUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

    public void setOnDbUpgradeListener(OnDbUpgradeListener upgradeListener){
        this.mUpgradeListener = upgradeListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + DBCache.TABLE_NAME + " (keyName text primary key not null, data varchar not null," +
                " duration integer not null, createTime integer not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgradeListener.dbUpgrade(db, oldVersion, newVersion);
    }




}
