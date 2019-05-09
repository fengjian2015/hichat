package com.wewin.hichat.androidlib.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import java.util.ArrayList;


public class ServiceUtil {


    /**
     * 判断服务是否开启
     * @param ServiceName 服务完整名
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null){
            return false;
        }
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) activityManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

}
