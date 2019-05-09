package com.wewin.hichat.component.manager;

import android.content.Context;
import android.util.Log;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.utils.LogUtil;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

/**
 * 语音通话
 * Created by Darren on 2019/2/12
 */
public class VoiceCallManager {

    private static VoiceCallManager instance;
    private RtcEngine mRtcEngine;
    private boolean isJoined = false;
    private boolean isInit = false;
    private OnCallStateChangeListener stateChangeListener;
    private int callType;//当前通话状态
    private int openType;//通话界面打开状态
    private int calledTime;//已通话时间
    private long callingDismissTime;//通话界面关闭时间


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        @Override
        public void onUserOffline(final int uid, final int reason) {
            if (stateChangeListener != null) {
                stateChangeListener.otherSideOffline();
            }
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {

        }
    };

    private VoiceCallManager(){}

    public static VoiceCallManager getInstance() {
        if (instance == null) {
            synchronized (VoiceCallManager.class) {
                if (instance == null) {
                    instance = new VoiceCallManager();
                }
            }
        }
        return instance;
    }

    public interface OnCallStateChangeListener {
        void joinChannelSuccess(String channel);

        void joinChannelFailure(int joinResult);

        void otherSideOffline();
    }

    public void setOnCallStateChangeListener(OnCallStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    //初始化
    public void init(Context context) {
        initAgoraEngine(context);
    }

    //进入频道
    public void joinChannel(String channel) {
        if (mRtcEngine != null && !isJoined) {
            int joinResult = mRtcEngine.joinChannel(null, channel, "", 0); // if you do not specify the uid, we will generate the uid for you
            LogUtil.i("joinResult", joinResult);
            LogUtil.i("joinChannel", channel);

            if (joinResult == 0 && stateChangeListener != null) {
                stateChangeListener.joinChannelSuccess(channel);
                isJoined = true;

            } else if (joinResult < 0 && stateChangeListener != null) {
                stateChangeListener.joinChannelFailure(joinResult);
            }
        }
    }

    //静音
    public void setMute(boolean isMute) {
        if (mRtcEngine != null) {
            mRtcEngine.muteLocalAudioStream(isMute);
        }
    }

    //免提
    public void setHandFree(boolean isHandFree) {
        if (mRtcEngine != null) {
            mRtcEngine.setEnableSpeakerphone(isHandFree);
        }
    }

    public void setCallType(int callType){
        this.callType = callType;
    }

    public int getCallType(){
        return callType;
    }

    public void setOpenType(int openType){
        this.openType = openType;
    }

    public int getOpenType(){
        return openType;
    }

    public void setCalledTime(int calledTime){
        this.calledTime = calledTime;
    }

    public int getCalledTime(){
        return calledTime;
    }

    public void setCallingDismissTime(long dismissTimestamp){
        this.callingDismissTime = dismissTimestamp;
    }

    public long getCallingDismissTime() {
        return callingDismissTime;
    }

    public boolean isRunning() {
        return isInit;
    }

    public void stop() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
        }
        RtcEngine.destroy();
        callType = -1;
        calledTime = -1;
        callingDismissTime = -1;
        isJoined = false;
        isInit = false;
    }

    private void initAgoraEngine(Context context) {
        try {
            if (mRtcEngine == null){
                mRtcEngine = RtcEngine.create(context, context.getString(R.string.agora_app_id),
                        mRtcEventHandler);
                mRtcEngine.setEnableSpeakerphone(false);
                isInit = true;
            }

        } catch (Exception e) {
            Log.e("VoiceCallManager", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n"
                    + Log.getStackTraceString(e));
        }
    }

}
