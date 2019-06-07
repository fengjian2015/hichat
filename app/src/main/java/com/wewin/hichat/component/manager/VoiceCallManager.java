package com.wewin.hichat.component.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.wewin.hichat.MainActivity;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.manage.RingVibrateManager;
import com.wewin.hichat.androidlib.permission.AuthorizationCheck;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.MyLifecycleHandler;
import com.wewin.hichat.androidlib.utils.SpeakerUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.widget.CustomCountDownTimer;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.LibCons;
import com.wewin.hichat.component.dialog.CallSmallDialog;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.VoiceCall;
import com.wewin.hichat.model.socket.ChatSocket;
import com.wewin.hichat.view.conversation.ChatVoiceCallActivity;

import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 语音通话
 * @author Darren
 * Created by Darren on 2019/2/12
 */
public class VoiceCallManager {

    private static VoiceCallManager instance;
    private RtcEngine mRtcEngine;
    private OnCallStateChangeListener stateChangeListener;
    private OnDurationChangeListener durationChangeListener;
    private boolean isJoined = false;//是否已进入聊天频道
    private boolean isRunning = false;//语音通话sdk是否初始化
    private int duration = 0;//已通话时间
    private ChatRoom callChatRoom;//保存通话界面chatRoom实例
    private VoiceCall voiceCall;//保存通话界面的voiceCall实例
    private boolean muteState = false;//是否静音
    public static final int TYPE_CALL_INVITING = 0;
    public static final int TYPE_CALL_WAIT_ANSWER = 1;
    public static final int TYPE_CALL_CONNECTING = 2;
    public static final int TYPE_CALL_CALLING = 3;
    public static final int TYPE_CALL_FINISH = 4;
    private int callType = TYPE_CALL_FINISH;//当前通话状态
    private final int MAX_WAIT_TIME = 60 * 1000;//60S超时挂断
    private Intent intent;//用于后台情况接受到通话后进行跳转
    public boolean backstage=false;

