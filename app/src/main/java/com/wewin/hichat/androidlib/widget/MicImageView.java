package com.wewin.hichat.androidlib.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 录音
 * Created by Darren on 2019/2/7
 */
public class MicImageView extends AppCompatImageView {

    private boolean isRecording = false;
    private OnTouchListener touchListener;

    public MicImageView(Context context) {
        super(context);
    }

    public MicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnTouchListener{
        void actionDown();

        void actionUp(float upX);
    }

    public void setOnTouchListener(OnTouchListener touchListener){
        this.touchListener = touchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!isRecording){
                    isRecording = true;
                    if (touchListener != null){
                        touchListener.actionDown();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isRecording){
                    isRecording = false;
                    if (touchListener != null){
                        touchListener.actionUp(event.getX());
                    }
                }
                break;

            default:
                break;
        }

        return true;
    }
}
