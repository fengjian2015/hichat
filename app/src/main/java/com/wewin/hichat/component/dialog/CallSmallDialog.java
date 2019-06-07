package com.wewin.hichat.component.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.widget.CallSmallView;

import java.util.ArrayList;
import java.util.List;

/**
 *   @author jason
 *   date:2019/5/1813:18
 */
public class CallSmallDialog {
    private CallSmallView mCallSmallView;
    private WindowManager wm;
    private WindowManager.LayoutParams mLayoutParams;
    private Context mContext;

    private List<View> mViewList = new ArrayList<>();
    private boolean openWindow = false;

    private static CallSmallDialog instance = null;

    public static CallSmallDialog getInstance() {
        if (instance == null) {
            throw new NullPointerException(CallSmallDialog.class.getSimpleName());
        }
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new CallSmallDialog(context);
        }
    }

    private CallSmallDialog(Context context) {
        mContext = context;
    }

    /**
     * 初始化窗口
     */
    public void initCallSmallViewLayout() {
        mCallSmallView = new CallSmallView(mContext);
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.UNKNOWN);
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.gravity = Gravity.NO_GRAVITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        //使用非CENTER时，可以通过设置XY的值来改变View的位置
        mCallSmallView.setWm(wm);
        mCallSmallView.setWmParams(mLayoutParams);
    }

    public void setOpenWindow(boolean openWindow) {
        this.openWindow = openWindow;
    }


    public void alertWindow() {
        try {
            if (!openWindow) {
                return;
            }
            if (mViewList.size() > 0) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(mContext)) {
                    show();
                }
            } else {
                show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void show() {
        //这里重新创建，不然再次进入时无法控制里面的子view，这里导致，以后启动新的播放界面时，必须在弹出之前调用关闭
        initCallSmallViewLayout();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (wm != null && mCallSmallView.getWm() != null && mCallSmallView.getWindowId() == null) {
                wm.addView(mCallSmallView, mLayoutParams);
                mViewList.add(mCallSmallView);
            }
        } else {
            if (wm != null && mCallSmallView.getWm() != null) {
                wm.addView(mCallSmallView, mLayoutParams);
                mViewList.add(mCallSmallView);
            }
        }
    }


    // 移除window
    public void dismissWindow(boolean isDestroyWindow) {
        try {
            LogUtil.i("isDestroyWindow", isDestroyWindow);
            LogUtil.i("mCallSmallView", mCallSmallView);
            if (isDestroyWindow) {
                openWindow = false;
            }
            if (mCallSmallView == null) {
                return;
            }
            if (wm != null && mCallSmallView.getWindowId() != null) {
                wm.removeView(mCallSmallView);
                mViewList.remove(mCallSmallView);
            }
            mCallSmallView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
