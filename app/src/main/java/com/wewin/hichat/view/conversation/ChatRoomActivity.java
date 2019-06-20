package com.wewin.hichat.view.conversation;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.androidlib.impl.CustomTextWatcher;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.permission.AuthorizationCheck;
import com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener;
import com.wewin.hichat.androidlib.rxjava.RxJavaObserver;
import com.wewin.hichat.androidlib.rxjava.RxJavaScheduler;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.FileOpenUtil;
import com.wewin.hichat.androidlib.utils.HyperLinkUtil;
import com.wewin.hichat.androidlib.utils.NameUtil;
import com.wewin.hichat.androidlib.utils.NotificationUtil;
import com.wewin.hichat.androidlib.utils.SystemUtil;
import com.wewin.hichat.androidlib.widget.CornerGifView;
import com.wewin.hichat.androidlib.widget.MyLinearLayoutManager;
import com.wewin.hichat.component.adapter.MessageChatRcvAdapter;
import com.wewin.hichat.component.base.BaseMessageChatRcvAdapter;
import com.wewin.hichat.component.dialog.AnnouncementDialog;
import com.wewin.hichat.component.dialog.ChatPopupWindow;
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.dialog.SelectPersonListDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.androidlib.permission.Permission;
import com.wewin.hichat.androidlib.permission.Rigger;
import com.wewin.hichat.androidlib.utils.ArithUtil;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.LubanCallBack;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.manage.TapeRecordManager;
import com.wewin.hichat.androidlib.widget.CustomCountDownTimer;
import com.wewin.hichat.androidlib.widget.KeyboardRelativeLayout;
import com.wewin.hichat.androidlib.widget.MicImageView;
import com.wewin.hichat.component.adapter.ChatRoomRightMoreGvAdapter;
import com.wewin.hichat.component.adapter.MessageChatEmoticonGvAdapter;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.component.base.BaseRcvTopLoadAdapter;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.androidlib.permission.PermissionCallback;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.ChatPlusDialog;
import com.wewin.hichat.component.manager.VoiceCallManager;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.Announcement;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.Emoticon;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.ImgUrl;
import com.wewin.hichat.model.db.entity.ReplyMsgInfo;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.model.http.HttpMessage;
import com.wewin.hichat.model.socket.ChatSocket;
import com.wewin.hichat.view.album.AlbumChoiceActivity;
import com.wewin.hichat.view.album.ImgShowActivity;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.view.contact.friend.FriendInfoActivity;
import com.wewin.hichat.view.contact.group.GroupInfoActivity;
import com.wewin.hichat.view.contact.group.GroupInviteMemberActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableEmitter;


/**
 * 聊天室
 *
 * @author Darren
 * Created by Darren on 2018/12/17.
 */
public class ChatRoomActivity extends BaseActivity implements ChatPopupWindow.OnChatPopListener {

    private RecyclerView containerRcv;
    private EditText contentInputEt, searchInputEt;
    private MicImageView tapeRecordIv;
    private ImageView voiceCallIv, botMoreIv, emoticonIv, sendMsgIv, recordPressIv, recordDeleteIv,
            leftSlideIv, rightExpandIv, searchClearIv, replyVideoIv, replyFinishIv;
    private TextView voiceTimingTv, leftSlidePromptTv, blackShelterTv, addFriendTv, ignoreTv,
            centerTitleTv, searchCancelTv, pullBlackListTv, replyNameTv, replyContentTv;
    private GridView emoticonGv;
    private FrameLayout inputContainerFl, searchContainerFl;
    private RelativeLayout titleLayoutRl, replyRl;
    private LinearLayout temporaryLl, leftBackLl, chatSendContainerLl;
    private CornerGifView replyIv;
    private KeyboardRelativeLayout rootViewKrl;
    private MyLinearLayoutManager layoutManager;

    private List<ChatMsg> mChatMsgList = new ArrayList<>();
    private List<Emoticon> emoticonList = new ArrayList<>();
    private MessageChatRcvAdapter rcvAdapter;
    private ChatPlusDialog chatPlusDialog;
    private PopupWindow rightMorePop;
    private ChatRoom mChatRoom;
    private MessageChatEmoticonGvAdapter emoticonGvAdapter;

    private int topLoadLastOffset = 0;//下拉加载消息偏移量
    private int mKeyboardHeight = 0;//键盘高度
    private long startRecordTime;//录音开始时间
    private int tapeRecordSecond = 1;//录音秒数
    private final int LEFT_SLIDE_AVAILABLE_X = -30;//左滑取消录音的起始X坐标
    private final int MAX_VOICE_RECORD_SECOND = 60 * 1000;//最大录音时长
    private final int PRESS_AVAILABLE_SECOND = 150;//按住150ms后才开始录音
    private boolean isKeyboardHeightSet = false;//是否已设置键盘高度
    private boolean isTapeRecordAvailable = true;//录音键/发送键切换
    private boolean isEmoticonActive = false;//键盘/表情切换
    private boolean isTapeRecordPermit = false;//录音权限是否开启
    private boolean isTapeRecording = false;//是否正在录音
    private boolean isNeedGetServerMsg = false;//是否需要请求服务器获取消息列表
    private long searchStartTimestamp;//聊天记录搜索的起始时间戳
    private Map<String, String> atMap = new HashMap<>();//@人的列表
    private ArrayList<String> emList = new ArrayList<>();//表情保存
    private String pageEarliestMsgId = null;//每页最早的一条消息id
    //记录当前同步删除的最老的一条消息
    private String mNotesMsgId;
    //获取当前列表最老的消息id
    private String minMsgId;
    //回复的消息
    private ReplyMsgInfo replyMsgInfo;
    /**
     * 长按消息弹窗，选择复制转发回复删除
     */
    private ChatPopupWindow chatPopupWindow;


    //录音计时器
    private CustomCountDownTimer recordCountDown
            = new CustomCountDownTimer(MAX_VOICE_RECORD_SECOND, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            voiceTimingTv.setText(TimeUtil.formatTimeStr(tapeRecordSecond));
            tapeRecordSecond++;
        }

