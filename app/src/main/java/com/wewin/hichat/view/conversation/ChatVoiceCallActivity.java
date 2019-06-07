package com.wewin.hichat.view.conversation;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.manage.RingVibrateManager;
import com.wewin.hichat.androidlib.permission.AuthorizationCheck;
import com.wewin.hichat.androidlib.utils.SpeakerUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.dialog.CallSmallDialog;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.component.manager.VoiceCallManager;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.UUIDUtil;
import com.wewin.hichat.androidlib.widget.CustomCountDownTimer;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.VoiceCall;
import com.wewin.hichat.model.socket.ChatSocket;

import java.util.HashMap;


/**
 * 聊天页-通话
 * Created by Darren on 2019/2/13
 */
public class ChatVoiceCallActivity extends BaseActivity {

    private ImageView muteIv, handFreeIv, avatarIv, msgIv, hangUpCenterIv,
            hangUpLeftIv, answerIv;
    private TextView nameTv, callStateTv;
    private LinearLayout muteLl, msgLl, handFreeLl, backLl;


    @Override
    protected void getIntentData() {
        super.getIntentData();
        ChatRoom chatRoom = (ChatRoom) getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM);
        if (chatRoom != null) {
            VoiceCallManager.get().setCallChatRoom(chatRoom);
            VoiceCallManager.get().backstage=false;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_chat_call;
    }

    @Override
    protected void initViews() {
        backLl = findViewById(R.id.ll_chat_voice_call_back);
        avatarIv = findViewById(R.id.civ_message_chat_call_avatar);
        muteIv = findViewById(R.id.iv_message_chat_call_mute);
        msgIv = findViewById(R.id.iv_message_chat_call_msg);
        handFreeIv = findViewById(R.id.iv_message_chat_call_hand_free);
        hangUpCenterIv = findViewById(R.id.iv_message_chat_call_hang_up_center);
        nameTv = findViewById(R.id.tv_message_chat_call_name);
        callStateTv = findViewById(R.id.tv_message_chat_call_state);
        answerIv = findViewById(R.id.iv_message_chat_call_answer);
        hangUpLeftIv = findViewById(R.id.iv_message_chat_call_hang_up_left);
        muteLl = findViewById(R.id.ll_message_chat_call_mute_container);
        msgLl = findViewById(R.id.ll_message_chat_call_msg_container);
        handFreeLl = findViewById(R.id.ll_message_chat_call_hand_free_container);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(0);
        if (VoiceCallManager.get().getCallChatRoom() == null) {
            return;
        }
        FriendInfo contactUser = ContactUserDao.getContactUser(VoiceCallManager.get().getCallChatRoom().getRoomId());
        if (contactUser != null) {
            ImgUtil.load(this, contactUser.getAvatar(), avatarIv);
            if (!TextUtils.isEmpty(contactUser.getFriendNote())) {
                nameTv.setText(contactUser.getFriendNote());
            } else {
                nameTv.setText(contactUser.getUsername());
            }
        }

        if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_FINISH) {
            VoiceCallManager.get().setCallType(VoiceCallManager.TYPE_CALL_INVITING);
        }

