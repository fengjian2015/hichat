package com.wewin.hichat.androidlib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author Darren
 * Created by Darren on 2017/3/13.
 */

public class SystemUtil {


    /**
     * 根据包名移除栈
     */
    public static void removeTaskByPckName(Context context, String pckName) {
        if (context == null || TextUtils.isEmpty(pckName)) {
            return;
        }
        try {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (manager == null) {
                return;
            }
            List<ActivityManager.RunningTaskInfo> taskInfoList = manager.getRunningTasks(30);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (pckName.contentEquals(taskInfo.baseActivity.getPackageName())) {
                    removeTask(context, taskInfo.id);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据类名移除栈
     */
    public static void removeTaskByClsName(Context context, String clsName) {
        if (context == null || TextUtils.isEmpty(clsName)) {
            return;
        }
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (manager == null) {
                return;
            }
            List<ActivityManager.RunningTaskInfo> taskInfoList = manager.getRunningTasks(30);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (clsName.contentEquals(taskInfo.topActivity.getClassName())) {
                    removeTask(context, taskInfo.id);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除栈
     */
    private static void removeTask(Context context, int taskId) {
        try {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (manager == null) {
                return;
            }
            Class<?> managerClass = Class.forName("android.app.ActivityManager");
            Method removeTask = managerClass.getDeclaredMethod("removeTask", int.class);
            removeTask.setAccessible(true);
            removeTask.invoke(manager, taskId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前进程名
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : manager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 通知MediaStore刷新
     */
    public static void notifyMediaStoreRefresh(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputManager != null && inputManager.isActive()) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputManager != null && inputManager.isActive()) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     *
     * @param view 要获取输入内容的view
     */
    public static void showKeyboard(Activity activity, View view) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 用*号隐藏手机号码
     */
    public static String hidePhoneNum(String phoneNum) {
        String centerFourNum = phoneNum.substring(3, 7);
        return phoneNum.replace(centerFourNum, "****");
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 得到的屏幕的宽度
     */
    public static int getWidthPx(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        // 初始化一个结构
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        // 对该结构赋值
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics.widthPixels;
    }

    /**
     * 得到的屏幕的高度
     */
    public static int getHeightPx(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        // 初始化一个结构
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        // 对该结构赋值
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics.heightPixels;
    }

    /**
     * 得到屏幕的dpi
     */
    public static int getDensityDpi(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        // 初始化一个结构
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        // 对该结构赋值
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics.densityDpi;
    }

    /**
     * 返回状态栏/通知栏的高度
     */
    public static int getStatusHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }


}
