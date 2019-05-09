package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Darren on 2016/10/18.
 */
public class ToastUtil {

    private static Handler mainThreadHandler;
    private static Toast mainToast;

    public static void showShort(final Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showShort(final Context context, int resourceId) {
        showToast(context, context.getString(resourceId), Toast.LENGTH_SHORT);
    }

    public static void showLong(final Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showLong(final Context context, int resourceId) {
        showToast(context, context.getString(resourceId), Toast.LENGTH_LONG);
    }

    private static void showToast(final Context context, final String message, final int duration) {
        if (mainToast != null) {
            mainToast.cancel();
        }
        if (mainThreadHandler != null) {
            mainThreadHandler.removeCallbacksAndMessages(null);
        }
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    mainToast = Toast.makeText(context, message, duration);
                    mainToast.show();
                } catch (Exception e) {
                    LogUtil.i("Toast run");
                }
            }
        });
    }

    private static Handler getMainThreadHandler() {
        if (mainThreadHandler == null) {
            synchronized (ToastUtil.class) {
                if (mainThreadHandler == null) {
                    mainThreadHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mainThreadHandler;
    }

}