        Rigger.on(this).permissions(Permission.WRITE_EXTERNAL_STORAGE).start(new PermissionCallback() {
            @Override
            public void onGranted() {
                if (!VoiceCallManager.get().isRunning()) {
                    VoiceCallManager.get().init(getAppContext());
                    if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_INVITING) {
                        String channel = UUIDUtil.get24UUID();
                        ChatMsg voiceCallMsg = ChatRoomManager
                                .packVoiceCallInviteMsg(VoiceCallManager.get().getCallChatRoom(), channel);
                        ChatSocket.getInstance().send(voiceCallMsg);
                        VoiceCallManager.get().setVoiceCall(voiceCallMsg.getVoiceCall());
                        VoiceCallManager.get().startWaitTimeCount();
                    }
                }
                setCallTypeView();
            }

            @Override
            public void onDenied(HashMap<String, Boolean> permissions) {

            }
        });

        //邀请方铃声为听筒播放；接收方铃声为扬声器播放
        if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_INVITING) {
            SpeakerUtil.setCallPhoneOn(getAppContext());
            RingVibrateManager.getInstance().playCallPhone(getAppContext());

        } else if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_WAIT_ANSWER) {
            SpeakerUtil.setSpeakerphoneOn(getAppContext());
            RingVibrateManager.getInstance().playCallRing(getAppContext());

        } else if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_CALLING
                && VoiceCallManager.get().getDuration() > 0) {
            callStateTv.setText(TimeUtil.formatTimeStr(VoiceCallManager.get().getDuration()));
        }
    }

    @Override
    protected void setListener() {
        backLl.setOnClickListener(this);
        muteIv.setOnClickListener(this);
        msgIv.setOnClickListener(this);
        handFreeIv.setOnClickListener(this);
        hangUpCenterIv.setOnClickListener(this);
        answerIv.setOnClickListener(this);
        hangUpLeftIv.setOnClickListener(this);

        VoiceCallManager.get().setOnDurationChangeListener(new VoiceCallManager.OnDurationChangeListener() {
            @Override
            public void durationChange(int duration) {
                callStateTv.setText(TimeUtil.formatTimeStr(duration));
            }
        });

        VoiceCallManager.get().setOnCallStateChangeListener(new VoiceCallManager.OnCallStateChangeListener() {
            @Override
            public void joinChannelSuccess(String channel) {
                LogUtil.i("joinChannelSuccess channel", channel);
            }

            @Override
            public void joinChannelFailure(int joinResult) {
                LogUtil.i("joinChannelSuccess joinResult", joinResult);
                ToastUtil.showShort(getAppContext(), R.string.voice_call_join_failure);
                VoiceCallManager.get().stop(getAppContext());
                finishActivity();
            }

            @Override
            public void otherSideOffline() {
                LogUtil.i("otherSideOffline");
                VoiceCallManager.get().stop(getAppContext());
                finishActivity();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_chat_voice_call_back:
                moveToBack();
                break;

            case R.id.iv_message_chat_call_mute:
                VoiceCallManager.get().setMute(!VoiceCallManager.get().isMute());
                if (!VoiceCallManager.get().isMute()) {
                    muteIv.setImageResource(R.drawable.mute_btn_off);
                } else {
                    muteIv.setImageResource(R.drawable.mute_btn_open);
                }
                break;

            case R.id.iv_message_chat_call_msg:
                moveToBack();
                break;

            case R.id.iv_message_chat_call_hand_free:
                VoiceCallManager.get().setHandFree(getAppContext(),
                        !VoiceCallManager.get().isHandFree(getAppContext()));
                if (VoiceCallManager.get().isHandFree(getAppContext())) {
                    handFreeIv.setImageResource(R.drawable.handsfree_btn_open);
                } else {
                    handFreeIv.setImageResource(R.drawable.handsfree_btn_off);
                }
                break;

            case R.id.iv_message_chat_call_hang_up_center:
            case R.id.iv_message_chat_call_hang_up_left:
                RingVibrateManager.getInstance().stop();
                if (VoiceCallManager.get().getVoiceCall() != null) {
                    VoiceCall voiceCall = VoiceCallManager.get().getVoiceCall();
                    if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_INVITING) {
                        voiceCall.setConnectState(VoiceCall.CANCEL);

                    } else if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_WAIT_ANSWER) {
                        voiceCall.setConnectState(VoiceCall.REFUSE);

                    } else if (VoiceCallManager.get().getCallType() == VoiceCallManager.TYPE_CALL_CALLING) {
                        voiceCall.setConnectState(VoiceCall.FINISH);
                        voiceCall.setDuration(VoiceCallManager.get().getDuration());
                    }
                    ChatSocket.getInstance().send(ChatRoomManager
                            .packVoiceCallMsg(VoiceCallManager.get().getCallChatRoom(), voiceCall));
                }
                finishActivity();
                break;

            case R.id.iv_message_chat_call_answer:
                RingVibrateManager.getInstance().stop();
                if (VoiceCallManager.get().getVoiceCall() == null) {
                    return;
                }
                VoiceCallManager.get().startCallTimeCount();
                VoiceCall voiceCall1 = VoiceCallManager.get().getVoiceCall();
                VoiceCallManager.get().joinChannel(getAppContext(), voiceCall1.getChannel());
                VoiceCallManager.get().setCallType(VoiceCallManager.TYPE_CALL_CALLING);
                setCallTypeView();
                voiceCall1.setConnectState(VoiceCall.CONNECT);
                ChatSocket.getInstance().send(ChatRoomManager
                        .packVoiceCallMsg(VoiceCallManager.get().getCallChatRoom(), voiceCall1));
                VoiceCallManager.get().cancelWaitTimeCount();
                break;

            default:
                break;
        }
    }

    private void setCallTypeView() {
        switch (VoiceCallManager.get().getCallType()) {
            case VoiceCallManager.TYPE_CALL_INVITING:
                callStateTv.setText(getString(R.string.waiting_for_accept));
                hangUpLeftIv.setVisibility(View.INVISIBLE);
                hangUpCenterIv.setVisibility(View.VISIBLE);
                answerIv.setVisibility(View.INVISIBLE);
                muteLl.setVisibility(View.INVISIBLE);
                msgLl.setVisibility(View.INVISIBLE);
                handFreeLl.setVisibility(View.INVISIBLE);
                break;

            case VoiceCallManager.TYPE_CALL_WAIT_ANSWER:
                callStateTv.setText(getString(R.string.invite_voice_call));
                hangUpLeftIv.setVisibility(View.VISIBLE);
                hangUpCenterIv.setVisibility(View.INVISIBLE);
                answerIv.setVisibility(View.VISIBLE);
                muteLl.setVisibility(View.INVISIBLE);
                msgLl.setVisibility(View.INVISIBLE);
                handFreeLl.setVisibility(View.INVISIBLE);
                break;

            case VoiceCallManager.TYPE_CALL_CONNECTING:
                callStateTv.setText(getString(R.string.connecting));
                hangUpLeftIv.setVisibility(View.INVISIBLE);
                hangUpCenterIv.setVisibility(View.VISIBLE);
                answerIv.setVisibility(View.INVISIBLE);
                muteLl.setVisibility(View.INVISIBLE);
                msgLl.setVisibility(View.INVISIBLE);
                handFreeLl.setVisibility(View.INVISIBLE);
                break;

            case VoiceCallManager.TYPE_CALL_CALLING:
                hangUpLeftIv.setVisibility(View.INVISIBLE);
                hangUpCenterIv.setVisibility(View.VISIBLE);
                answerIv.setVisibility(View.INVISIBLE);
                muteLl.setVisibility(View.VISIBLE);
                msgLl.setVisibility(View.VISIBLE);
                handFreeLl.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        CallSmallDialog.getInstance().dismissWindow(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (VoiceCallManager.get().isRunning()) {
            CallSmallDialog.getInstance().setOpenWindow(true);
            CallSmallDialog.getInstance().alertWindow();
        }
        super.onPause();
    }

    /**
     * 模拟将该activity切到后台
     */
    private void moveToBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            new PromptDialog.PromptBuilder(this)
                    .setPromptContent(getString(R.string.call_small_prompt))
                    .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                        @Override
                        public void confirmClick() {
                            AuthorizationCheck.openApplication(ChatVoiceCallActivity.this);
                        }
                    })
                    .setOnCancelClickListener(new PromptDialog.PromptBuilder.OnCancelClickListener() {
                        @Override
                        public void cancelClick() {
                            CallSmallDialog.getInstance().setOpenWindow(true);
                            finish();
                        }
                    })
                    .setCancelVisible(true)
                    .create()
                    .show();
        } else {
            CallSmallDialog.getInstance().setOpenWindow(true);
            finish();
        }
    }

    /**
     * 关闭activity，挂断
     */
    private void finishActivity() {
        finish();
        VoiceCallManager.get().stop(getAppContext());
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.MESSAGE_VOICE_CALL_REFRESH) {
            int voiceType = (int) msg.getData();
            if (voiceType == VoiceCall.CONNECT) {
                setCallTypeView();
            } else {
                finishActivity();
            }
        } else if (msg.getKey() == EventMsg.CONVERSATION_VOICE_CALL_FINISH) {
            if (msg.getData() != null) {
                String connectStr = msg.getData().toString();
                if (!TextUtils.isEmpty(connectStr)) {
                    int connectState = Integer.parseInt(connectStr);
                    if (connectState == VoiceCall.BUSY) {
                        ToastUtil.showShort(getAppContext(), R.string.other_side_busy);
                    }
                }
            }
            finishActivity();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveToBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        RingVibrateManager.getInstance().stop();
        super.onDestroy();
    }

}
