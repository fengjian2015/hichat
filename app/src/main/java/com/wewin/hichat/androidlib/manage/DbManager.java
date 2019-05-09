package com.wewin.hichat.androidlib.manage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.wewin.hichat.model.db.dbHelper.SQLiteHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yst on 2017/6/1.
 * 数据库读写管理，避免大数据是被锁崩溃情况
 */

public class DbManager {

    private static DbManager instance;
    private static SQLiteHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private Handler mHandler = new Handler();
    private Runnable mRunnable= new Runnable() {
        @Override
        public void run() {
            if (mDatabase != null)
                mDatabase.close();
        }
    };

    public static void init(Context context) {
        if (instance == null) {
            instance = new DbManager();
            mDatabaseHelper = new SQLiteHelper(context);
        }
    }

    public static DbManager getInstance() {
        synchronized (DbManager.class) {
            if (instance == null) {
                throw new IllegalStateException(DbManager.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
            }
            return instance;
        }
    }

    public SQLiteDatabase openDatabase(boolean isWritable) {
        synchronized (DbManager.class) {
            mOpenCounter.incrementAndGet();
            mHandler.removeCallbacks(mRunnable);
            try {
                if (isWritable) {
                    mDatabase = mDatabaseHelper.getWritableDatabase();
                } else {
                    mDatabase = mDatabaseHelper.getReadableDatabase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mDatabase;
        }
    }

    public void closeDatabase() {
        synchronized (DbManager.class) {
            try {
                if (mOpenCounter.decrementAndGet() == 0) {
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 60 * 1000);
                } else {
                    mHandler.removeCallbacks(mRunnable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
