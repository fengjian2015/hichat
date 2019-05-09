package com.wewin.hichat.androidlib.datamanager;

import java.io.Serializable;

/**
 * Created by Darren on 2018/8/6.
 */

public interface DataCache {

    /**
     * 基础类型数据保存
     * @param key
     * @param obj
     * @param duration 保存时长 单位：秒，-1表示永久
     */
    void setData(String key, Object obj, long duration);

    void setData(String key, Object obj);

    /**
     * 对象保存
     * @param key
     * @param obj 要缓存的对象（必须实现Serializable接口）
     * @param duration 保存时长 单位：秒，-1表示永久
     */
    void setObject(String key, Serializable obj, long duration);

    void setObject(String key, Serializable obj);

    Object getObject(String key);

    String getString(String key);

    Boolean getBoolean(String key);

    Integer getInteger(String key);

    Double getDouble(String key);

    Float getFloat(String key);

    Long getLong(String key);

    Short getShort(String key);

    void remove(String key);

    void clear(boolean deleteValid);


}
