package com.wewin.hichat.androidlib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;

import java.util.List;

/**
 * Created by Darren on 2018/12/18.
 */
public class ClassUtil {

    //获取类名+方法名
    public static String classMethodName() {
        return getCurrentClassName() + "--" + getCurrentMethodName() + "--:";
    }

    //获取方法名
    private static String getCurrentMethodName() {
        int level = 2;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        return stacks[level].getMethodName();
    }

    //获取类名
    private static String getCurrentClassName() {
        int level = 2;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

    //判断是否在主线程
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取栈顶Activity
     */
    public static String getStackTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = manager.getRunningTasks(1);
            if (runningTaskInfoList != null) {
                return (runningTaskInfoList.get(0).topActivity).toString();
            }
        }
        return null;
    }

}