        @Override
        protected void onFinish() {
            tapeRecordIv.setEnabled(false);
            resetTapeRecordView();
            stopTapeRecord();
        }
    };

    //录音长按是否生效计时器
    private CustomCountDownTimer pressCountDown
            = new CustomCountDownTimer(PRESS_AVAILABLE_SECOND, 150) {
        @Override
        public void onFinish() {
            botMoreIv.setVisibility(View.GONE);
            voiceCallIv.setVisibility(View.GONE);
            inputContainerFl.setVisibility(View.GONE);

            recordPressIv.setVisibility(View.VISIBLE);
            recordDeleteIv.setVisibility(View.VISIBLE);
            voiceTimingTv.setVisibility(View.VISIBLE);
            leftSlideIv.setVisibility(View.VISIBLE);
            leftSlidePromptTv.setVisibility(View.VISIBLE);

            recordCountDown.start();
            TapeRecordManager.getInstance().startRecord(getAppContext());
            startRecordTime = System.currentTimeMillis();
            isTapeRecording = true;

            AnimationDrawable animDrawable = (AnimationDrawable) leftSlideIv.getDrawable();
            if (animDrawable != null) {
                animDrawable.start();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_chat;
    }

    @Override
    protected void getIntentData() {
        searchStartTimestamp = getIntent().getLongExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, 0);
        mChatRoom = (ChatRoom) getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM);
        LogUtil.i("getIntentData", mChatRoom);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            searchStartTimestamp = intent.getLongExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, 0);
            mChatRoom = (ChatRoom) intent.getSerializableExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM);
            initViewsData();
            setListener();
        }
    }

    @Override
    protected void initViews() {
        containerRcv = findViewById(R.id.rcv_conversation_chat_container);
        contentInputEt = findViewById(R.id.et_conversation_chat_content_input);
        emoticonIv = findViewById(R.id.iv_conversation_chat_emoticon);
        tapeRecordIv = findViewById(R.id.iv_conversation_chat_tape_record);
        voiceCallIv = findViewById(R.id.iv_conversation_chat_voice_call);
        rootViewKrl = findViewById(R.id.krl_message_chat_root_layout);
        botMoreIv = findViewById(R.id.iv_conversation_chat_more);
        emoticonGv = findViewById(R.id.gv_conversation_chat_expression_container);
        sendMsgIv = findViewById(R.id.iv_conversation_chat_send);
        recordPressIv = findViewById(R.id.iv_conversation_chat_tape_record_press);
        recordDeleteIv = findViewById(R.id.iv_conversation_chat_tape_record_delete);
        voiceTimingTv = findViewById(R.id.tv_conversation_chat_tape_record_timing);
        leftSlideIv = findViewById(R.id.iv_conversation_chat_tape_record_anim);
        leftSlidePromptTv = findViewById(R.id.tv_conversation_chat_tape_record_left_slide_prompt);
        inputContainerFl = findViewById(R.id.fl_conversation_chat_input_container);
        blackShelterTv = findViewById(R.id.tv_conversation_chat_black_shelter);
        chatSendContainerLl = findViewById(R.id.ll_conversation_chat_send_container);
        addFriendTv = findViewById(R.id.tv_conversation_chat_temporary_add_friend);
        ignoreTv = findViewById(R.id.tv_conversation_chat_temporary_ignore);
        temporaryLl = findViewById(R.id.ll_conversation_chat_temporary_prompt);
        leftBackLl = findViewById(R.id.ll_conversation_chat_left_back_container);
        centerTitleTv = findViewById(R.id.tv_conversation_chat_center_title);
        rightExpandIv = findViewById(R.id.iv_conversation_chat_right_expand);
        titleLayoutRl = findViewById(R.id.rl_conversation_chat_title_layout);
        searchCancelTv = findViewById(R.id.tv_conversation_chat_search_cancel);
        searchInputEt = findViewById(R.id.et_conversation_chat_search_input);
        searchClearIv = findViewById(R.id.iv_conversation_chat_search_clear);
        searchContainerFl = findViewById(R.id.fl_conversation_chat_search_container);
        pullBlackListTv = findViewById(R.id.tv_conversation_chat_temporary_blacklist);
        replyRl = findViewById(R.id.rl_message_chat_reply);
        replyIv = findViewById(R.id.iv_message_chat_reply_img);
        replyVideoIv = findViewById(R.id.iv_message_chat_reply_video);
        replyNameTv = findViewById(R.id.tv_message_chat_reply_name);
        replyContentTv = findViewById(R.id.tv_message_chat_reply_type);
        replyFinishIv = findViewById(R.id.iv_message_chat_reply_finish);
    }

    @Override
    protected void initViewsData() {
        if (mChatRoom == null) {
            return;
        }
        getWindow().setBackgroundDrawable(null);
        NotificationUtil.clearNotification(this);
        chatPopupWindow = new ChatPopupWindow(ChatRoomActivity.this);
        ChatRoomManager.setCurrentRoomId(mChatRoom.getRoomId());
        ChatRoomManager.setCurrentRoomType(mChatRoom.getRoomType());
        mKeyboardHeight = SpCons.getKeyboardHeight(getAppContext());
        initRecyclerView();
        setRoomTypeView();
        initMsgDataList();
        getNewAnnouncement();
        // 起初的布局可自动调整大小
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initDeaft();
    }


    /**
     * 处理草稿
     */
    private void initDeaft() {
        try {
            String draft = SpCons.getString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT);
            if (!TextUtils.isEmpty(draft)) {
                contentInputEt.setText(SpCons.getString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT));
                contentInputEt.setSelection(contentInputEt.getText().length());
                String em = SpCons.getString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT_EM_LIST);
                if (!TextUtils.isEmpty(em)) {
                    emList = (ArrayList<String>) JSON.parseArray(em, String.class);
                }
                String at = SpCons.getString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT_AT_MAP);
                if (!TextUtils.isEmpty(at)) {
                    atMap = (Map<String, String>) JSON.parseObject(at, Map.class);
                }
                String reply = SpCons.getString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT_REPLY);
                if (!TextUtils.isEmpty(reply)) {
                    replyMsgInfo = JSON.parseObject(reply, ReplyMsgInfo.class);
                    replyView();
                }
            }
            changeSendMsgView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeSendMsgView() {
        isTapeRecordAvailable = !(contentInputEt.getText().toString().trim().length() > 0);
        if (isTapeRecordAvailable) {
            tapeRecordIv.setVisibility(View.VISIBLE);
            sendMsgIv.setVisibility(View.INVISIBLE);
        } else {
            tapeRecordIv.setVisibility(View.INVISIBLE);
            sendMsgIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListener() {
        emoticonIv.setOnClickListener(this);
        voiceCallIv.setOnClickListener(this);
        contentInputEt.setOnClickListener(this);
        botMoreIv.setOnClickListener(this);
        sendMsgIv.setOnClickListener(this);
        addFriendTv.setOnClickListener(this);
        ignoreTv.setOnClickListener(this);
        leftBackLl.setOnClickListener(this);
        rightExpandIv.setOnClickListener(this);
        searchClearIv.setOnClickListener(this);
        searchCancelTv.setOnClickListener(this);
        pullBlackListTv.setOnClickListener(this);
        replyFinishIv.setOnClickListener(this);
        chatPopupWindow.setOnChatPopListener(this);

        contentInputEt.addTextChangedListener(new CustomTextWatcher() {
            int delIndex = -1;
            int delLength = 0;

            @Override
            public void afterTextChanged(Editable s) {
                changeSendMsgView();
                if (delIndex >= 0 && delLength > 0) {
                    int temp1 = delIndex;
                    int temp2 = delLength;
                    delIndex = -1;
                    delLength = 0;
                    s.delete(temp1 - temp2, temp1);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //说明，是删除
                if (count > 0 && after == 0) {
                    CharSequence temp = contentInputEt.getText();
                    if (temp.charAt(start) == ' ') {
                        //检查是不是@
                        checkIsAt(start);
                    } else if (temp.charAt(start) == ']') {
                        //检查是不是表情
                        checkIsEmoticon(start);
                    }
                }
            }

            private void checkIsEmoticon(int index) {
                if (index <= 0) {
                    return;
                }
                String sub = contentInputEt.getText().toString().substring(0, index);
                //如果子串是空，返回
                if (sub.length() == 0) {
                    return;
                }
                int atIndex = sub.lastIndexOf("face[");
                if (atIndex < 0) {
                    return;
                }
                if (index - atIndex <= 1) {
                    return;
                }
                String name = sub.substring(atIndex, index) + "]";
                for (String value : emList) {
                    if (value.equals(name)) {
                        delIndex = index;
                        delLength = name.length() - 1;
                        return;
                    }
                }

            }

            //检查是不是@
            private void checkIsAt(int index) {
                if (index <= 0) {
                    return;
                }
                String sub = contentInputEt.getText().toString().substring(0, index);
                //如果子串是空，返回
                if (sub.length() == 0) {
                    return;
                }
                int atIndex = sub.lastIndexOf('@');
                //如果没有@，返回
                if (atIndex < 0) {
                    return;
                }
                //空格前面就是@，返回
                if (index - atIndex <= 1) {
                    return;
                }
                //+1是因为去掉@
                String name = sub.substring(atIndex + 1, index);
                for (String value : atMap.values()) {
                    if (value.equals(name)) {
                        delIndex = index;
                        //+1是因为把@也要删除掉
                        delLength = name.length() + 1;
                        atMap.remove(value);
                        return;
                    }
                }
            }
        });

        //监听输入框显示群成员@功能
        contentInputEt.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //是群聊，输入了单独一个@时
                if (source.length() == 1 && "@".equals(source.toString())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                    showSelectPersonPop();
                    return source;
                }
                return source;
            }
        }});

        searchInputEt.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    searchClearIv.setVisibility(View.VISIBLE);
                } else {
                    searchClearIv.setVisibility(View.INVISIBLE);
                }
            }
        });

        //长按录音
        tapeRecordIv.setOnTouchListener(new MicImageView.OnTouchListener() {
            @Override
            public void actionDown() {
                Rigger.on(getHostActivity()).permissions(Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.RECORD_AUDIO)
                        .start(new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                isTapeRecordPermit = true;
                            }

                            @Override
                            public void onDenied(HashMap<String, Boolean> permissions) {
                                isTapeRecordPermit = false;
                                if (!permissions.get(Permission.RECORD_AUDIO)) {
                                    new PromptDialog.PromptBuilder(ChatRoomActivity.this)
                                            .setPromptContent(getString(R.string.call_prompt))
                                            .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                                                @Override
                                                public void confirmClick() {
                                                    AuthorizationCheck.openApplication(ChatRoomActivity.this);
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                            }
                        });
                if (isTapeRecordPermit) {
                    pressCountDown.start();
                }
            }

            @Override
            public void actionUp(float upX) {
                tapeRecordIv.setEnabled(true);
                resetTapeRecordView();
                if (upX < LEFT_SLIDE_AVAILABLE_X) {
                    TapeRecordManager.getInstance().cancelRecord();

                } else if (isTapeRecording) {
                    stopTapeRecord();
                    contentInputEt.requestFocus();
                }
            }
        });

        rootViewKrl.setOnKeyboardLayoutListener(new KeyboardRelativeLayout.OnKeyboardLayoutListener() {
            @Override
            public void keyboardStateChanged(boolean isKeyboardActive, int keyboardHeight) {
                if (isKeyboardActive && !isKeyboardHeightSet) {
                    mKeyboardHeight = keyboardHeight;
                    SpCons.setKeyboardHeight(getAppContext(), keyboardHeight);
                    isKeyboardHeightSet = true;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_conversation_chat_send:
                sendMessage();
                break;

            case R.id.iv_conversation_chat_more:
                showBotMoreDialog();
                break;

            case R.id.iv_conversation_chat_emoticon:
                initEmoticonGridView();
                showEmoticonGv();
                break;

            case R.id.iv_conversation_chat_voice_call:
                VoiceCallManager.get().startVoiceCallActivity(getAppContext(), mChatRoom);
                break;

            case R.id.et_conversation_chat_content_input:
                showKeyboard();
                break;

            case R.id.tv_conversation_chat_temporary_add_friend:
                DataCache spCache = new SpCache(getAppContext());
                Subgroup friendSubgroup = (Subgroup) spCache.getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);
                applyAddFriend(mChatRoom.getRoomId(), SpCons.getUser(getAppContext()).getUsername() +
                        getString(R.string.apply_add_friend), friendSubgroup.getId());
                break;

            case R.id.tv_conversation_chat_temporary_ignore:
                startTemporaryGoneAnim();
                break;

            case R.id.ll_conversation_chat_left_back_container:
                getHostActivity().finish();
                break;

            case R.id.iv_conversation_chat_right_expand:
                showRightMorePop();
                break;

            case R.id.iv_conversation_chat_search_clear:
                searchInputEt.setText("");
                break;

            case R.id.tv_conversation_chat_search_cancel:
                searchContainerFl.setVisibility(View.INVISIBLE);
                titleLayoutRl.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_conversation_chat_temporary_blacklist:
                pullBlack();
                break;
            case R.id.iv_message_chat_reply_finish:
                replyMsgInfo = null;
                replyRl.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void sendMessage() {
        if (isTapeRecordAvailable) {
            return;
        }
        String messageStr = contentInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(messageStr)) {
            ToastUtil.showShort(getAppContext(), R.string.message_cannot_null);

        } else {
            contentInputEt.setText("");
            emList.clear();

            for (Iterator<Map.Entry<String, String>> it = atMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> item = it.next();
                if (!messageStr.contains(item.getValue())) {
                    it.remove();
                }
            }
            String atContent = JSONObject.toJSONString(atMap);
            if (replyMsgInfo != null) {
                ChatSocket.getInstance().send(ChatRoomManager.packReplyMsg(mChatRoom, messageStr, atContent, replyMsgInfo));
            } else {
                if (atMap == null || atMap.size() <= 0) {
                    ChatSocket.getInstance().send(ChatRoomManager.packTextMsg(mChatRoom, messageStr, replyMsgInfo));
                } else {
                    ChatSocket.getInstance().send(ChatRoomManager.packTextMsg(mChatRoom, messageStr, atContent, replyMsgInfo));
                }
            }
            replyMsgInfo = null;
            replyRl.setVisibility(View.GONE);
            atMap.clear();
        }
    }

    private void showKeyboard() {
        //延迟一段时间，等待输入法完全弹出
        rootViewKrl.postDelayed(new Runnable() {
            @Override
            public void run() {
                //输入法弹出之后，重新调整
                layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                emoticonGv.setVisibility(View.GONE);
                emoticonIv.setImageResource(R.drawable.con_emoticon_03);
                isEmoticonActive = false;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }, 200);
    }

    private void initMsgDataList() {
        if (mChatRoom.getLastChatMsg() != null) {
            setMessageRead(mChatRoom.getLastChatMsg().getMsgId(), ChatMsg.TYPE_READ_NORMAL, true);
        }
        //如果searchStartTimestamp > 0,则搜索历史消息记录
        if (searchStartTimestamp > 0) {
            List<ChatMsg> beforeList = MessageDao.getMessageBeforeList(mChatRoom.getRoomId(),
                    mChatRoom.getRoomType(), searchStartTimestamp);
            List<ChatMsg> afterList = MessageDao.getMessageAfterList(mChatRoom.getRoomId(),
                    mChatRoom.getRoomType(), searchStartTimestamp);
            if (afterList != null && beforeList != null) {
                afterList.addAll(0, beforeList);
            }
            mChatMsgList.clear();
            mChatMsgList.addAll(afterList);
            updateRcv();

            if (beforeList != null) {
                if (rcvAdapter.getTopPullViewVisible()) {
                    layoutManager.scrollToPositionWithOffset(beforeList.size() + 1, 0);
                } else {
                    layoutManager.scrollToPositionWithOffset(beforeList.size(), 0);
                }
            } else {
                layoutManager.scrollToPositionWithOffset(1, 0);
            }
            oldServerMsgDelete();
        } else {
            //正常打开聊天页面
            List<ChatMsg> dataList = MessageDao.getMessageBeforeList(mChatRoom.getRoomId(),
                    mChatRoom.getRoomType(), TimeUtil.getServerTimestamp());
            String maxUnSyncMsgId = MessageDao.getMaxUnSyncMsgId(mChatRoom);
            if (!dataList.isEmpty()) {
                String pageFirstMsgId = dataList.get(0).getMsgId();
                //如果最大未同步消息id为空 或（数据库第一页最早一条消息id大于最大未同步消息id，且第一页数据条数满一页），
                //则只加载本地数据。否则调接口
                boolean isLoadLocalData = TextUtils.isEmpty(maxUnSyncMsgId)
                        || (Long.parseLong(pageFirstMsgId) > Long.parseLong(maxUnSyncMsgId)
                        && dataList.size() >= MessageDao.getPageSize());
                mChatMsgList.clear();
                mChatMsgList.addAll(dataList);
                updateRcv();
                if (rcvAdapter.getTopPullViewVisible()) {
                    layoutManager.scrollToPositionWithOffset(mChatMsgList.size(), 0);
                } else {
                    layoutManager.scrollToPositionWithOffset(mChatMsgList.size() - 1, 0);
                }
                if (isLoadLocalData) {
                    oldServerMsgDelete();
                } else {
                    getServerMsgList(1, "0");
                }
            } else {
                getServerMsgList(1, "0");
            }
        }
    }

    private void setRoomTypeView() {
        switch (mChatRoom.getRoomType()) {
            case ChatRoom.TYPE_SINGLE:
                FriendInfo friend = FriendDao.getFriendInfo(mChatRoom.getRoomId());
                if (friend == null || friend.getFriendship() == 0) {
                    //临时会话
                    centerTitleTv.setText(String.format(getString(R.string.temporary_chat), NameUtil.getName(mChatRoom.getRoomId())));
                    temporaryLl.setVisibility(View.VISIBLE);
                } else {
                    //单聊
                    centerTitleTv.setText(NameUtil.getName(mChatRoom.getRoomId()));
                    temporaryLl.setVisibility(View.GONE);
                }
                voiceCallIv.setVisibility(View.VISIBLE);
                botMoreIv.setVisibility(View.VISIBLE);
                voiceCallIv.setVisibility(View.VISIBLE);

                //拉黑禁止发送消息
                if (mChatRoom.getBlackMark() == 1) {
                    blackShelterTv.setVisibility(View.VISIBLE);
                    blackShelterTv.setText(R.string.cannot_send_msg_to_black_person);
                    chatSendContainerLl.setVisibility(View.INVISIBLE);
                } else {
                    blackShelterTv.setVisibility(View.INVISIBLE);
                    chatSendContainerLl.setVisibility(View.VISIBLE);
                }
                break;

            case ChatRoom.TYPE_GROUP:
                //群聊，无语音通话
                GroupInfo groupInfo = GroupDao.getGroup(mChatRoom.getRoomId());
                if (groupInfo != null) {
                    centerTitleTv.setText(groupInfo.getGroupName());
                }
                voiceCallIv.setVisibility(View.GONE);
                botMoreIv.setVisibility(View.VISIBLE);
                temporaryLl.setVisibility(View.GONE);
                setGroupSpeakView();
                break;

            default:
                break;
        }
    }

    /**
     * 显示@弹窗，提供用户选择@的人
     */
    private void showSelectPersonPop() {
        final List<FriendInfo> list = GroupMemberDao.getGroupMemberListNoMy(mChatRoom.getRoomId());
        SelectPersonListDialog selectPersonListDialog = new SelectPersonListDialog(this, list);
        selectPersonListDialog.setListOnClick(new SelectPersonListDialog.ListOnClick() {
            @Override
            public void onClickItem(int position) {
                String id = list.get(position).getId();
                String name = list.get(position).getUsername();
                inputATMember(id, name, true);
            }

            @Override
            public void allMember() {
                inputATMember("0", "全体成员", true);
            }
        });
        selectPersonListDialog.showDialog();
    }

    /**
     * 把@的消息输入到文本框中
     */
    private void inputATMember(String id, String name, boolean inputMark) {
        int index = contentInputEt.getSelectionStart();
        if (inputMark) {
            Editable editable = contentInputEt.getText();
            editable.delete(index - 1, index);
        }
        atMap.put(id, name);
        String atContentStr = "@" + name + " ";
        Editable editText = contentInputEt.getEditableText();
        index = contentInputEt.getSelectionStart();
        editText.insert(index, atContentStr);
    }

    private void pullBlack() {
        new PromptDialog.PromptBuilder(this)
                .setPromptContent(getString(R.string.pull_black_prompt))
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        pullBlackFriend(mChatRoom.getRoomId(),
                                FriendDao.findFriendshipMark(mChatRoom.getRoomId()));
                    }
                })
                .create()
                .show();
    }


    /**
     * 展示公告信息
     */
    private AnnouncementDialog.PromptBuilder aVoid;

    private void announcementDialog(Announcement announcement) {
        if (aVoid == null) {
            aVoid = new AnnouncementDialog.PromptBuilder(this);
        }
        String name = NameUtil.getName(announcement.getAccount().getId());
        aVoid.setPromptTitle(announcement.getTitle())
                .setPromptNameTime(name + "发布于" + TimeUtil.timestampToStr(announcement.getPostTime(), "yyyy/MM/dd"))
                .setPromptContent(announcement.getContent());
        if (!aVoid.isShowing()) {
            aVoid.setCancelableOnTouchOutside(false)
                    .create()
                    .show();
        }
    }

    private void setGroupSpeakView() {
        if (mChatRoom.getGroupGrade() == GroupInfo.TYPE_GRADE_NORMAL
                && mChatRoom.getGroupSpeakMark() == 0) {
            blackShelterTv.setVisibility(View.VISIBLE);
            blackShelterTv.setText(R.string.manager_open_ban_speak);
            chatSendContainerLl.setVisibility(View.INVISIBLE);
            SystemUtil.hideKeyboard(this);
        } else {
            blackShelterTv.setVisibility(View.INVISIBLE);
            chatSendContainerLl.setVisibility(View.VISIBLE);
        }
    }

    private void startTemporaryGoneAnim() {
        ObjectAnimator.ofFloat(temporaryLl, "translationY",
                -temporaryLl.getMeasuredHeight())
                .setDuration(200)
                .start();
    }

    /**
     * 结束录音时重置界面
     */
    private void resetTapeRecordView() {
        botMoreIv.setVisibility(View.VISIBLE);
        if (ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
            voiceCallIv.setVisibility(View.VISIBLE);
        }
        inputContainerFl.setVisibility(View.VISIBLE);
        recordPressIv.setVisibility(View.INVISIBLE);
        recordDeleteIv.setVisibility(View.INVISIBLE);
        voiceTimingTv.setVisibility(View.INVISIBLE);
        leftSlideIv.setVisibility(View.INVISIBLE);
        leftSlidePromptTv.setVisibility(View.INVISIBLE);

        tapeRecordSecond = 1;
        recordCountDown.cancel();
        pressCountDown.cancel();
        voiceTimingTv.setText(TimeUtil.formatTimeStr(0));

        AnimationDrawable animDrawable = (AnimationDrawable) leftSlideIv.getDrawable();
        if (animDrawable != null) {
            animDrawable.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetTapeRecordView();
        SpCons.setString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT, contentInputEt.getText().toString());
        SpCons.setString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT_EM_LIST, JSON.toJSONString(emList));
        SpCons.setString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT_AT_MAP, JSON.toJSONString(atMap));
        if (replyMsgInfo != null) {
            SpCons.setString(this, UserDao.user.getId() + mChatRoom.getRoomId() + mChatRoom.getRoomType() + SpCons.DRAFT_REPLY, JSON.toJSONString(replyMsgInfo));
        }
        //这里需要刷新会话列表，所以不重复调用刷新草稿变化
        //通知会话界面未读消息数量置为0
        EventTrans.post(EventMsg.CONVERSATION_UNREAD_NUM_REFRESH, mChatRoom.getRoomId(), mChatRoom.getRoomType());
    }

    /**
     * 结束录音
     */
    private void stopTapeRecord() {
        TapeRecordManager.getInstance().stopRecord(new TapeRecordManager.OnVoiceListener() {
            @Override
            public void stop(String savePath) {
                isTapeRecording = false;
                long stopRecordTime = System.currentTimeMillis();
                long diffTime = stopRecordTime - startRecordTime;
                int recordSecond = (int) ArithUtil.round(Double.parseDouble(ArithUtil
                        .div(String.valueOf(diffTime), String.valueOf(1000))), 0);
                if (recordSecond > 0.5) {
                    if (recordSecond > MAX_VOICE_RECORD_SECOND / 1000) {
                        recordSecond = MAX_VOICE_RECORD_SECOND / 1000;
                    }
                    final File file = new File(savePath);
                    final FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(file.getName());
                    fileInfo.setFileType(FileInfo.TYPE_TAPE_RECORD);
                    fileInfo.setFileLength(file.length());
                    fileInfo.setOriginPath(savePath);
                    fileInfo.setDuration(recordSecond);
                    //切到主线程
                    RxJavaScheduler.execute(new OnRxJavaProcessListener() {
                        @Override
                        public void process(ObservableEmitter<Object> emitter) {
                            while (!TapeRecordManager.getInstance().getFlushAndRelease()) {
                                LogUtil.e("jason--:占用中");
                            }
                            LogUtil.e("jason--:未被占用");
                        }
                    }, new RxJavaObserver<Object>() {
                        @Override
                        public void onComplete() {
                            LogUtil.e("jason--:" + file.exists() + "   " + file.length());
                            ChatRoomManager.uploadFile(getAppContext(), mChatRoom, fileInfo);
                        }
                    });


                } else {
                    ToastUtil.showShort(getAppContext(), R.string.voice_record_too_short);
                }
            }
        });
    }

    private void initRecyclerView() {
        rcvAdapter = new MessageChatRcvAdapter(this, mChatMsgList);
        layoutManager = new MyLinearLayoutManager(getAppContext());
        containerRcv.setLayoutManager(layoutManager);
        containerRcv.setAdapter(rcvAdapter);

        containerRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (rcvAdapter.getItemCount() > 2) {
                    //获取可视的第一个view
                    View topView = layoutManager.getChildAt(1);
                    if (topView != null) {
                        //获取与该view的顶部的偏移量
                        topLoadLastOffset = topView.getTop();
                    }
                }
            }
        });

        //下拉加载
        rcvAdapter.setOnLoadDataListener(new BaseRcvTopLoadAdapter.OnLoadDataListener() {
            @Override
            public void loadData() {
                if (isNeedGetServerMsg) {
                    getServerMsgList(0, pageEarliestMsgId);

                } else {
                    long beginTimestamp;
                    if (mChatMsgList.isEmpty()) {
                        beginTimestamp = TimeUtil.getServerTimestamp();
                    } else {
                        beginTimestamp = mChatMsgList.get(0).getCreateTimestamp();
                    }
                    List<ChatMsg> dataList = MessageDao.getMessageBeforeList(mChatRoom.getRoomId(),
                            mChatRoom.getRoomType(), beginTimestamp);
                    if (dataList.isEmpty()) {
                        rcvAdapter.setTopPullViewVisible(false);
                        return;
                    }
                    mChatMsgList.addAll(0, dataList);
                    updateRcv();
                    if (rcvAdapter.getTopPullViewVisible()) {
                        layoutManager.scrollToPositionWithOffset(dataList.size() + 1, topLoadLastOffset);
                    } else {
                        layoutManager.scrollToPositionWithOffset(dataList.size(), topLoadLastOffset);
                    }
                    oldServerMsgDelete();
                }

            }
        });
        rcvAdapter.setOmMsgLongClickListener(new BaseMessageChatRcvAdapter.OmMsgLongClickListener() {
            @Override
            public void textLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }

            @Override
            public void voiceCallLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }

            @Override
            public void imgLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }

            @Override
            public void videoLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }

            @Override
            public void docLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }

            @Override
            public void tapeRecordLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }

            @Override
            public void avatarLeftLongClick(int position, View view) {
                String id = mChatMsgList.get(position).getSenderId();
                String name = ContactUserDao.getContactUser(mChatMsgList.get(position).getSenderId()).getUsername();
                inputATMember(id, name, false);
            }

            @Override
            public void replyLongClick(int position, View view) {
                if (chatPopupWindow != null) {
                    chatPopupWindow.showPopupWindow(mChatMsgList.get(position), view, position);
                }
            }
        });
        //各类型消息点击事件
        rcvAdapter.setOnMsgClickListener(new MessageChatRcvAdapter.OnMsgClickListener() {
            @Override
            public void docClick(int position) {
                FileInfo fileInfo = mChatMsgList.get(position).getFileInfo();
                if (fileInfo == null || (fileInfo.getFileType() != FileInfo.TYPE_DOC
                        && fileInfo.getFileType() != FileInfo.TYPE_MUSIC)) {
                    return;
                }
                openFile(fileInfo, position);
            }

            @Override
            public void videoClick(int position) {
                if (mChatMsgList.get(position).getFileInfo() == null) {
                    return;
                }
                intentPlayVideo(mChatMsgList.get(position).getFileInfo());
            }

            @Override
            public void imgClick(int position) {
                if (mChatMsgList.get(position).getFileInfo() == null) {
                    return;
                }
                clickImage(position);
            }

            @Override
            public void tapeRecordClick(final int position) {
                openTapeRecord(mChatMsgList.get(position).getFileInfo(), position);
            }

            @Override
            public void voiceCallClick() {
                VoiceCallManager.get().startVoiceCallActivity(getAppContext(), mChatRoom);
            }

            @Override
            public void replyClick(final int position) {
                ChatMsg chatMsg = mChatMsgList.get(position);
                if (chatMsg.getReplyMsgInfo() != null && chatMsg.getReplyMsgInfo().getFileInfo() != null) {
                    final FileInfo fileInfo = chatMsg.getReplyMsgInfo().getFileInfo();
                    switch (fileInfo.getFileType()) {
                        case FileInfo.TYPE_IMG:
                            clickImage(position);
                            break;
                        case FileInfo.TYPE_VIDEO:
                            intentPlayVideo(fileInfo);
                            break;
                        case FileInfo.TYPE_DOC:
                        case FileInfo.TYPE_MUSIC:
                            openFile(fileInfo, position);
                            break;
                        case FileInfo.TYPE_TAPE_RECORD:
                            openTapeRecord(fileInfo, position);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void avatarLeftClick(int position) {
                if (ChatRoom.TYPE_SINGLE.equals(mChatMsgList.get(position).getRoomType())) {
                    Intent intent = new Intent(getAppContext(), FriendInfoActivity.class);
                    intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, mChatRoom);
                    startActivity(intent);
                } else if (ChatRoom.TYPE_GROUP.equals(mChatMsgList.get(position).getRoomType())) {
                    if (GroupDao.getAddMark(mChatRoom.getRoomId()) == 0 && FriendDao.findFriendshipMark(mChatMsgList.get(position).getSenderId()) != 1) {
                        ToastUtil.showShort(ChatRoomActivity.this, getString(R.string.allow_add_prompt));
                        return;
                    }
                    Intent intent = new Intent(getAppContext(), FriendInfoActivity.class);
                    ChatRoom chatRoom = ChatRoomManager.getChatRoom(mChatMsgList.get(position).getSenderId(),
                            ChatRoom.TYPE_SINGLE);
                    intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 点击打开录音文件
     *
     * @param fileInfo
     * @param position
     */
    private void openTapeRecord(final FileInfo fileInfo, final int position) {
        Rigger.on(getHostActivity()).permissions(Permission.WRITE_EXTERNAL_STORAGE)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        fileInfo.setTapeUnreadMark(0);
                        if (!FileUtil.isFileExists(fileInfo.getOriginPath())
                                && !FileUtil.isFileExists(fileInfo.getSavePath())) {
                            String tapeSaveDir = FileUtil.getSDTapeRecordPath(getAppContext());
                            FileUtil.createDir(tapeSaveDir + ".nomedia");
                            downloadFile(mChatMsgList.get(position), tapeSaveDir);
                        } else {
                            playVoiceRecord(mChatMsgList.get(position));
                        }
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {
                    }
                });
    }

    /**
     * 点击打开文件
     *
     * @param fileInfo
     * @param position
     */
    private void openFile(FileInfo fileInfo, int position) {
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())
                && FileUtil.isFileExists(fileInfo.getOriginPath())) {
            FileOpenUtil.openFile(getHostActivity(), fileInfo.getOriginPath());

        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())
                && FileUtil.isFileExists(fileInfo.getSavePath())) {
            FileOpenUtil.openFile(getHostActivity(), fileInfo.getSavePath());

        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            String docSaveDir = FileUtil.getSdDocPath(getAppContext());
            downloadFile(mChatMsgList.get(position), docSaveDir);
        }
    }

    /**
     * 点击查看视频
     *
     * @param fileInfo
     */
    private void intentPlayVideo(FileInfo fileInfo) {
        Intent intent = new Intent(getAppContext(), VideoPlayActivity.class);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_FILE, fileInfo);
        startActivity(intent);
    }

    /**
     * 点击查看大图
     *
     * @param position
     */
    private void clickImage(int position) {
        List<ImgUrl> imgUrlList = new ArrayList<>();
        int imgClickPosition = 0;
        for (ChatMsg chatMsg : mChatMsgList) {
            if (chatMsg.getFileInfo() != null
                    && !TextUtils.isEmpty(chatMsg.getFileInfo().getDownloadPath())
                    && chatMsg.getFileInfo().getFileType() == FileInfo.TYPE_IMG) {

                ImgUrl imgUrl = new ImgUrl(chatMsg.getFileInfo().getDownloadPath());
                imgUrl.setFileName(chatMsg.getFileInfo().getFileName());
                imgUrlList.add(imgUrl);
                if (chatMsg.getMsgId().equals(mChatMsgList.get(position).getMsgId())) {
                    imgClickPosition = imgUrlList.indexOf(imgUrl);
                }
            }
            if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY
                    && chatMsg.getReplyMsgInfo() != null
                    && chatMsg.getReplyMsgInfo().getFileInfo() != null
                    && chatMsg.getReplyMsgInfo().getFileInfo().getFileType() == FileInfo.TYPE_IMG
                    && !TextUtils.isEmpty(chatMsg.getReplyMsgInfo().getFileInfo().getDownloadPath())) {
                ImgUrl imgUrl = new ImgUrl(chatMsg.getReplyMsgInfo().getFileInfo().getDownloadPath());
                imgUrl.setFileName(chatMsg.getReplyMsgInfo().getFileInfo().getFileName());
                imgUrlList.add(imgUrl);
                if (chatMsg.getMsgId().equals(mChatMsgList.get(position).getMsgId())) {
                    imgClickPosition = imgUrlList.indexOf(imgUrl);
                }
            }
        }
        Intent intent = new Intent(getAppContext(), ImgShowActivity.class);
        intent.putExtra(ImgUtil.IMG_DONWLOAD, true);
        intent.putExtra(ImgUtil.IMG_CLICK_POSITION, imgClickPosition);
        intent.putExtra(ImgUtil.IMG_LIST_KEY, (Serializable) imgUrlList);
        startActivity(intent);
    }

    private void initEmoticonGridView() {
        if (emoticonGvAdapter == null) {
            emoticonGvAdapter = new MessageChatEmoticonGvAdapter(getAppContext(),
                    emoticonList);
            emoticonGv.setAdapter(emoticonGvAdapter);
            emoticonGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Emoticon emoticon = emoticonList.get(position);
                    int cursor = contentInputEt.getSelectionStart();
                    String tag = emoticon.getTag();
                    SpannableString spannableString = new SpannableString(tag);
                    int resId = getResources().getIdentifier(emoticon.getId(), "drawable",
                            getPackageName());
                    // 通过上面匹配得到的字符串来生成图片资源id
                    if (resId != 0) {
                        Drawable drawable = getResources().getDrawable(resId);
                        drawable.setBounds(0, 0, SystemUtil.dp2px(getAppContext(), 20),
                                SystemUtil.dp2px(getAppContext(), 20));
                        // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                        // 计算该图片名字的长度，也就是要替换的字符串的长度
                        // 将该图片替换字符串中规定的位置中
                        spannableString.setSpan(imageSpan, 0, tag.length(),
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        contentInputEt.getText().insert(cursor, spannableString);
                        emList.add(tag);
                    }
                }
            });
            emoticonList.clear();
            emoticonList.addAll(EmoticonUtil.getEmoticonList());
            emoticonGvAdapter.notifyDataSetChanged();
        }
    }

    //底部更多按钮
    private void showBotMoreDialog() {
        if (chatPlusDialog == null) {
            ChatPlusDialog.Builder dialogBuilder = new ChatPlusDialog.Builder(this);
            chatPlusDialog = dialogBuilder.setOnSelectClickListener(new ChatPlusDialog.Builder.OnSelectClickListener() {
                @Override
                public void albumClick() {
                    Intent intent = new Intent(getAppContext(), AlbumChoiceActivity.class);
                    intent.putExtra(ImgUtil.ALBUM_OPEN_KEY, -1);
                    startActivityForResult(intent, ImgUtil.REQUEST_ALBUM);
                }

                @Override
                public void cameraClick() {
                    ImgUtil.openCamera(getHostActivity());
                }

                @Override
                public void fileSend() {
                    Rigger.on(getHostActivity()).permissions(Permission.WRITE_EXTERNAL_STORAGE)
                            .start(new PermissionCallback() {
                                @Override
                                public void onGranted() {
                                    Intent intent = new Intent(getAppContext(), ChatFileActivity.class);
                                    intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, mChatRoom);
                                    startActivity(intent);
                                }

                                @Override
                                public void onDenied(HashMap<String, Boolean> permissions) {

                                }
                            });
                }
            }).create();
        }
        chatPlusDialog.show();
    }

    private void showEmoticonGv() {
        if (isEmoticonActive) {
            SystemUtil.showKeyboard(getHostActivity(), contentInputEt);
            layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
            //延迟一段时间，等待输入法完全弹出
            rootViewKrl.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //输入法弹出之后，重新调整
                    layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                    emoticonGv.setVisibility(View.GONE);
                    emoticonIv.setImageResource(R.drawable.con_emoticon_03);
                    isEmoticonActive = false;
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }, 200);

        } else {
            SystemUtil.hideKeyboard(getHostActivity(), contentInputEt);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            emoticonGv.setVisibility(View.VISIBLE);
            emoticonIv.setImageResource(R.drawable.con_character_04);
            isEmoticonActive = true;
            layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
            if (mKeyboardHeight > 0) {
                ViewGroup.LayoutParams params = emoticonGv.getLayoutParams();
                params.height = mKeyboardHeight;
            }
        }
    }

    /**
     * 刷新的同时，计算是否需要继续分页
     */
    private void updateRcv() {
        if (rcvAdapter == null) {
            return;
        }
        boolean isTopPullAvailable = MessageDao.getCount(mChatRoom.getRoomId(),
                mChatRoom.getRoomType()) > mChatMsgList.size() || isNeedGetServerMsg;
        rcvAdapter.setTopPullViewVisible(isTopPullAvailable);
        rcvAdapter.notifyDataSetChanged();
    }

    /**
     * 播放录音
     */
    private void playVoiceRecord(final ChatMsg chatMsg) {
        if (chatMsg == null) {
            return;
        }
        final FileInfo fileInfo;
        if (chatMsg.getReplyMsgInfo() != null && chatMsg.getReplyMsgInfo().getFileInfo() != null && chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
            fileInfo = chatMsg.getReplyMsgInfo().getFileInfo();
        } else if (chatMsg.getFileInfo() != null) {
            fileInfo = chatMsg.getFileInfo();
        } else {
            return;
        }
        String tapePath = "";
        if (FileUtil.isFileExists(fileInfo.getOriginPath())) {
            tapePath = fileInfo.getOriginPath();
        } else if (FileUtil.isFileExists(fileInfo.getSavePath())) {
            tapePath = fileInfo.getSavePath();
        }
        if (TextUtils.isEmpty(tapePath)) {
            return;
        }
        TapeRecordManager.getInstance().playEndOrFail();
        int playingMark = fileInfo.getTapePlayingMark();
        for (ChatMsg msg : mChatMsgList) {
            if (msg.getFileInfo() != null
                    && msg.getFileInfo().getFileType() == FileInfo.TYPE_TAPE_RECORD) {
                msg.getFileInfo().setTapePlayingMark(0);
            }
            if (msg.getReplyMsgInfo() != null
                    && msg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY
                    && msg.getReplyMsgInfo().getFileInfo() != null
                    && msg.getReplyMsgInfo().getFileInfo().getFileType() == FileInfo.TYPE_TAPE_RECORD) {
                msg.getReplyMsgInfo().getFileInfo().setTapePlayingMark(0);
            }
        }
        fileInfo.setTapePlayingMark(1 - playingMark);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRcv();
            }
        });
        if (fileInfo.getTapePlayingMark() == 1) {
            TapeRecordManager.getInstance().playRecord(tapePath, new android.media.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mp) {
                    //播放完成
                    fileInfo.setTapePlayingMark(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateRcv();
                        }
                    });
                    TapeRecordManager.getInstance().playEndOrFail();
                }
            }, new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //播放完成
                    fileInfo.setTapePlayingMark(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateRcv();
                        }
                    });
                    TapeRecordManager.getInstance().playEndOrFail();
                    return false;
                }
            });
        }
    }

    private void downloadFile(final ChatMsg chatMsg, final String saveDir) {
        if (chatMsg == null) {
            return;
        }
        final FileInfo fileInfo;
        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY
                && chatMsg.getReplyMsgInfo() != null
                && chatMsg.getReplyMsgInfo().getFileInfo() != null) {
            fileInfo = chatMsg.getReplyMsgInfo().getFileInfo();
        } else if (chatMsg.getFileInfo() != null) {
            fileInfo = chatMsg.getFileInfo();
        } else {
            return;
        }
        fileInfo.setDownloadState(FileInfo.TYPE_DOWNLOADING);
        updateRcv();
        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
            MessageDao.updateReplyFileInfo(chatMsg.getLocalMsgId(), fileInfo);
        } else {
            MessageDao.updateFileInfo(chatMsg.getLocalMsgId(), fileInfo);
        }
        HttpUtil.downloadFile(fileInfo.getDownloadPath(), saveDir,
                fileInfo.getFileName(), new HttpUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        fileInfo.setSavePath(saveDir + fileInfo.getFileName());
                        fileInfo.setDownloadState(FileInfo.TYPE_DOWNLOAD_SUCCESS);

                        if (fileInfo.getFileType() == FileInfo.TYPE_DOC
                                || fileInfo.getFileType() == FileInfo.TYPE_MUSIC) {
                            SystemUtil.notifyMediaStoreRefresh(getAppContext(), fileInfo.getSavePath());
                            FileOpenUtil.openFile(getHostActivity(), fileInfo.getSavePath());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateRcv();
                                }
                            });

                        } else if (fileInfo.getFileType() == FileInfo.TYPE_TAPE_RECORD) {
                            playVoiceRecord(chatMsg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateRcv();
                                    if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
                                        setMessageRead(chatMsg.getMsgId(), ChatMsg.TYPE_READ_NORMAL, false);
                                    } else {
                                        setMessageRead(chatMsg.getMsgId(), ChatMsg.TYPE_READ_TAPE, false);
                                    }
                                }
                            });
                        }
                        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
                            MessageDao.updateReplyFileInfo(chatMsg.getLocalMsgId(), fileInfo);
                        } else {
                            MessageDao.updateFileInfo(chatMsg.getLocalMsgId(), fileInfo);
                        }
                    }

                    @Override
                    public void onDownloading(int progress) {
                        LogUtil.i("progress", progress);
                    }

                    @Override
                    public void onDownloadFailed() {
                        LogUtil.i("downloadFile Failure");
                        fileInfo.setDownloadState(FileInfo.TYPE_DOWNLOAD_FAIL);
                        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
                            MessageDao.updateReplyFileInfo(chatMsg.getLocalMsgId(), fileInfo);
                        } else {
                            MessageDao.updateFileInfo(chatMsg.getLocalMsgId(), fileInfo);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateRcv();
                            }
                        });
                    }
                });
    }


    private void compressImg(final String filePath) {
        ImgUtil.compress(getAppContext(), filePath, new LubanCallBack() {
            @Override
            public void onSuccess(File outputFile) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(outputFile.getName());
                fileInfo.setFileType(FileInfo.TYPE_IMG);
                fileInfo.setFileLength(outputFile.length());
                fileInfo.setOriginPath(filePath);
                fileInfo.setCompressPath(outputFile.getAbsolutePath());
                ChatRoomManager.uploadFile(getAppContext(), mChatRoom, fileInfo);
            }
        });
    }

    private void showRightMorePop() {
        initRightMorePop();
        if (rightMorePop != null) {
            rightMorePop.showAsDropDown(titleLayoutRl, 0, 0);
        }
        rightExpandIv.setImageResource(R.drawable.con_revenue_right_black);
    }

    private void initRightMorePop() {
        View popView = View.inflate(getAppContext(), R.layout.pop_conversation_chat_right_more, null);
        GridView containerGv = popView.findViewById(R.id.gv_conversation_chat_right_more);
        final List<ImgUrl> imgList = new ArrayList<>();
        if (mChatRoom.getBlackMark() == 0) {
//            imgList.add(new ImgUrl("search", getString(R.string.search), R.drawable.search_01));
            if (mChatRoom.getTopMark() == 0) {
                imgList.add(new ImgUrl("makeTop", getString(R.string.make_top), R.drawable.topping_02));
            } else {
                imgList.add(new ImgUrl("makeTop", getString(R.string.cancel_make_top),
                        R.drawable.topping_02));
            }
            imgList.add(new ImgUrl("clearConversation", getString(R.string.clear_conversation),
                    R.drawable.empty_03));
            if (mChatRoom.getShieldMark() == 0) {
                imgList.add(new ImgUrl("shieldConversation", getString(R.string.shield_conversation),
                        R.drawable.shield_04));
            } else {
                imgList.add(new ImgUrl("shieldConversation", getString(R.string.cancel_shield_conversation),
                        R.drawable.shield_04));
            }
        }
        imgList.add(new ImgUrl("checkInfo", getString(R.string.check_info), R.drawable.data_friend_05));
        if ("group".equals(mChatRoom.getRoomType()) && mChatRoom.getInviteMark() == 1) {
            imgList.add(new ImgUrl("inviteJoin", getString(R.string.invite_friend_join_group), R.drawable.con_invite_friend));
        }
        ChatRoomRightMoreGvAdapter gvAdapter = new ChatRoomRightMoreGvAdapter(
                getHostActivity(), imgList);
        containerGv.setAdapter(gvAdapter);
        containerGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rightMorePop.dismiss();
                switch (imgList.get(position).getId()) {
                    case "search":
                        searchContainerFl.setVisibility(View.VISIBLE);
                        titleLayoutRl.setVisibility(View.INVISIBLE);
                        break;

                    case "makeTop":
                        int topMark = mChatRoom.getTopMark();
                        if (ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                            makeTopFriend(mChatRoom.getRoomId(), 1 - topMark);

                        } else if ("group".equals(mChatRoom.getRoomType())) {
                            makeTopGroup(mChatRoom.getRoomId(), 1 - topMark);
                        }
                        break;

                    case "clearConversation":
                        clearConversation(mChatRoom.getRoomId(), mChatRoom.getRoomType());
                        break;

                    case "shieldConversation":
                        int shieldMark = mChatRoom.getShieldMark();
                        if (ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                            shieldFriendConversation(mChatRoom.getRoomId(), 1 - shieldMark);
                        } else if (ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                            shieldGroupConversation(mChatRoom.getRoomId(), 1 - shieldMark);
                        }
                        break;

                    case "checkInfo":
                        if (mChatRoom != null && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                            Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
                            intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_ID, mChatRoom.getRoomId());
                            startActivity(intent);

                        } else if (mChatRoom != null) {
                            Intent intent = new Intent(getApplicationContext(), FriendInfoActivity.class);
                            intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, mChatRoom);
                            startActivity(intent);
                        }
                        break;

                    case "inviteJoin":
                        if (mChatRoom != null && "group".equals(mChatRoom.getRoomType())) {
                            getGroupInfo(mChatRoom.getRoomId());
                        }
                        break;

                    default:
                        break;
                }
            }
        });
        rightMorePop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rightMorePop.setFocusable(true);
        rightMorePop.setBackgroundDrawable(new BitmapDrawable());
        rightMorePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rightExpandIv.setImageResource(R.drawable.con_drop_right_black);
            }
        });
    }

    /**
     * 解析接口返回的消息记录
     */
    private void processServerMsgList(List<ChatMsg> backMsgList, int firstMark) {
        if (backMsgList == null || backMsgList.isEmpty()) {
            //如果是获取第一页数据，未获取到服务器数据则只加载本地数据
            if (firstMark == 1) {
                List<ChatMsg> cacheList = MessageDao.getMessageBeforeList(mChatRoom.getRoomId(),
                        mChatRoom.getRoomType(), TimeUtil.getServerTimestamp());
                mChatMsgList.clear();
                mChatMsgList.addAll(cacheList);
                if (rcvAdapter.getTopPullViewVisible()) {
                    layoutManager.scrollToPositionWithOffset(mChatMsgList.size(), 0);
                } else {
                    layoutManager.scrollToPositionWithOffset(mChatMsgList.size() - 1, 0);
                }
            }
            MessageDao.removeUnSyncMsgId(mChatRoom, MessageDao.getMinMsgId(mChatRoom));
            isNeedGetServerMsg = false;
            updateRcv();
            return;
        }
        for (ChatMsg msg : backMsgList) {
            msg.setRoomId(mChatRoom.getRoomId());
            msg.setRoomType(mChatRoom.getRoomType());
            msg.setSendState(ChatMsg.TYPE_SEND_SUCCESS);
            if (ChatMsg.TYPE_CONTENT_TEXT == msg.getContentType()
                    || ChatMsg.TYPE_CONTENT_AT == msg.getContentType()
                    || ChatMsg.TYPE_CONTENT_REPLY == msg.getContentType()) {
                if (EmoticonUtil.isContainEmotion(msg.getContent())) {
                    msg.setEmoMark(1);
                }
                if (HyperLinkUtil.isContainPhone(msg.getContent())) {
                    msg.setPhoneMark(1);
                }
                if (HyperLinkUtil.isContainUrl(msg.getContent())) {
                    msg.setUrlMark(1);
                }
            }
        }
        //避免获取的msgid为空，发送中和发送失败的都为空，msgId之后成功后的消息才会有
        for (int i=backMsgList.size()-1;i>=0;i--){
            if (backMsgList.get(i).getMsgId()!=null){
                pageEarliestMsgId =backMsgList.get(i).getMsgId() ;
                break;
            }
        }

        String maxUnSyncMsgId = MessageDao.getMaxUnSyncMsgId(mChatRoom);

        if (!TextUtils.isEmpty(maxUnSyncMsgId) && !"0".equals(maxUnSyncMsgId)) {
            //如果当前页最小一条消息id > 最大未同步消息id, 则需继续从服务器获取数据，
            if (Long.parseLong(pageEarliestMsgId) > Long.parseLong(maxUnSyncMsgId)) {
                isNeedGetServerMsg = true;
                backMsgList.get(backMsgList.size() - 1).setUnSyncMsgFirstId(maxUnSyncMsgId);
            } else {
                //如果当前页最小一条消息id <= 最大未同步消息id，则移除所有比pageEarliestMsgId大的未同步消息id
                //并且重新获取最大未同步消息id，重新判断
                MessageDao.removeUnSyncMsgId(mChatRoom, pageEarliestMsgId);
                maxUnSyncMsgId = MessageDao.getMaxUnSyncMsgId(mChatRoom);
                //如果这里还能拿到最大未同步消息id，说明一定比当前pageEarliestMsgId小，则仍需继续向服务器请求。
                if (!TextUtils.isEmpty(maxUnSyncMsgId) && !"0".equals(maxUnSyncMsgId)) {
                    isNeedGetServerMsg = true;
                    backMsgList.get(backMsgList.size() - 1).setUnSyncMsgFirstId(maxUnSyncMsgId);
                } else {
                    isNeedGetServerMsg = false;
                }
            }
        } else {
            isNeedGetServerMsg = false;
        }
        backMsgList = MessageDao.addMessageList(backMsgList);
        Collections.sort(backMsgList, new ChatMsg.TimeRiseComparator());

        mChatMsgList.clear();
        mChatMsgList.addAll(MessageDao.getMessageBeforeListAll(mChatRoom.getRoomId(), mChatRoom.getRoomType(), backMsgList.get(backMsgList.size() - 1).getCreateTimestamp()));
//        //遍历删除,除去已被本地删除的
//        Iterator<ChatMsg> iterator = backMsgList.iterator();
//        while (iterator.hasNext()) {
//            ChatMsg cm = iterator.next();
//            if (cm.getDeleteMark() == 1) {
//                iterator.remove();//使用迭代器的删除方法删除
//            }
//        }
//        //去重
//        Set<ChatMsg> msgSet = new HashSet<>();
//        msgSet.addAll(mChatMsgList);
//        msgSet.addAll(backMsgList);
//        mChatMsgList.clear();
//        mChatMsgList.addAll(msgSet);
//        Collections.sort(mChatMsgList, new ChatMsg.TimeRiseComparator());
        updateRcv();
        if (firstMark == 1) {
            layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
            if (backMsgList.size() > 0) {
                String lastId = backMsgList.get(0).getMsgId();
                setMessageRead(lastId, ChatMsg.TYPE_READ_NORMAL, true);
            }
        } else {
            if (rcvAdapter.getTopPullViewVisible()) {
                layoutManager.scrollToPositionWithOffset(backMsgList.size() + 1, topLoadLastOffset);
            } else {
                layoutManager.scrollToPositionWithOffset(backMsgList.size(), topLoadLastOffset);
            }
        }
        oldServerMsgDelete();
    }


    private void oldServerMsgDelete() {
        if (mChatMsgList.size() <= 0) {
            return;
        }
        if (TextUtils.isEmpty(mNotesMsgId)) {
            //避免获取的msgid为空，发送中和发送失败的都为空，msgId之后成功后的消息才会有
            for (int i=mChatMsgList.size()-1;i>=0;i--){
                if (mChatMsgList.get(i).getMsgId()!=null){
                    mNotesMsgId =mChatMsgList.get(i).getMsgId() ;
                    break;
                }
            }
        }
        //获取最老的id
        if (mChatMsgList.get(0) == null) {
            return;
        }
        minMsgId = mChatMsgList.get(0).getMsgId();
        if (minMsgId == null || minMsgId.equals(mNotesMsgId)) {
            return;
        }
        HttpMessage.getDeleteMessageList(mChatRoom.getRoomId(), mChatRoom.getRoomType(), mNotesMsgId, minMsgId, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                mNotesMsgId = minMsgId;
                if (data == null) {
                    return;
                }
                try {
                    List<String> msgIdList = JSON.parseArray(data.toString(), String.class);
                    if (msgIdList == null || msgIdList.size() <= 0) {
                        return;
                    }
                    MessageDao.deleteList(msgIdList);
                    for (String msgId : msgIdList) {
                        Iterator<ChatMsg> iterator = mChatMsgList.iterator();
                        while (iterator.hasNext()) {
                            ChatMsg chatMsg1 = iterator.next();
                            if (chatMsg1.getMsgId().equals(msgId)) {
                                iterator.remove();//使用迭代器的删除方法删除
                                break;
                            }
                        }
                    }
                    updateRcv();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取群公告进行弹窗
     */
    private void getNewAnnouncement() {
        if (ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
            HttpContact.getNewAnnouncement(mChatRoom.getRoomId(), new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                @Override
                public void success(Object data, int count) {
                    if (data == null) {
                        return;
                    }
                    Announcement announcement = JSON.parseObject(data.toString(), Announcement.class);
                    if (ActivityUtil.isActivityOnTop(ChatRoomActivity.this)) {
                        announcementDialog(announcement);
                    }
                }
            });
        }
    }

    /**
     * 设置消息已读
     */
    private void setMessageRead(String msgId, int readType, final boolean isNeedRefresh) {
        if (msgId==null){
            //这里说明最新一条消息是本地的失败消息或者发送中的消息
            return;
        }
        HttpMessage.setMessageRead(mChatRoom.getRoomId(), mChatRoom.getRoomType(), msgId, readType,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        LogUtil.i("setMessageRead success");
                        if (isNeedRefresh) {
                            EventTrans.post(EventMsg.CONVERSATION_UNREAD_NUM_REFRESH,
                                    mChatRoom.getRoomId(), mChatRoom.getRoomType());
                        }
                    }
                });
    }

    /**
     * 获取服务器消息列表
     */
    private void getServerMsgList(final int firstMark, String msgId) {
        HttpMessage.getServerMessageList(mChatRoom.getRoomId(), mChatRoom.getRoomType(), firstMark,
                MessageDao.getPageSize(), msgId, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            List<ChatMsg> backMsgList = JSON.parseArray(data.toString(), ChatMsg.class);
                            processServerMsgList(backMsgList, firstMark);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void makeTopFriend(final String friendId, final int topMark) {
        HttpContact.makeTopFriend(friendId, topMark, FriendDao.findFriendshipMark(friendId), new HttpCallBack(this, ClassUtil.classMethodName(), true) {
            @Override
            public void success(Object data, int count) {
                if (mChatRoom != null) {
                    mChatRoom.setTopMark(topMark);
                }
                ChatRoomDao.updateTopMark(friendId, ChatRoom.TYPE_SINGLE, topMark);
                FriendDao.updateTopMark(friendId, topMark);
                ContactUserDao.updateTopMark(friendId, topMark);
                EventTrans.post(EventMsg.CONTACT_FRIEND_MAKE_TOP_REFRESH, friendId, topMark);
            }
        });
    }

    private void shieldFriendConversation(final String friendId, final int shieldMark) {
        HttpContact.shieldFriendConversation(friendId, shieldMark, FriendDao.findFriendshipMark(friendId), new HttpCallBack(this, ClassUtil.classMethodName(), true) {
            @Override
            public void success(Object data, int count) {
                if (mChatRoom != null) {
                    mChatRoom.setShieldMark(shieldMark);
                }
                ChatRoomDao.updateShieldMark(friendId, ChatRoom.TYPE_SINGLE, shieldMark);
                FriendDao.updateShieldMark(friendId, shieldMark);
                ContactUserDao.updateShieldMark(friendId, shieldMark);
                EventTrans.post(EventMsg.CONTACT_FRIEND_SHIELD_REFRESH, friendId, shieldMark);
            }
        });
    }

    private void makeTopGroup(final String groupId, final int topMark) {
        HttpContact.makeTopGroup(groupId, topMark, new HttpCallBack(this, ClassUtil.classMethodName(), true) {
            @Override
            public void success(Object data, int count) {
                if (mChatRoom != null) {
                    mChatRoom.setTopMark(topMark);
                }
                ChatRoomDao.updateTopMark(groupId, ChatRoom.TYPE_GROUP, topMark);
                GroupDao.updateTopMark(groupId, topMark);
                EventTrans.post(EventMsg.CONTACT_GROUP_MAKE_TOP_REFRESH, groupId, topMark);
            }
        });
    }

    private void shieldGroupConversation(final String groupId, final int shieldMark) {
        HttpContact.shieldGroupConversation(groupId, shieldMark, new HttpCallBack(this, ClassUtil.classMethodName(), true) {
            @Override
            public void success(Object data, int count) {
                if (mChatRoom != null) {
                    mChatRoom.setShieldMark(shieldMark);
                }
                ChatRoomDao.updateShieldMark(groupId, ChatRoom.TYPE_GROUP, shieldMark);
                GroupDao.updateShieldMark(groupId, shieldMark);
                EventTrans.post(EventMsg.CONTACT_GROUP_SHIELD_REFRESH, groupId, shieldMark);
            }
        });
    }

    private void clearConversation(final String roomId, final String roomType) {
        HttpMessage.deleteSingleConversation(roomId, roomType,
                new HttpCallBack(this, ClassUtil.classMethodName(), true) {
                    @Override
                    public void success(Object data, int count) {
                        ChatRoomDao.clearConversation(roomId, roomType);
                        MessageDao.deleteRoomMsg(roomId, roomType);
                        EventTrans.post(EventMsg.CONVERSATION_CLEAR_REFRESH, roomId);
                    }
                });
    }

    private void applyAddFriend(String friendId, String verifyInfo, String subgroupId) {
        HttpContact.applyAddFriend(friendId, verifyInfo, subgroupId, SpCons.getUser(getAppContext()).getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName(), true) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            ToastUtil.showShort(getAppContext(), getString(R.string.apply_add_friend_sent));
                        } else {
                            ToastUtil.showShort(getAppContext(), getString(R.string.add_to_friend));
                        }
                        startTemporaryGoneAnim();
                    }
                });
    }

    private void getGroupInfo(final String groupId) {
        HttpContact.getGroupInfo(groupId,
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            GroupInfo dataGroup = JSON.parseObject(data.toString(), GroupInfo.class);
                            if (dataGroup.getInviteFlag() == 0) {
                                ToastUtil.showShort(getAppContext(), R.string.not_allow_invite_prompt);
                            } else {
                                Intent intent = new Intent(getAppContext(), GroupInviteMemberActivity.class);
                                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_ID, groupId);
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void pullBlackFriend(final String friendId, int friendshipMark) {
        HttpContact.pullBlackFriend(friendId, 1, friendshipMark,
                new HttpCallBack(this, ClassUtil.classMethodName(), true) {
                    @Override
                    public void success(Object data, int count) {
                        FriendInfo mFriendInfo = ContactUserDao.getContactUser(friendId);
                        if (mFriendInfo != null) {
                            mFriendInfo.setBlackMark(1);
                            mFriendInfo.setTopMark(0);
                            if (mFriendInfo.getBlackMark() == 1) {
                                ChatRoomDao.deleteRoom(friendId, ChatRoom.TYPE_SINGLE);
                                MessageDao.deleteRoomMsg(friendId, ChatRoom.TYPE_SINGLE);
                            }
                            ContactUserDao.addContactUser(mFriendInfo);
                            EventTrans.post(EventMsg.CONTACT_FRIEND_BLACK_REFRESH, friendId);
                        }
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int[] locationArr = new int[2];
            contentInputEt.getLocationInWindow(locationArr);
            if (ev.getY() < locationArr[1]) {
                emoticonGv.setVisibility(View.GONE);
                SystemUtil.hideKeyboard(getHostActivity(), contentInputEt);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ImgUtil.REQUEST_ALBUM) {
                List<String> dataList = data.getStringArrayListExtra(ImgUtil.IMG_SELECT_PATH_LIST);
                if (dataList == null || dataList.isEmpty()) {
                    return;
                }
                for (String path : dataList) {
                    compressImg(path);
                }
            } else if (requestCode == ImgUtil.REQUEST_CAMERA) {
                compressImg(ImgUtil.cameraOutputPath);
            }
        }
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.SOCKET_ON_MESSAGE:
                ChatMsg chatMsg = (ChatMsg) msg.getData();
                LogUtil.i("SOCKET_ON_MESSAGE", chatMsg);
                if (chatMsg != null && chatMsg.getRoomId().equals(mChatRoom.getRoomId())
                        && chatMsg.getRoomType().equals(mChatRoom.getRoomType())) {
                    chatMsg = MessageDao.getMessage(chatMsg.getMsgId(), chatMsg.getLocalMsgId());

                    boolean removeMark = false;
                    //遍历删除,除去重复消息
                    Iterator<ChatMsg> iterator = mChatMsgList.iterator();
                    while (iterator.hasNext()) {
                        ChatMsg cm = iterator.next();
                        if (cm.getLocalMsgId().equals(chatMsg.getLocalMsgId())) {
                            iterator.remove();//使用迭代器的删除方法删除
                            removeMark = true;
                        }
                    }
                    mChatMsgList.add(chatMsg);
                    if (removeMark) {
                        rcvAdapter.notifyDataSetChanged();
                    } else {
                        //这里+1是由于适配器重写固定增加底部加载，所以获取到的少一个
                        rcvAdapter.notifyItemInserted(mChatMsgList.indexOf(chatMsg) + 1);
                    }
                    if (containerRcv.canScrollVertically(1) && UserDao.user.getId().equals(chatMsg.getSenderId())) {
                        layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                    } else if (!containerRcv.canScrollVertically(1)) {
                        layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                    }
                    boolean isTopPullAvailable = MessageDao.getCount(mChatRoom.getRoomId(),
                            mChatRoom.getRoomType()) > mChatMsgList.size() || isNeedGetServerMsg;
                    rcvAdapter.setTopPullViewVisible(isTopPullAvailable);
                    //收到的所有消息，反馈给服务器消息已读
                    if (chatMsg.getRoomType().equals(mChatRoom.getRoomType())
                            && chatMsg.getRoomId().equals(mChatRoom.getRoomId())
                            && !TextUtils.isEmpty(chatMsg.getMsgId())
                            && (!chatMsg.getSenderId().equals(SpCons.getUser(getAppContext()).getId())
                            || (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                            && chatMsg.getVoiceCall() != null
                            && !chatMsg.getVoiceCall().getInviteUserId()
                            .equals(SpCons.getUser(getAppContext()).getId())))) {
                        setMessageRead(chatMsg.getMsgId(), ChatMsg.TYPE_READ_NORMAL, false);
                        if (ChatRoomDao.getAtType(mChatRoom.getRoomId(), mChatRoom.getRoomType()) != ChatMsg.TYPE_AT_NORMAL) {
                            EventTrans.post(EventMsg.CONVERSATION_REFRESH_TYPE_AT_NORMAL, chatMsg);
                        }
                    }
                }
                break;

            case EventMsg.CONVERSATION_CHAT_REFRESH:
                LogUtil.i("CONVERSATION_CHAT_REFRESH");
                for (int i = 0; i < mChatMsgList.size(); i++) {
                    ChatMsg chatMsg1 = mChatMsgList.get(i);
                    if ((chatMsg1.getLocalMsgId() != null && chatMsg1.getLocalMsgId().equals(msg.getData()))
                            || chatMsg1.getMsgId() != null && chatMsg1.getMsgId().equals(msg.getSecondData())) {
                        ChatMsg message = MessageDao.getMessage(chatMsg1.getMsgId(), chatMsg1.getLocalMsgId());
                        if (message == null) {
                            break;
                        }
                        chatMsg1.setSendState(message.getSendState());
                        chatMsg1.setMsgId(message.getMsgId());
                        chatMsg1.setFileInfo(message.getFileInfo());
                        rcvAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;

            case EventMsg.CONVERSATION_CHAT_FINISH:
                this.finish();
                break;

            case EventMsg.CONVERSATION_CLEAR_REFRESH:
                mChatMsgList.clear();
                updateRcv();
                break;

            case EventMsg.CONVERSATION_DELETE_ROOM:
            case EventMsg.CONVERSATION_DELETE_ALL_ROOM:
                getHostActivity().finish();
                break;

            case EventMsg.CONTACT_FRIEND_NOTE_REFRESH:
                String friendId6 = msg.getData().toString();
                if (!TextUtils.isEmpty(friendId6) && friendId6.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    FriendInfo friendInfo6 = FriendDao.getFriendInfo(friendId6);
                    if (friendInfo6 == null) {
                        friendInfo6 = ContactUserDao.getContactUser(friendId6);
                    }
                    if (friendInfo6 == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(friendInfo6.getFriendNote())) {
                        centerTitleTv.setText(friendInfo6.getFriendNote());
                    } else {
                        centerTitleTv.setText(friendInfo6.getUsername());
                    }
                }
                rcvAdapter.notifyDataSetChanged();
                break;

            case EventMsg.CONTACT_GROUP_INFO_REFRESH:
                GroupInfo groupInfo2 = (GroupInfo) msg.getData();
                if (groupInfo2 == null || TextUtils.isEmpty(groupInfo2.getId())) {
                    return;
                }
                if (groupInfo2.getId().equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())
                        && !TextUtils.isEmpty(groupInfo2.getGroupName())) {
                    centerTitleTv.setText(groupInfo2.getGroupName());
                }
                break;

            case EventMsg.CONTACT_FRIEND_BLACK_REFRESH:
            case EventMsg.CONTACT_FRIEND_DELETE_REFRESH:
                String friendId = msg.getData().toString();
                if (!TextUtils.isEmpty(friendId) && friendId.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_FRIEND_MAKE_TOP_REFRESH:
                String friendId4 = msg.getData().toString();
                int topMark = Integer.parseInt(msg.getSecondData().toString());
                if (!TextUtils.isEmpty(friendId4) && friendId4.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    mChatRoom.setTopMark(topMark);
                }
                break;

            case EventMsg.CONTACT_FRIEND_SHIELD_REFRESH:
                String friendId7 = msg.getData().toString();
                int shieldMark = Integer.parseInt(msg.getSecondData().toString());
                if (!TextUtils.isEmpty(friendId7) && friendId7.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    mChatRoom.setShieldMark(shieldMark);
                }
                break;

            case EventMsg.CONTACT_GROUP_MAKE_TOP_REFRESH:
                String groupId = msg.getData().toString();
                int topMark1 = Integer.parseInt(msg.getSecondData().toString());
                if (!TextUtils.isEmpty(groupId) && groupId.equals(mChatRoom.getRoomId())
                        && "group".equals(mChatRoom.getRoomType())) {
                    mChatRoom.setTopMark(topMark1);
                }
                break;

            case EventMsg.CONTACT_GROUP_SHIELD_REFRESH:
                String groupId3 = msg.getData().toString();
                int shieldMark3 = Integer.parseInt(msg.getSecondData().toString());
                if (!TextUtils.isEmpty(groupId3) && groupId3.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                    mChatRoom.setShieldMark(shieldMark3);
                }
                break;

            case EventMsg.CONTACT_FRIEND_NAME_REFRESH:
                String friendId1 = msg.getData().toString();
                if (friendId1.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    FriendInfo contactUser = ContactUserDao.getContactUser(friendId1);
                    if (contactUser == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(contactUser.getFriendNote())) {
                        centerTitleTv.setText(contactUser.getUsername());
                    } else {
                        centerTitleTv.setText(contactUser.getFriendNote());
                    }
                }
                break;

            case EventMsg.CONTACT_FRIEND_AVATAR_REFRESH:
                String friendId2 = msg.getData().toString();
                if (friendId2.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    updateRcv();

                } else if (ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                    FriendInfo member = GroupMemberDao.getGroupMember(friendId2,
                            mChatRoom.getRoomId());
                    if (member != null) {
                        updateRcv();
                    }
                }
                break;

            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo5 = (GroupInfo) msg.getData();
                FriendInfo receiver = (FriendInfo) msg.getThirdData();
                if (groupInfo5 == null || TextUtils.isEmpty(groupInfo5.getId())
                        || receiver == null || TextUtils.isEmpty(receiver.getId())) {
                    return;
                }
                if (groupInfo5.getId().equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())
                        && receiver.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                    ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_QUIT:
                String groupId2 = msg.getData().toString();
                String friendId3 = msg.getSecondData().toString();
                if (!TextUtils.isEmpty(groupId2) && groupId2.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                    if (!TextUtils.isEmpty(friendId3)
                            && friendId3.equals(SpCons.getUser(getAppContext()).getId())) {
                        getHostActivity().finish();
                    }
                }
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId1) && groupId1.equals(mChatRoom.getRoomId())) {
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_DELETE_BY_OTHER:
                String friendId5 = msg.getData().toString();
                if (!TextUtils.isEmpty(friendId5) && friendId5.equals(mChatRoom.getRoomId())) {
                    ToastUtil.showShort(getHostActivity(), R.string.other_side_deleted_you);
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_PERMISSION_REFRESH:
                GroupInfo groupInfo3 = (GroupInfo) msg.getData();
                if (groupInfo3 == null || TextUtils.isEmpty(groupInfo3.getId())) {
                    return;
                }
                if (groupInfo3.getId().equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())) {
                    int groupSpeakMark = groupInfo3.getGroupSpeak();
                    int inviteMark = groupInfo3.getInviteFlag();
                    if (groupSpeakMark != -1) {
                        mChatRoom.setGroupSpeakMark(groupSpeakMark);
                        setGroupSpeakView();
                    }
                    if (inviteMark != -1) {
                        mChatRoom.setInviteMark(inviteMark);
                    }
                }
                break;

            case EventMsg.CONTACT_GROUP_SET_MANAGER:
                GroupInfo groupInfo4 = (GroupInfo) msg.getData();
                FriendInfo receiver4 = (FriendInfo) msg.getThirdData();
                if (groupInfo4 == null || TextUtils.isEmpty(groupInfo4.getId()) || receiver4 == null
                        || TextUtils.isEmpty(receiver4.getId())) {
                    return;
                }
                if (groupInfo4.getId().equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType())
                        && receiver4.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                    mChatRoom.setGroupGrade(groupInfo4.getGrade());
                    setGroupSpeakView();
                }
                break;

            case EventMsg.CONTACT_FRIEND_AGREE_REFRESH:
                String friend_Id = msg.getData().toString();
                if (!TextUtils.isEmpty(friend_Id) && friend_Id.equals(mChatRoom.getRoomId())
                        && ChatRoom.TYPE_SINGLE.equals(mChatRoom.getRoomType())) {
                    setRoomTypeView();
                }
                break;

            //socket断开重连后，通过同步消息列表获取离线消息
            case EventMsg.CONVERSATION_SYNC_SERVER_LIST:
                List<ChatRoom> updateRoomList = (List<ChatRoom>) msg.getData();
                if (updateRoomList != null && !updateRoomList.isEmpty()) {
                    for (ChatRoom room : updateRoomList) {
                        if (room.getRoomId().equals(mChatRoom.getRoomId())
                                && room.getRoomType().equals(mChatRoom.getRoomType())) {
                            initMsgDataList();
                            break;
                        }
                    }
                }
                break;
            //删除消息
            case EventMsg.CONVERSATION_DELETE_MSG:
                ChatMsg chat = (ChatMsg) msg.getData();
                if (mChatRoom.getRoomType().equals(chat.getRoomType()) && mChatRoom.getRoomId().equals(chat.getRoomId())) {
                    for (ChatMsg chatMsg1 : mChatMsgList) {
                        if (chat.getMsgId().equals(chatMsg1.getMsgId())) {
                            mChatMsgList.remove(chatMsg1);
                            rcvAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                }
                break;
            case EventMsg.CONVERSATION_ADD_GROUP_POST:
                //发布公告
                if (ChatRoom.TYPE_GROUP.equals(mChatRoom.getRoomType()) && mChatRoom.getRoomId().equals(msg.getData())) {
                    getNewAnnouncement();
                }
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (rootViewKrl.isKeyboardActive()) {
                SystemUtil.hideKeyboard(this);
                return true;
            } else if (isEmoticonActive) {
                emoticonGv.setVisibility(View.GONE);
                isEmoticonActive = false;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        if (rightMorePop != null) {
            rightMorePop.dismiss();
        }
        TapeRecordManager.getInstance().playEndOrFail();
        ChatRoomManager.clearRoomInfo();

        super.onDestroy();
    }

    @Override
    public void reply(ChatMsg chatMsg) {
        if (replyMsgInfo == null) {
            replyMsgInfo = new ReplyMsgInfo();
        }
        FriendInfo friendInfo = ContactUserDao.getContactUser(chatMsg.getSenderId());
        if (friendInfo == null) {
            friendInfo = new FriendInfo();
            friendInfo.setId(chatMsg.getSenderId());
        }
        replyMsgInfo.setSenderInfo(friendInfo);
        replyMsgInfo.setFileInfo(chatMsg.getFileInfo());
        replyMsgInfo.setContentType(chatMsg.getContentType());
        replyMsgInfo.setContent(chatMsg.getContent());
        replyView();
    }

    private void replyView() {
        if (replyMsgInfo.getContentType() == ChatMsg.TYPE_CONTENT_TEXT
                || replyMsgInfo.getContentType() == ChatMsg.TYPE_CONTENT_AT
                || replyMsgInfo.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
            replyIv.setVisibility(View.GONE);
            replyContentTv.setText(replyMsgInfo.getContent());
        } else {
            FileInfo fileInfo = replyMsgInfo.getFileInfo();
            replyIv.setVisibility(View.VISIBLE);
            replyVideoIv.setVisibility(View.GONE);
            if (fileInfo != null) {
                String filePath = "";
                switch (fileInfo.getFileType()) {
                    case FileInfo.TYPE_IMG:
                        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
                            filePath = fileInfo.getOriginPath();
                        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
                            filePath = fileInfo.getSavePath();
                        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
                            filePath = fileInfo.getDownloadPath();
                        }
                        ImgUtil.load(this, filePath, replyIv, R.drawable.con_img_vertical);
                        replyContentTv.setText(getString(R.string.image));
                        break;
                    case FileInfo.TYPE_VIDEO:
                        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
                            filePath = fileInfo.getOriginPath();
                        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
                            filePath = fileInfo.getSavePath();
                        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
                            filePath = fileInfo.getDownloadPath();
                        }
                        ImgUtil.load(this, filePath, replyIv, R.drawable.con_img_vertical);
                        replyVideoIv.setVisibility(View.VISIBLE);
                        replyContentTv.setText(getString(R.string.video));
                        break;
                    case FileInfo.TYPE_DOC:
                        String nameStr = fileInfo.getFileName();
                        String type = nameStr.substring(nameStr.lastIndexOf("."), nameStr.length());
                        FileUtil.imageTypeView(type, replyIv);
                        replyContentTv.setText(getString(R.string.document));
                        break;
                    case FileInfo.TYPE_MUSIC:
                        String nameStr1 = fileInfo.getFileName();
                        String type1 = nameStr1.substring(nameStr1.lastIndexOf("."), nameStr1.length());
                        FileUtil.imageTypeView(type1, replyIv);
                        replyContentTv.setText(getString(R.string.musicInfo));
                        break;
                    case FileInfo.TYPE_TAPE_RECORD:
                        replyIv.setVisibility(View.GONE);
                        replyContentTv.setText(getString(R.string.audio));
                        break;
                    default:
                        break;
                }
            }
        }
        replyNameTv.setText(NameUtil.getName(replyMsgInfo.getSenderInfo().getId()));
        replyRl.setVisibility(View.VISIBLE);
    }
}
