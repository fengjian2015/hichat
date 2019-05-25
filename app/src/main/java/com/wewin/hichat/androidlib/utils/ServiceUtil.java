package com.wewin.hichat.androidlib.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import java.util.ArrayList;

/**
 * @author Darren
 */
public class ServiceUtil {

    /**
     * 判断服务是否开启
     * @param serviceName 服务完整名
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (("").equals(serviceName) || serviceName == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null){
            return false;
        }
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) activityManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

}
