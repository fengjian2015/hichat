package com.wewin.hichat.androidlib.permission;

import java.util.HashMap;

/**
 * 作者：created by jason on 2019/4/5 14
 * 回调方法
 */
public interface PermissionCallback {
    //确认回调
    void onGranted();
    //取消回调
    void onDenied(HashMap<String, Boolean> permissions);


}
