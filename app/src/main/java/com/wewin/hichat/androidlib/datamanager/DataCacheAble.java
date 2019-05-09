package com.wewin.hichat.androidlib.datamanager;

/**
 * Created by Darren on 2018/7/25.
 */

public interface DataCacheAble {

    String getCacheKey();

    long getSaveTime();

    byte[] toByteArr();

    void fromByteArr(byte[] bytes);

}
