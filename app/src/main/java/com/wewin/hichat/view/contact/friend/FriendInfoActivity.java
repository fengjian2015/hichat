package com.wewin.hichat.view.contact.friend;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.datamanager.DataCache;
import com.wewin.hichat.androidlib.datamanager.SpCache;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.utils.ImgUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.utils.TransUtil;
import com.wewin.hichat.component.base.BaseActivity;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.component.dialog.FriendDeleteDialog;
import com.wewin.hichat.component.dialog.PromptInputDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.component.manager.VoiceCallManager;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.model.http.HttpMessage;

import java.util.Map;

/**
 * 好友资料
 * Created by Darren on 2019/1/3.
 */
public class FriendInfoActivity extends BaseActivity {

    private ImageView avatarIv, callIv, chatIv, makeTopIv, shieldIv;
    private TextView nameTv, phoneNumTv, signTv, nicknameTv, sexTv, clearConversationTv,
            blacklistTv, deleteFriendTv, friendNoteTv, addFriendTv;
    private FrameLayout moveGroupingFl, makeTopFl, shieldFl;
    private FriendInfo mFriendInfo;
    private FriendDeleteDialog friendDeleteDialog;
    private PromptInputDialog promptInputDialog;
    private ChatRoom mChatRoom;
    private boolean isInit = true;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_friend_info;
    }

    @Override
    protected void getIntentData() {
        mChatRoom = (ChatRoom) getIntent().getSerializableExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM);
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.civ_contact_friend_info_avatar);
        callIv = findViewById(R.id.iv_contact_friend_info_call);
        chatIv = findViewById(R.id.iv_contact_friend_info_chat);
        nameTv = findViewById(R.id.tv_contact_friend_info_name);
        phoneNumTv = findViewById(R.id.tv_contact_friend_info_phone_num);
        signTv = findViewById(R.id.tv_contact_friend_info_sign);
        nicknameTv = findViewById(R.id.tv_contact_friend_info_nickname);
        sexTv = findViewById(R.id.tv_contact_friend_info_sex);
        clearConversationTv = findViewById(R.id.tv_contact_friend_info_clear_conversation);
        blacklistTv = findViewById(R.id.tv_contact_friend_info_pull_blacklist);
        deleteFriendTv = findViewById(R.id.tv_contact_friend_info_delete_friend);
        friendNoteTv = findViewById(R.id.tv_contact_friend_info_edit_friend_note);
        moveGroupingFl = findViewById(R.id.fl_contact_friend_info_move_grouping);
        makeTopFl = findViewById(R.id.fl_contact_friend_info_make_top);
        shieldFl = findViewById(R.id.fl_contact_friend_info_shield);
        makeTopIv = findViewById(R.id.iv_contact_friend_info_make_top);
        shieldIv = findViewById(R.id.iv_contact_friend_info_shield);
        addFriendTv = findViewById(R.id.tv_contact_friend_info_add_friend);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.friend_info);
        setLeftText(R.string.back);
        LogUtil.i("mChatRoom", mChatRoom);
        mFriendInfo = FriendDao.getFriendInfo(mChatRoom.getRoomId());
        if (mFriendInfo == null) {
            mFriendInfo = ContactUserDao.getContactUser(mChatRoom.getRoomId());
        }
        if (mFriendInfo != null) {
            setViewByData();
        }
        if (mChatRoom != null) {
            getFriendInfo(mChatRoom.getRoomId());
        }
    }


    @Override
    protected void setListener() {
        callIv.setOnClickListener(this);
        chatIv.setOnClickListener(this);
        moveGroupingFl.setOnClickListener(this);
        makeTopFl.setOnClickListener(this);
        shieldFl.setOnClickListener(this);
        clearConversationTv.setOnClickListener(this);
        blacklistTv.setOnClickListener(this);
        deleteFriendTv.setOnClickListener(this);
        friendNoteTv.setOnClickListener(this);
        addFriendTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contact_friend_info_call:
                if (mChatRoom != null) {
                    VoiceCallManager.get().startVoiceCallActivity(getHostActivity(), mChatRoom);
                }
                break;

            case R.id.iv_contact_friend_info_chat:
                if (mFriendInfo != null) {
                    ChatRoomManager.startSingleRoomActivity(getHostActivity(), mFriendInfo.getId());
                }
                break;

            case R.id.fl_contact_friend_info_move_grouping:
                if (mFriendInfo != null) {
                    Intent intent1 = new Intent(getAppContext(), FriendSubgroupActivity.class);
                    intent1.putExtra(ContactCons.EXTRA_CONTACT_FRIEND_INFO, mFriendInfo);
                    startActivity(intent1);
                }
                break;

            case R.id.fl_contact_friend_info_make_top:
                if (mFriendInfo != null) {
                    int topMark = 1 - mFriendInfo.getTopMark();
                    makeTopFriend(mFriendInfo.getId(), topMark, FriendDao.findFriendshipMark(mFriendInfo.getId()));
                }
                break;

            case R.id.fl_contact_friend_info_shield:
                if (mFriendInfo != null) {
                    int shieldMark = 1 - mFriendInfo.getShieldMark();
                    shieldFriendConversation(mFriendInfo.getId(), shieldMark);
                }
                break;

            case R.id.tv_contact_friend_info_clear_conversation:
                if (mFriendInfo != null) {
                    clearConversation(mFriendInfo.getId());
                }
                break;

            case R.id.tv_contact_friend_info_pull_blacklist:
                if (mFriendInfo != null) {
                    int blackMark;
                    if (mFriendInfo.getBlackMark() != 1) {
                        blackMark = 1;
                    } else {
                        blackMark = 0;
                    }
                    pullBlackFriend(mFriendInfo.getId(), blackMark, FriendDao.findFriendshipMark(mChatRoom.getRoomId()));
                }
                break;

            case R.id.tv_contact_friend_info_delete_friend:
                showFriendDeleteDialog();
                break;

            case R.id.tv_contact_friend_info_edit_friend_note:
                String friendNoteStr = friendNoteTv.getText().toString().trim();
                showPromptInputDialog(friendNoteStr);
                break;
            case R.id.tv_contact_friend_info_add_friend:
                DataCache spCache = new SpCache(getAppContext());
                Subgroup friendSubgroup = (Subgroup) spCache.getObject(SpCons.SP_KEY_FRIEND_SUBGROUP);
                applyAddFriend(mFriendInfo.getId(), SpCons.getUser(getAppContext()).getUsername() + getString(R.string.apply_add_friend),
                        friendSubgroup.getId());
                break;

            default:

                break;
        }
    }

    private void setViewByData() {
        if (mFriendInfo == null) {
            return;
        }
        if (!TextUtils.isEmpty(mFriendInfo.getGroupId())) {
            mFriendInfo.setSubgroupId(mFriendInfo.getGroupId());
        }
        if (!TextUtils.isEmpty(mFriendInfo.getGroupName())) {
            mFriendInfo.setSubgroupName(mFriendInfo.getGroupName());
        }
        LogUtil.i("setViewByData", mFriendInfo);
        ImgUtil.load(getHostActivity(), mFriendInfo.getAvatar(), avatarIv);
        String friendNote = mFriendInfo.getFriendNote();
        if (!TextUtils.isEmpty(friendNote)) {
            nameTv.setText(friendNote);
            friendNoteTv.setText(mFriendInfo.getFriendNote());
        } else {
            nameTv.setText(mFriendInfo.getUsername());
        }
        phoneNumTv.setText(mFriendInfo.getPhone());
        signTv.setText(mFriendInfo.getSign());
        nicknameTv.setText(mFriendInfo.getUsername());
        if (mFriendInfo.getGender() == 0) {
            sexTv.setText(R.string.female);
        } else if (mFriendInfo.getGender() == 1) {
            sexTv.setText(R.string.male);
        } else {
            sexTv.setText(R.string.keep_secret);
        }
        makeTopIv.setSelected(TransUtil.intToBoolean(mFriendInfo.getTopMark()));
        shieldIv.setSelected(TransUtil.intToBoolean(mFriendInfo.getShieldMark()));

        if (mFriendInfo.getBlackMark() == 1) {
            blacklistTv.setText(getString(R.string.push_black_list));
            callIv.setVisibility(View.INVISIBLE);
            chatIv.setVisibility(View.INVISIBLE);
            moveGroupingFl.setVisibility(View.GONE);
            makeTopFl.setVisibility(View.GONE);
            shieldFl.setVisibility(View.GONE);
            clearConversationTv.setVisibility(View.GONE);
        } else {
            blacklistTv.setText(getString(R.string.pull_black_list));
            callIv.setVisibility(View.VISIBLE);
            chatIv.setVisibility(View.VISIBLE);
            moveGroupingFl.setVisibility(View.VISIBLE);
            makeTopFl.setVisibility(View.VISIBLE);
            shieldFl.setVisibility(View.VISIBLE);
            clearConversationTv.setVisibility(View.VISIBLE);
        }
        //非好友的情况
        if (mFriendInfo.getFriendship() == 0) {
            addFriendTv.setVisibility(View.VISIBLE);
            deleteFriendTv.setVisibility(View.GONE);
            moveGroupingFl.setVisibility(View.GONE);
            phoneNumTv.setVisibility(View.GONE);
        } else {
            addFriendTv.setVisibility(View.GONE);
            deleteFriendTv.setVisibility(View.VISIBLE);
            moveGroupingFl.setVisibility(View.VISIBLE);
            phoneNumTv.setVisibility(View.VISIBLE);
        }
    }

    private void showFriendDeleteDialog() {
        if (friendDeleteDialog == null) {
            FriendDeleteDialog.FriendDeleteBuilder builder = new FriendDeleteDialog.FriendDeleteBuilder(this);
            friendDeleteDialog = builder.setOnConfirmClickListener(new FriendDeleteDialog.FriendDeleteBuilder.OnDialogConfirmClickListener() {
                @Override
                public void confirmClick() {
                    deleteFriend(SpCons.getUser(getAppContext()).getId(), mFriendInfo.getId());
                }
            }).create();
        }
        friendDeleteDialog.show();
    }

    private void showPromptInputDialog(String inputStr) {
        if (promptInputDialog == null) {
            PromptInputDialog.PromptInputBuilder builder = new PromptInputDialog.PromptInputBuilder(this);
            promptInputDialog = builder.setTitle(R.string.remark_edit)
                    .setInputStr(inputStr)
                    .setMaxInputLength(7)
                    .setOnConfirmListener(new PromptInputDialog.PromptInputBuilder.OnConfirmListener() {
                        @Override
                        public void confirm(String inputStr) {
                            if (!TextUtils.isEmpty(inputStr)) {
                                modifyFriendNote(SpCons.getUser(getAppContext()).getId(), mFriendInfo.getId(), inputStr, FriendDao.findFriendshipMark(mFriendInfo.getId()));
                            }
                        }
                    }).create();
        }
        promptInputDialog.show();
    }

    private void parseBackFriendInfo(FriendInfo backFriend){
        FriendDao.updateFriendInfo(backFriend);
        ContactUserDao.addContactUser(backFriend);
        setViewByData();
        if (isInit) {
            isInit = false;
            if (!mFriendInfo.getAvatar().equals(backFriend.getAvatar())){
                EventTrans.post(EventMsg.CONTACT_FRIEND_AVATAR_REFRESH, backFriend.getId());
            }
            if (!mFriendInfo.getFriendNote().equals(backFriend.getFriendNote())){
                EventTrans.post(EventMsg.CONTACT_FRIEND_NOTE_REFRESH, backFriend.getId());
            }
            if (mFriendInfo.getTopMark() != backFriend.getTopMark()){
                EventTrans.post(EventMsg.CONTACT_FRIEND_MAKE_TOP_REFRESH, backFriend.getId(),
                        mFriendInfo.getTopMark());
            }
            if (mFriendInfo.getShieldMark() != backFriend.getShieldMark()){
                EventTrans.post(EventMsg.CONTACT_FRIEND_SHIELD_REFRESH, backFriend.getId(),
                        mFriendInfo.getShieldMark());
            }
        }
    }

    private void getFriendInfo(final String friendId) {
        HttpContact.getFriendInfo(friendId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            FriendInfo backFriend = JSON.parseObject(data.toString(), FriendInfo.class);
                            LogUtil.i("getFriendInfo", backFriend);
                            parseBackFriendInfo(backFriend);
                            mFriendInfo = backFriend;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void pullBlackFriend(final String friendId, final int blackMark, int friendShipMark) {
        HttpContact.pullBlackFriend(friendId, blackMark, friendShipMark,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (mFriendInfo != null) {
                            mFriendInfo.setBlackMark(blackMark);
                            mFriendInfo.setTopMark(0);
                            setViewByData();
                            if (mFriendInfo.getBlackMark() == 1) {
                                ChatRoomDao.deleteRoom(friendId, ChatRoom.TYPE_SINGLE);
                                MessageDao.updateShowMark(friendId, ChatRoom.TYPE_SINGLE);
                            }
                            EventTrans.post(EventMsg.CONTACT_FRIEND_BLACK_REFRESH, friendId);
                        }
                    }
                });
    }

    private void shieldFriendConversation(final String friendId, final int shieldMark) {
        HttpContact.shieldFriendConversation(friendId, shieldMark, FriendDao.findFriendshipMark(friendId),
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (mFriendInfo == null) {
                            return;
                        }
                        mFriendInfo.setShieldMark(shieldMark);
                        shieldIv.setSelected(TransUtil.intToBoolean(shieldMark));
                        ChatRoomDao.updateShieldMark(friendId, ChatRoom.TYPE_SINGLE, shieldMark);
                        FriendDao.updateShieldMark(friendId, shieldMark);
                        ContactUserDao.updateShieldMark(friendId, shieldMark);
                        EventTrans.post(EventMsg.CONTACT_FRIEND_SHIELD_REFRESH,
                                mFriendInfo.getId(), shieldMark);
                    }
                });
    }

    private void makeTopFriend(final String friendId, final int topMark, int friendShipMark) {
        HttpContact.makeTopFriend(friendId, topMark, friendShipMark,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (mFriendInfo == null) {
                            return;
                        }
                        mFriendInfo.setTopMark(topMark);
                        makeTopIv.setSelected(TransUtil.intToBoolean(mFriendInfo.getTopMark()));
                        ChatRoomDao.updateTopMark(friendId, ChatRoom.TYPE_SINGLE, topMark);
                        FriendDao.updateTopMark(friendId, topMark);
                        ContactUserDao.updateTopMark(friendId, topMark);
                        EventTrans.post(EventMsg.CONTACT_FRIEND_MAKE_TOP_REFRESH, friendId, topMark);
                    }
                });
    }

    private void deleteFriend(String accountId, final String friendId) {
        HttpContact.deleteFriend(accountId, friendId,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ToastUtil.showShort(getAppContext(), R.string.delete_success);
                        ChatRoomDao.deleteRoom(friendId, ChatRoom.TYPE_SINGLE);
                        MessageDao.updateShowMark(friendId, ChatRoom.TYPE_SINGLE);
                        FriendDao.deleteFriend(friendId);
                        EventTrans.post(EventMsg.CONTACT_FRIEND_DELETE_REFRESH, friendId);
                    }
                });
    }

    private void modifyFriendNote(String accountId, final String friendId, final String friendNote, int friendShipMark) {
        HttpContact.modifyFriendNote(accountId, friendId, friendNote, friendShipMark,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        friendNoteTv.setText(friendNote);
                        mFriendInfo.setFriendNote(friendNote);
                        if (!TextUtils.isEmpty(friendNote)) {
                            nameTv.setText(friendNote);
                        } else {
                            nameTv.setText(mFriendInfo.getUsername());
                        }
                        FriendDao.updateNote(mFriendInfo.getId(), friendNote);
                        ContactUserDao.updateNote(mFriendInfo.getId(), friendNote);
                        EventTrans.post(EventMsg.CONTACT_FRIEND_NOTE_REFRESH, mFriendInfo.getId());
                    }
                });
    }

    private void clearConversation(final String friendId) {
        HttpMessage.deleteSingleConversation(friendId, ChatRoom.TYPE_SINGLE,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        ChatRoomDao.clearConversation(friendId, ChatRoom.TYPE_SINGLE);
                        MessageDao.updateShowMark(friendId, ChatRoom.TYPE_SINGLE);
                        EventTrans.post(EventMsg.CONVERSATION_CLEAR_REFRESH, mFriendInfo.getId());
                        ToastUtil.showShort(getAppContext(), R.string.cleared);
                    }
                });
    }

    private void applyAddFriend(String friendId, String verifyInfo, String groupingId) {
        HttpContact.applyAddFriend(friendId, verifyInfo, groupingId, SpCons.getUser(getAppContext()).getId(),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            ToastUtil.showShort(getAppContext(), getString(R.string.apply_add_friend_sent));
                        } else {
                            ToastUtil.showShort(getAppContext(), getString(R.string.add_to_friend));
                        }
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.CONTACT_DELETE_BY_OTHER:
                String friendId = msg.getData().toString();
                if (!TextUtils.isEmpty(friendId) && friendId.equals(mFriendInfo.getId())) {
                    ToastUtil.showShort(getHostActivity(), R.string.other_side_deleted_you);
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_FRIEND_AVATAR_REFRESH:
            case EventMsg.CONTACT_FRIEND_NAME_REFRESH:
            case EventMsg.CONTACT_FRIEND_INFO_REFRESH:
                getFriendInfo(mChatRoom.getRoomId());
                break;

            case EventMsg.CONTACT_FRIEND_DELETE_REFRESH:
                String friendId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(friendId1) && friendId1.equals(mFriendInfo.getId())) {
                    getHostActivity().finish();
                }
            case EventMsg.CONTACT_FRIEND_AGREE_REFRESH:
                String friendId2 = msg.getData().toString();
                if (!TextUtils.isEmpty(friendId2) && friendId2.equals(mFriendInfo.getId())) {
                    getFriendInfo(mChatRoom.getRoomId());
                }
                break;

            default:

                break;
        }
    }

}
