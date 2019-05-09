package com.wewin.hichat.view.conversation;

import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
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

    private ImageView muteIv, handFreeIv, backIv, avatarIv, msgIv, hangUpCenterIv, hangUpLeftIv,
            answerIv;
    private TextView backTv, nameTv, callStateTv;
    private LinearLayout muteLl, msgLl, handFreeLl;
    private boolean isMute = false;
    private boolean isHandFree = false;
    public static final int TYPE_CALL_INVITING = 0;
    public static final int TYPE_CALL_WAIT_ANSWER = 1;
    private static final int TYPE_CALL_CONNECTING = 2;
    private static final int TYPE_CALL_CALLING = 3;
    private static final int TYPE_CALL_FINISH = 4;
    private int callType;
    private int callingSecond = 0;
    private final int MAX_WAIT_TIME = 6 * 1000;//等待接通最大时长
    private ChatRoom mChatRoom;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (callType == TYPE_CALL_CALLING) {
                callStateTv.setText(TimeUtil.formatTimeStr(callingSecond));
                handler.postDelayed(this, 1000);
                callingSecond++;
            }
        }
    };

    private CustomCountDownTimer waitAnswerTimer = new CustomCountDownTimer(MAX_WAIT_TIME, 1000) {
        @Override
        public void onFinish() {
            if (callType == TYPE_CALL_INVITING) {
                VoiceCallManager.getInstance().stop();
                finish();
                if (mChatRoom.getLastChatMsg() == null
                        || mChatRoom.getLastChatMsg().getVoiceCall() == null){
                    return;
                }
                VoiceCall voiceCall = mChatRoom.getLastChatMsg().getVoiceCall();
                voiceCall.setConnectState(VoiceCall.TIME_OUT);
                ChatSocket.getInstance().send(ChatRoomManager.packVoiceCallMsg(mChatRoom, voiceCall));
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_chat_call;
    }

    @Override
    protected void getIntentData() {
        mChatRoom = (ChatRoom)getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM);
        callType = getIntent().getIntExtra(ContactCons.EXTRA_MESSAGE_CHAT_CALL_TYPE, 0);
    }

    @Override
    protected void initViews() {
        backIv = findViewById(R.id.iv_message_chat_call_back);
        avatarIv = findViewById(R.id.civ_message_chat_call_avatar);
        muteIv = findViewById(R.id.iv_message_chat_call_mute);
        msgIv = findViewById(R.id.iv_message_chat_call_msg);
        handFreeIv = findViewById(R.id.iv_message_chat_call_hand_free);
        hangUpCenterIv = findViewById(R.id.iv_message_chat_call_hang_up_center);
        backTv = findViewById(R.id.tv_message_chat_call_back);
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
        LogUtil.i("initViewsData");

        setCenterTitle(0);

        if (mChatRoom == null){
            return;
        }

        FriendInfo contactUser = ContactUserDao.getContactUser(mChatRoom.getRoomId());
        if (contactUser!= null){
            ImgUtil.load(this, contactUser.getAvatar(), avatarIv);
            if (!TextUtils.isEmpty(contactUser.getFriendNote())){
                nameTv.setText(contactUser.getFriendNote());
            }else {
                nameTv.setText(contactUser.getUsername());
            }
        }

        Rigger.on(this).permissions(Permission.WRITE_EXTERNAL_STORAGE).start(new PermissionCallback() {
            @Override
            public void onGranted() {
                if (!VoiceCallManager.getInstance().isRunning()) {
                    VoiceCallManager.getInstance().init(getAppContext());

                    if (callType == TYPE_CALL_INVITING) {
                        String channel = UUIDUtil.get24UUID();
                        VoiceCallManager.getInstance().joinChannel(channel);
                        ChatMsg voiceCallMsg = ChatRoomManager.packVoiceCallInviteMsg(mChatRoom, channel);
                        ChatSocket.getInstance().send(voiceCallMsg);
                        mChatRoom.setLastChatMsg(voiceCallMsg);

                    } else if (callType == TYPE_CALL_WAIT_ANSWER) {
                        /*List<FriendInfo> friendInfoList = FriendDao.getFriendList();
                        for (FriendInfo info : friendInfoList) {
                            if (info.getId().equals(friendInfo.getId())) {
                                friendInfo = info;
                                friendInfo.setType("friend");
                                break;
                            }
                        }*/
                    }
                }
                setView();
            }

            @Override
            public void onDenied(HashMap<String, Boolean> permissions) {

            }
        });
    }

    @Override
    protected void setListener() {
        backIv.setOnClickListener(this);
        muteIv.setOnClickListener(this);
        msgIv.setOnClickListener(this);
        handFreeIv.setOnClickListener(this);
        hangUpCenterIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        answerIv.setOnClickListener(this);
        hangUpLeftIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_message_chat_call_back:
            case R.id.tv_message_chat_call_back:
                moveTaskToBack(true);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_right_out);
                break;

            case R.id.iv_message_chat_call_mute:
                if (isMute) {
                    isMute = false;
                    muteIv.setImageResource(R.drawable.mute_btn_off);
                } else {
                    isMute = true;
                    muteIv.setImageResource(R.drawable.mute_btn_open);
                }
                VoiceCallManager.getInstance().setMute(isMute);
                break;

            case R.id.iv_message_chat_call_msg:
                moveTaskToBack(true);
                overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_right_out);
                break;

            case R.id.iv_message_chat_call_hand_free:
                if (isHandFree) {
                    isHandFree = false;
                    handFreeIv.setImageResource(R.drawable.handsfree_btn_off);
                } else {
                    isHandFree = true;
                    handFreeIv.setImageResource(R.drawable.handsfree_btn_open);
                }
                VoiceCallManager.getInstance().setHandFree(isHandFree);
                break;

            case R.id.iv_message_chat_call_hang_up_center:
            case R.id.iv_message_chat_call_hang_up_left:
                if (mChatRoom == null || mChatRoom.getLastChatMsg() == null
                        || mChatRoom.getLastChatMsg().getVoiceCall() == null){
                    return;
                }
                VoiceCall voiceCall = mChatRoom.getLastChatMsg().getVoiceCall();
                if (callType == TYPE_CALL_INVITING) {
                    voiceCall.setConnectState(VoiceCall.CANCEL);

                } else if (callType == TYPE_CALL_WAIT_ANSWER) {
                    voiceCall.setConnectState(VoiceCall.REFUSE);

                } else if (callType == TYPE_CALL_CALLING) {
                    voiceCall.setConnectState(VoiceCall.FINISH);
                    voiceCall.setDuration(callingSecond);
                }
                ChatSocket.getInstance().send(ChatRoomManager.packVoiceCallMsg(mChatRoom, voiceCall));
                finish();
                break;

            case R.id.iv_message_chat_call_answer:
                if (mChatRoom == null || mChatRoom.getLastChatMsg() == null
                        || mChatRoom.getLastChatMsg().getVoiceCall() == null){
                    return;
                }
                VoiceCall voiceCall1 = mChatRoom.getLastChatMsg().getVoiceCall();
                VoiceCallManager.getInstance().joinChannel(voiceCall1.getChannel());
                callType = TYPE_CALL_CALLING;
                setView();
                voiceCall1.setConnectState(VoiceCall.CONNECT);
                ChatSocket.getInstance().send(ChatRoomManager.packVoiceCallMsg(mChatRoom, voiceCall1));
                waitAnswerTimer.cancel();
                break;
        }
    }

    private void setView() {
        switch (callType) {
            case TYPE_CALL_INVITING:
                waitAnswerTimer.start();
                callStateTv.setText(getString(R.string.waiting_for_accept));
                hangUpLeftIv.setVisibility(View.INVISIBLE);
                hangUpCenterIv.setVisibility(View.VISIBLE);
                answerIv.setVisibility(View.INVISIBLE);
                muteLl.setVisibility(View.INVISIBLE);
                msgLl.setVisibility(View.INVISIBLE);
                handFreeLl.setVisibility(View.INVISIBLE);
                break;

            case TYPE_CALL_WAIT_ANSWER:
                callStateTv.setText(getString(R.string.invite_voice_call));
                hangUpLeftIv.setVisibility(View.VISIBLE);
                hangUpCenterIv.setVisibility(View.INVISIBLE);
                answerIv.setVisibility(View.VISIBLE);
                muteLl.setVisibility(View.INVISIBLE);
                msgLl.setVisibility(View.INVISIBLE);
                handFreeLl.setVisibility(View.INVISIBLE);
                break;

            case TYPE_CALL_CONNECTING:
                callStateTv.setText(getString(R.string.connecting));
                hangUpLeftIv.setVisibility(View.INVISIBLE);
                hangUpCenterIv.setVisibility(View.VISIBLE);
                answerIv.setVisibility(View.INVISIBLE);
                muteLl.setVisibility(View.INVISIBLE);
                msgLl.setVisibility(View.INVISIBLE);
                handFreeLl.setVisibility(View.INVISIBLE);
                break;

            case TYPE_CALL_CALLING:
                handler.removeCallbacks(runnable);
                handler.post(runnable);
                hangUpLeftIv.setVisibility(View.INVISIBLE);
                hangUpCenterIv.setVisibility(View.VISIBLE);
                answerIv.setVisibility(View.INVISIBLE);
                muteLl.setVisibility(View.VISIBLE);
                msgLl.setVisibility(View.VISIBLE);
                handFreeLl.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        if (msg.getKey() == EventMsg.MESSAGE_CHAT_VOICE_CALL_REFRESH) {
            int voiceType = (int) msg.getData();
            if (voiceType == VoiceCall.CONNECT) {
                callType = TYPE_CALL_CALLING;
                setView();

            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(true);
            overridePendingTransition(R.anim.activity_slide_left_in, R.anim.activity_slide_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        VoiceCallManager.getInstance().stop();
        callType = TYPE_CALL_FINISH;
        handler.removeCallbacks(runnable);
        runnable = null;
        handler = null;
        waitAnswerTimer.cancel();
        super.onDestroy();
    }

}
