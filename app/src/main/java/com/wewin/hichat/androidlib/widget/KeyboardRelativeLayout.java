package com.wewin.hichat.androidlib.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by Darren on 2019/1/28
 */
public class KeyboardRelativeLayout extends RelativeLayout {

    private OnKeyboardLayoutListener mListener;
    //输入法是否激活
    private boolean mIsKeyboardActive = false;
    //输入法高度
    private int mKeyboardHeight = 0;

    public KeyboardRelativeLayout(Context context) {
        this(context, null, 0);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 监听布局变化
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
    }

    public interface OnKeyboardLayoutListener {
        void keyboardStateChanged(boolean isKeyboardActive, int keyboardHeight);
    }

    public void setOnKeyboardLayoutListener(OnKeyboardLayoutListener listener) {
        mListener = listener;
    }

    public OnKeyboardLayoutListener getKeyboardListener() {
        return mListener;
    }

    public boolean isKeyboardActive() {
        return mIsKeyboardActive;
    }

    /**
     * 获取输入法高度
     * @return
     */
    public int getKeyboardHeight() {
        return mKeyboardHeight;
    }

    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        int mScreenHeight = 0;

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            mScreenHeight = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            // 获取当前页面窗口的显示范围
            ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - rect.bottom; // 输入法的高度
            boolean isActive = false;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                isActive = true; // 超过屏幕五分之一则表示弹出了输入法
                mKeyboardHeight = keyboardHeight;
            }
            mIsKeyboardActive = isActive;
            if (mListener != null) {
                mListener.keyboardStateChanged(isActive, keyboardHeight);
            }
        }
    }


}
