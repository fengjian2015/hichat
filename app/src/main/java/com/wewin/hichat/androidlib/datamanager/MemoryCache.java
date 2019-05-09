package com.wewin.hichat.androidlib.datamanager;

import android.support.v4.util.LruCache;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * 内存缓存
 * Created by Darren on 2018/7/25.
 */

public class MemoryCache implements DataCache {

    private static final int DEFAULT_MAX_SIZE = 1024 * 1024;
    private LruCache<String, DataItem> mLruCache;

    public MemoryCache() {
        this(DEFAULT_MAX_SIZE);
    }

    public MemoryCache(int maxSize) {
        mLruCache = new LruCache<String, DataItem>(maxSize) {
            @Override
            protected int sizeOf(String key, DataItem value) {
                if (value == null || value.mData == null) {
                    return 0;
                }
                return value.mData.length;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, DataItem oldValue, DataItem newValue) {
                System.out.println("memory cache entryRemoved");
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
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
            mLruCache.put(key, new DataItem(obj.toString().getBytes(), durationResult));
        }
    }

    @Override
    public void setData(String key, Object obj){
        setData(key, obj, -1);
    }

    @Override
    public void setObject(String key, Serializable obj, long duration) {
        if (TextUtils.isEmpty(key) || obj == null) {
            return;
        }
        long durationResult;
        if (duration != -1) {
            durationResult = duration / 1000;
        } else {
            durationResult = -1;
        }
        mLruCache.put(key, new DataItem(EnDecryptUtil.obj2byteArr(obj), durationResult));
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
        byte[] data = get(key);
        if (data != null && data.length > 0) {
            return EnDecryptUtil.byteArr2Obj(data);
        }
        return null;
    }

    @Override
    public String getString(String key) {
        byte[] data = get(key);
        if (data != null && data.length > 0) {
            return new String(data);
        }
        return null;
    }

    @Override
    public Boolean getBoolean(String key) {
        return !TextUtils.isEmpty(getString(key)) && Boolean.parseBoolean(getString(key));
    }

    @Override
    public Integer getInteger(String key) {
        if (!TextUtils.isEmpty(getString(key))) {
            return Integer.parseInt(getString(key));
        }
        return -1;
    }

    @Override
    public Double getDouble(String key) {
        if (!TextUtils.isEmpty(getString(key))) {
            return Double.parseDouble(getString(key));
        }
        return -1.0;
    }

    @Override
    public Float getFloat(String key) {
        if (!TextUtils.isEmpty(getString(key))) {
            return Float.parseFloat(getString(key));
        }
        return -1.0f;
    }

    @Override
    public Long getLong(String key) {
        if (!TextUtils.isEmpty(getString(key))) {
            return Long.parseLong(getString(key));
        }
        return -1L;
    }

    @Override
    public Short getShort(String key) {
        if (!TextUtils.isEmpty(getString(key))) {
            return Short.parseShort(getString(key));
        }
        return -1;
    }

    @Override
    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mLruCache.remove(key);
    }

    /**
     * 清除缓存
     * @param deleteValid true：删除未过期的数据；false：不删除未过期的数据
     */
    @Override
    public void clear(boolean deleteValid) {
        if (deleteValid){
            mLruCache.trimToSize(-1);
        }else {
            Map<String, DataItem> map = mLruCache.snapshot();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry)iterator.next();
                DataItem item = (DataItem) entry.getValue();
                if (!item.isValid()){
                    iterator.remove();
                }
            }
        }
    }

    private class DataItem {
        //缓存数据
        private byte[] mData;
        //缓存时长，-1表示永久
        private long mDuration;
        //缓存创建时间
        private long mCreateTime;

        DataItem(byte[] data, long saveTime) {
            this.mData = data;
            this.mDuration = saveTime;
            this.mCreateTime = System.currentTimeMillis();
        }

        private boolean isValid() {
            return mDuration == -1 || System.currentTimeMillis() <= mCreateTime + mDuration;
        }
    }

    private byte[] get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        DataItem dataItem = mLruCache.get(key);
        if (dataItem == null) {
            return null;
        }
        if (dataItem.isValid()) {
            return dataItem.mData;
        } else {
            mLruCache.remove(key);
            return null;
        }
    }

}
