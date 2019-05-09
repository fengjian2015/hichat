package com.wewin.hichat.androidlib.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by Darren on 2017/9/6.
 */

public class PullNestedScrollView extends NestedScrollView implements Pullable {

    private boolean isScrolledToTop = false;
    private boolean isScrolledToBottom = false;
    private Scroller mScroller;
    private boolean isBotDataAdd = false;
    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private float downY;
    private float downX;

    public PullNestedScrollView(Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    public PullNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public PullNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    public void setIsBotDataAdd(boolean type) {
        isBotDataAdd = type;
    }

    public void setRefreshing(boolean type) {
        this.isRefreshing = type;
    }

    public void setLoading(boolean type) {
        this.isLoading = type;
    }

    @Override
    public boolean canPullDown() {
        return isScrolledToTop;
    }

    @Override
    public boolean canPullUp() {
        return isScrolledToBottom;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!isBotDataAdd) {
            if (getScrollY() + getHeight() >= ((LinearLayout) getChildAt(0)).getChildAt(0).getHeight()) {
                isScrolledToTop = false;
                isScrolledToBottom = true;
                if (!isRefreshing) {
                    setScrollY(((LinearLayout) getChildAt(0)).getChildAt(0).getHeight() - getHeight());
                }
            } else {
                isScrolledToTop = false;
                isScrolledToBottom = false;
            }
        } else {
            if (getScrollY() <= ((LinearLayout) getChildAt(0)).getChildAt(0).getHeight()) {
                isScrolledToTop = true;
                isScrolledToBottom = false;
                if (!isLoading) {
                    setScrollY(((LinearLayout) getChildAt(0)).getChildAt(0).getBottom());
                }

            } else {
                isScrolledToTop = false;
                isScrolledToBottom = false;
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
//                isRefreshing = false;
//                isLoading = false;
                break;

        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                isRefreshing = false;
                isLoading = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float xLength = Math.abs(ev.getX() - downX);
                float yLength = Math.abs(ev.getY() - downY);
                if (xLength > yLength){
                    return false;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    //调用此方法滚动到目标位置  duration滚动时间
    public void smoothScrollToSlow(int fx, int fy, int duration) {
        int dx = fx - getScrollX();//mScroller.getFinalX();  普通view使用这种方法
        int dy = fy - getScrollY();  //mScroller.getFinalY();
        smoothScrollBySlow(dx, dy, duration);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBySlow(int dx, int dy, int duration) {

        //设置mScroller的滚动偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);//scrollView使用的方法（因为可以触摸拖动）
//        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, duration);  //普通view使用的方法
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 滑动事件，这是控制手指滑动的惯性速度
     */
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
    }

}
