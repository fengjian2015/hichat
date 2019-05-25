package com.wewin.hichat.androidlib.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.manager.VoiceCallManager;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.view.TestActivity;

/**
 * @author Jason
 * date:2019/5/1813:40
 */
public class CallSmallView extends LinearLayout {
    private int screenHeight;
    private int screenWidth;
    private int statusHeight;

    //按下位置
    private float mTouchStartX;
    private float mTouchStartY;
    //移动位置
    private float mMoveStartRawX;
    private float mMoveStartRawY;
    //移动位置
    private float mMoveX;
    private float mMoveY;
    boolean isRight = true;
    // 小窗口位置坐标
    private int[] location = new int[2];
    //按下时间
    private long currentMS;
    //记录是否移动
    private int moveX;
    private int moveY;

    private WindowManager wm;
    public WindowManager.LayoutParams wmParams;

    private Context mContext;

    private ImageView imageView;

    public CallSmallView(Context context) {
        super(context);
        mContext = context;
        initData();
    }

    public CallSmallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initData();
    }

    public CallSmallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
    }

    private void initData() {
        statusHeight = getStatusHeight(mContext);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        initView();
    }

    private void initView() {
        View relativeLayout = View.inflate(mContext, R.layout.dialog_call_small, null);
        imageView = relativeLayout.findViewById(R.id.iv_avatar);
        ChatRoom chatRoom = VoiceCallManager.get().getCallChatRoom();
        if (chatRoom == null){
            return;
        }
        FriendInfo contactInfo = ContactUserDao.getContactUser(chatRoom.getRoomId());
        ImgUtil.loadCircle(mContext, contactInfo.getAvatar(), imageView);
        addView(relativeLayout);
    }

    public void setWm(WindowManager wm) {
        this.wm = wm;
    }

    public WindowManager getWm() {
        return wm;
    }

    public void setWmParams(WindowManager.LayoutParams wmParamss) {
        wmParams = wmParamss;
        //wmParams.x表示的是布局中心的，屏幕原点在中心处
        wmParams.x = SpCons.getVMParamsX(mContext);
        if (wmParams.x == 0) {
            wmParams.x = (screenWidth / 2) - wmParamss.width + (wmParamss.width / 2);
        }
        wmParams.y = SpCons.getVMParamsY(mContext);

    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getLocationOnScreen(location);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (wmParams.x > 0) {
                    isRight = true;
                }
                if (wmParams.x < 0) {
                    isRight = false;
                }
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();

                oldY = wmParams.y;
                oldX = wmParams.x;
                mMoveStartRawX = screenWidth / 2 + oldX;
                mMoveStartRawY = screenHeight / 2 + oldY;
                moveX = 0;
                moveY = 0;
                //long currentMS     获取系统时间
                currentMS = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getRawX();
                mMoveY = event.getRawY();
                //X轴距离
                moveX = (int) Math.abs(mMoveX - mMoveStartRawX);
                //y轴距离
                moveY = (int) Math.abs(mMoveY - mMoveStartRawY);
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                //移动时间
                long moveTime = System.currentTimeMillis() - currentMS;
                //判断是否继续传递信号
                if (moveTime <= 200 && moveX < getWidth() && moveY < getHeight()) {
                    VoiceCallManager.get().startVoiceCallActivity(mContext, null);
                }
                if (wmParams.x <= 0) {
                    wmParams.x = -(screenWidth / 2) + (wmParams.width / 2);
                } else {
                    wmParams.x = (screenWidth / 2) - wmParams.width + (wmParams.width / 2);
                }
                SpCons.setVMParamsX(mContext, wmParams.x);
                SpCons.setVMParamsY(mContext, wmParams.y);
                wm.updateViewLayout(this, wmParams);
                break;

            default:

                break;
        }
        return super.onTouchEvent(event);
    }

    private float oldY;
    private float oldX;

    private void updateViewPosition() {
        wmParams.gravity = Gravity.NO_GRAVITY;
        float move_x = (oldX + (mMoveX - mMoveStartRawX));
        float move_y = (oldY + (mMoveY - mMoveStartRawY));
        //获取可移动范围
        int max_x = (screenWidth / 2) - wmParams.width + (wmParams.width / 2);
        int min_x = -(screenWidth / 2) + (wmParams.width / 2);
        int max_y = (screenHeight / 2) - wmParams.height + (wmParams.height / 2);
        int min_y = -(screenHeight / 2) + (wmParams.height / 2);

        if (move_x > max_x) {
            wmParams.x = max_x;
        } else if (move_x < min_x) {
            wmParams.x = min_x;
        } else {
            wmParams.x = (int) move_x;
        }

        if (move_y > max_y) {
            wmParams.y = max_y;
        } else if (move_y < min_y) {
            wmParams.y = min_y;
        } else {
            wmParams.y = (int) move_y;
        }
        wm.updateViewLayout(this, wmParams);
    }


}
