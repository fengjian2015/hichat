package com.wewin.hichat.androidlib.widget;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

/**
 * 计时器
 * Created by Darren on 2019/2/18
 */
public class CustomCountDownTimer {

    private CountDownHandler mHandler = new CountDownHandler(new WeakReference<CustomCountDownTimer>(this));
    private static final int MSG = 1;

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    protected void onTick(long millisUntilFinished){

    }

    /**
     * Callback fired when the time is up.
     */
    protected void onFinish(){

    }

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private boolean mCancelled = false;

    /**
     * @param millisInFuture The number of millis in the future from the call
     *   to {@link #start()} until the countdown is done and {@link #onFinish()}
     *   is called.
     * @param countDownInterval The interval along the way to receive
     *   {@link #onTick(long)} callbacks.
     */
    protected CustomCountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     *
     * Do not call it from inside CountDownTimer threads
     */
    public final void cancel() {
        mHandler.removeMessages(MSG);
        mCancelled = true;
    }

    /**
     * Start the countdown.
     */
    public synchronized final CustomCountDownTimer start() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mCancelled = false;
        return this;
    }

    private static class CountDownHandler extends Handler{

        WeakReference<CustomCountDownTimer> reference;

        private CountDownHandler(WeakReference<CustomCountDownTimer> reference){
            super();
            this.reference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            synchronized (CountDownHandler.this) {
                final long millisLeft = reference.get().mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    reference.get().onFinish();
                } else if (millisLeft < reference.get().mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    reference.get().onTick(millisLeft);

                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + reference.get().mCountdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) {
                        delay += reference.get().mCountdownInterval;
                    }

                    if (!reference.get().mCancelled) {
                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }

        }
    }

}
