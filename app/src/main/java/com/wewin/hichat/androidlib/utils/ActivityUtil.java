package com.wewin.hichat.androidlib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * 判断activity是否存在
 */
public class ActivityUtil {

    public static boolean isActivityOnTop(Activity act) {
        if (act != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                return !act.isDestroyed() && !act.isFinishing();
            } else {
                return !act.isFinishing();
            }
        } else {
            return false;
        }
    }

    public static boolean isActivityOnTop(Context context) {
        if (context != null) {
            if (context instanceof Activity) {
                if (Build.VERSION.SDK_INT >= 17) {
                    return !((Activity) context).isDestroyed() && !((Activity) context).isFinishing();
                } else {
                    return !((Activity) context).isFinishing();
                }
            } else
                return !(context instanceof Service) || isServiceRunning(context, context.getClass().getName());
        } else {
            return false;
        }
    }


    /**
     * 当前app是否还在运行
     *
     * @param context
     * @return
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null){
            return false;
        }
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName)
                    || info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 判断视图是否存在
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null){
            return false;
        }
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(150);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


}
