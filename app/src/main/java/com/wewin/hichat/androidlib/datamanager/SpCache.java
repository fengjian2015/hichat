package com.wewin.hichat.androidlib.datamanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.wewin.hichat.androidlib.utils.LogUtil;

import java.io.Serializable;

/**
 * SharedPreferences缓存
 * Created by Darren on 2018/7/31.
 */

public class SpCache implements DataCache {

    private String fileName = "spCache";
    private Context context;

    public SpCache(Context context) {
        this.context = context;
    }

    public SpCache(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    public void setData(String key, Object obj, long duration) {
        if (TextUtils.isEmpty(key) || obj == null) {
            return;
        }
        if (obj instanceof String || obj instanceof Integer || obj instanceof Double || obj instanceof Float
                || obj instanceof Short || obj instanceof Long || obj instanceof Boolean) {
            SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            byte[] encryptData = EnDecryptUtil.encrypt(obj.toString());
            if (encryptData != null && encryptData.length > 0) {
                editor.putString(key, EnDecryptUtil.parseByte2HexStr(encryptData));
            }
            editor.apply();
        }
    }

    @Override
    public void setData(String key, Object obj) {
        setData(key, obj, -1);
    }

    @Override
    public void setObject(String key, Serializable obj, long duration) {
        if (TextUtils.isEmpty(key) || obj == null || context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        byte[] byteArr = EnDecryptUtil.obj2byteArr(obj);
        if (byteArr != null) {
            editor.putString(key, EnDecryptUtil.parseByte2HexStr(byteArr));
            editor.apply();
        } else {
            LogUtil.i("EnDecryptUtil.obj2byteArr(obj) == null");
        }
    }

    @Override
    public void setObject(String key, Serializable obj) {
        setObject(key, obj, -1);
    }

    @Override
    public Object getObject(String key) {
        if (TextUtils.isEmpty(key) || context == null) {
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String result = sp.getString(key, null);
        if (!TextUtils.isEmpty(result)) {
            return EnDecryptUtil.byteArr2Obj(EnDecryptUtil.parseHexStr2Byte(result));
        }
        return null;
    }

    @Override
    public String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String result = sp.getString(key, null);
        if (!TextUtils.isEmpty(result)) {
            byte[] decryptData = EnDecryptUtil.decrypt(EnDecryptUtil.parseHexStr2Byte(result));
            if (decryptData != null && decryptData.length > 0) {
                return new String(decryptData);
            }
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

    }

    @Override
    public void clear(boolean deleteValid) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
    }


}