    private CustomCountDownTimer waitTimer = new CustomCountDownTimer(MAX_WAIT_TIME, 1000) {
        @Override
        public void onFinish() {
            if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_INVITING) {
                if (VoiceCallManager.get().getVoiceCall() != null) {
                    VoiceCall voiceCall = VoiceCallManager.get().getVoiceCall();
                    voiceCall.setConnectState(VoiceCall.TIME_OUT);
                    ChatSocket.getInstance().send(ChatRoomManager
                            .packVoiceCallMsg(VoiceCallManager.get().getCallChatRoom(), voiceCall));
                }
                stop(null);

            } else if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_WAIT_ANSWER) {
                stop(null);
            }
            EventTrans.post(EventMsg.CONVERSATION_VOICE_CALL_FINISH);
        }
    };


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (callType == TYPE_CALL_CALLING) {
                if (durationChangeListener != null) {
                    durationChangeListener.durationChange(duration);
                }
                duration++;
                handler.postDelayed(this, 1000);
            }
        }
    };

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
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

    private VoiceCallManager() {

    }

    public static VoiceCallManager get() {
        if (instance == null) {
            synchronized (VoiceCallManager.class) {
                if (instance == null) {
                    instance = new VoiceCallManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        isRunning = true;

        try {
            if (mRtcEngine == null) {
                mRtcEngine = RtcEngine.create(context, LibCons.AGORA_APP_ID, mRtcEventHandler);
                mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

            }
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n"
                    + Log.getStackTraceString(e));
        }
    }

    /**
     * 进入频道
     */
    public void joinChannel(Context context, String channel) {
        if (mRtcEngine != null && !isJoined) {
            // if you do not specify the uid, we will generate the uid for you
            int joinResult = mRtcEngine.joinChannel(null, channel, "", 0);
            LogUtil.i("joinResult", joinResult);
            LogUtil.i("joinChannel", channel);

            if (joinResult == 0) {
                setHandFree(context, false);
                mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
                mRtcEngine.adjustRecordingSignalVolume(150);//调节录音音量
                mRtcEngine.adjustPlaybackSignalVolume(150);//调节播放音量
                isJoined = true;
                if (stateChangeListener != null) {
                    stateChangeListener.joinChannelSuccess(channel);
                }
            } else if (joinResult < 0 && stateChangeListener != null) {
                stateChangeListener.joinChannelFailure(joinResult);
            }
        }
    }

    //开始通话计时
    public void startCallTimeCount() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.post(runnable);
        }
    }

    //开始等待接听倒计时
    public void startWaitTimeCount(){
        if (waitTimer != null){
            waitTimer.start();
        }
    }

    //取消等待接听倒计时
    public void cancelWaitTimeCount(){
        if (waitTimer != null){
            waitTimer.cancel();
        }
    }

    //静音
    public void setMute(boolean muteState) {
        if (mRtcEngine != null) {
            mRtcEngine.muteLocalAudioStream(muteState);
            this.muteState = muteState;
        }
    }

    //免提
    public void setHandFree(Context context, boolean isSpeakerOn) {
        if (isSpeakerOn) {
            SpeakerUtil.setSpeakerphoneOn(context);
            if (mRtcEngine != null){
                mRtcEngine.setEnableSpeakerphone(true);
            }
        } else {
            SpeakerUtil.setCallPhoneOn(context);
            if (mRtcEngine != null){
                mRtcEngine.setEnableSpeakerphone(false);
            }
        }
    }

    public boolean isHandFree(Context context) {
        return SpeakerUtil.isSpeakerphoneOn(context);
    }

    public boolean isMute() {
        return muteState;
    }

    public void stop(Context context) {
        backstage=false;
        RingVibrateManager.getInstance().stop();
        LogUtil.i("stop");
        if (!isRunning){
            return;
        }
        isRunning = false;
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
        }
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        if (waitTimer != null){
            waitTimer.cancel();
        }
        CallSmallDialog.getInstance().dismissWindow(true);
        RtcEngine.destroy();
        isJoined = false;
        duration = 0;
        callChatRoom = null;
        voiceCall = null;
        muteState = false;
        callType = TYPE_CALL_FINISH;
        SpeakerUtil.setSpeakerphoneOn(context);
    }

    //启动语音通话界面(等待接听)
    void startVoiceCallWaitActivity(Context context, ChatRoom chatRoom) {
        if (AuthorizationCheck.authorizationCheck(Permission.RECORD_AUDIO,context)){
            this.callType = TYPE_CALL_WAIT_ANSWER;
            startVoiceCallActivity(context, chatRoom);
        }else {
            VoiceCallManager.get().voiceCall.setConnectState(VoiceCall.REFUSE);
            ChatSocket.getInstance().send(ChatRoomManager.packVoiceCallMsg(chatRoom, VoiceCallManager.get().voiceCall));
            permissionDialog(MyLifecycleHandler.getActivity());
        }

    }

    //启动语音通话界面
    public void startVoiceCallActivity(Context context, ChatRoom chatRoom) {
        if (!AuthorizationCheck.authorizationCheck(Permission.RECORD_AUDIO,context)){
            permissionDialog(MyLifecycleHandler.getActivity());
            return;
        }
        if (callChatRoom!=null&&chatRoom!=null&&!callChatRoom.getRoomId().equals(chatRoom.getRoomId())){
            ToastUtil.showShort(context,context.getString(R.string.side_busy));
            return;
        }
        if (chatRoom != null){
            VoiceCallManager.get().setCallChatRoom(chatRoom);
        }
        if(!MyLifecycleHandler.isApplicationVisible()){
            backstage=true;
            SpeakerUtil.setSpeakerphoneOn(context);
            RingVibrateManager.getInstance().playCallRing(context);
        }else {
            backstage=false;
        }
        Intent intent = new Intent(context, ChatVoiceCallActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 应用在后台的情况下记录，然后打开应用的时候调用
     * @param context
     */
    public void startBackstageCallActivity(Context context){
        if (backstage){
            startVoiceCallActivity(context,null);
        }
    }

    private void permissionDialog(final Activity activity){
        new PromptDialog.PromptBuilder(activity)
                .setPromptContent(activity.getString(R.string.call_prompt))
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        AuthorizationCheck.openApplication(activity);
                    }
                })
                .setCancelVisible(false)
                .create()
                .show();
    }

    public void setOnDurationChangeListener(OnDurationChangeListener durationChangeListener) {
        this.durationChangeListener = durationChangeListener;
    }

    public void setOnCallStateChangeListener(OnCallStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public VoiceCall getVoiceCall() {
        return voiceCall;
    }

    public void setVoiceCall(VoiceCall voiceCall) {
        this.voiceCall = voiceCall;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getCallType() {
        return callType;
    }

    public ChatRoom getCallChatRoom() {
        return callChatRoom;
    }

    public void setCallChatRoom(ChatRoom callChatRoom) {
        this.callChatRoom = callChatRoom;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public interface OnDurationChangeListener {
        void durationChange(int duration);
    }

    public interface OnCallStateChangeListener {
        void joinChannelSuccess(String channel);

        void joinChannelFailure(int joinResult);

        void otherSideOffline();
    }

}
