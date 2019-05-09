package com.wewin.hichat.androidlib.datamanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 数据库缓存
 * Created by Darren on 2018/7/25.
 */

public class DBCache implements DataCache {

    static final String TABLE_NAME = "dbCache";
    private Context context;

    public DBCache(Context context) {
        this.context = context;
    }

    @Override
    public void setData(String key, Object obj, long duration) {
        if (TextUtils.isEmpty(key) || obj == null) {
            return;
        }
        long durationResult;
        if (duration != -1) {
            durationResult = duration / 1000;
        } else {
            durationResult = -1;
        }
        if (obj instanceof String || obj instanceof Integer || obj instanceof Double || obj instanceof Float
                || obj instanceof Short || obj instanceof Long || obj instanceof Boolean) {
            put(key, obj.toString(), durationResult, true);
        }
    }

    @Override
    public void setData(String key, Object obj) {
        setData(key, obj, -1);
    }

    @Override
    public void setObject(String key, Serializable obj, long duration) {
        byte[] bytes = EnDecryptUtil.obj2byteArr(obj);
        long durationResult;
        if (duration != -1) {
            durationResult = duration / 1000;
        } else {
            durationResult = -1;
        }
        if (bytes != null && bytes.length > 0) {
            put(key, EnDecryptUtil.parseByte2HexStr(bytes), durationResult, false);
        }
    }

    @Override
    public void setObject(String key, Serializable obj) {
        setObject(key, obj, -1);
    }

    @Override
    public Object getObject(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String data = get(key, false);
        if (!TextUtils.isEmpty(data)) {
            return EnDecryptUtil.byteArr2Obj(EnDecryptUtil.parseHexStr2Byte(data));
        }
        return null;
    }

    @Override
    public String getString(String key) {
        String data = get(key, true);
        if (!TextUtils.isEmpty(data)) {
            return data;
        }
        return null;
    }

    @Override
    public Boolean getBoolean(String key) {
        String result = getString(key);
        return !TextUtils.isEmpty(result) && Boolean.parseBoolean(getString(key));
    }

    @Override
    public Integer getInteger(String key) {
        String result = getString(key);
        if (!TextUtils.isEmpty(result)) {
            return Integer.parseInt(getString(key));
        }
        return -1;
    }

    @Override
    public Double getDouble(String key) {
        String result = getString(key);
        if (!TextUtils.isEmpty(result)) {
            return Double.parseDouble(getString(key));
        }
        return -1.0;
    }

    @Override
    public Float getFloat(String key) {
        String result = getString(key);
        if (!TextUtils.isEmpty(result)) {
            return Float.parseFloat(getString(key));
        }
        return -1f;
    }

    @Override
    public Long getLong(String key) {
        String result = getString(key);
        if (!TextUtils.isEmpty(result)) {
            return Long.parseLong(getString(key));
        }
        return -1L;
    }

    @Override
    public Short getShort(String key) {
        String result = getString(key);
        if (!TextUtils.isEmpty(result)) {
            return Short.parseShort(getString(key));
        }
        return -1;
    }

    @Override
    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        deleteByKey(key);
    }

    /**
     * 清除缓存
     * @param deleteValid true：删除未过期的数据；false：不删除未过期的数据
     */
    @Override
    public void clear(boolean deleteValid) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        if (!deleteValid) {
            String sql = "delete from " + TABLE_NAME + " where duration != -1 or ? < createTime + duration";
            Object[] args = new Object[]{System.currentTimeMillis()};
            db.execSQL(sql, args);
        } else {
            String sql = "delete from " + TABLE_NAME;
            db.execSQL(sql);
        }
        db.close();
    }

    /*public void setCacheAble(DataCacheAble cacheAble, boolean needEncrypt) {
        put(cacheAble.getCacheKey(), cacheAble.toByteArr(), cacheAble.getSaveTime(), needEncrypt);
    }*/

    private void put(String key, String data, long duration, boolean needEncrypt) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null || TextUtils.isEmpty(key)) {
            return;
        }
        String sql = "replace into " + TABLE_NAME + " (keyName, data, duration, createTime) values (?, ?, ?, ?)";
        Object[] objects;
        if (needEncrypt) {
            byte[] encryptData = EnDecryptUtil.encrypt(data);
            if (encryptData != null && encryptData.length > 0) {
                objects = new Object[]{key, EnDecryptUtil.parseByte2HexStr(encryptData), duration, System.currentTimeMillis()};
            } else {
                objects = null;
            }
        } else {
            objects = new Object[]{key, data, duration, System.currentTimeMillis()};
        }
        if (objects != null) {
            db.execSQL(sql, objects);
        }
        db.close();
    }

    private void deleteByKey(String key) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null || TextUtils.isEmpty(key)) {
            return;
        }
        String sql = "delete from " + TABLE_NAME + " where keyName = ?";
        Object[] objects = new Object[]{key};
        db.execSQL(sql, objects);
        db.close();
    }

    private String get(String key, boolean needDecrypt) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null || TextUtils.isEmpty(key)) {
            return null;
        }
        Cursor cursor = null;
        try {
            String[] columnArr = new String[]{"keyName", "data", "duration", "createTime"};
            cursor = db.query(TABLE_NAME, columnArr, "keyName = ?", new String[]{key}, null, null, null);
            if (!cursor.moveToFirst()) {
                return null;
            }
            String data = cursor.getString(1);
            if (TextUtils.isEmpty(data)) {
                return null;
            }
            long duration = cursor.getLong(2);
            long createTime = cursor.getLong(3);
            long currentTime = System.currentTimeMillis();
            if (duration == -1 || currentTime - createTime <= duration) {
                if (needDecrypt) {
                    byte[] decryptData = EnDecryptUtil.decrypt(EnDecryptUtil.parseHexStr2Byte(data));
                    if (decryptData != null && decryptData.length > 0) {
                        return new String(decryptData);
                    }
                } else {
                    return data;
                }
            } else {
                deleteByKey(key);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return null;
    }


}
