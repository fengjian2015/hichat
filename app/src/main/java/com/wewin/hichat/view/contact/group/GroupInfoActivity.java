package com.wewin.hichat.view.contact.group;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.R;
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
import com.wewin.hichat.component.dialog.PromptDialog;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.model.http.HttpContact;

import java.util.List;

/**
 * 群资料
 * Created by Darren on 2018/12/31.
 */
public class GroupInfoActivity extends BaseActivity {

    private ImageView avatarIv, chatIv, makeTopIv, shieldIv, allowSearchIv, needVerifyIv,
            allowInviteIv, bannedSpeakIv, allowAddIv;
    private TextView nameTv, groupIdTv, descTv, nicknameTv, memberSumTv, clearConversationTv,
            exitGroupTv;
    private FrameLayout inviteNewMemberFl, checkMemberFl, announcementFl, makeTopFl, shieldFl,
            allowSearchFl, needVerifyFl, allowInviteFl, bannedSpeakFl, allowAddFl;
    private LinearLayout permissionContainerLl;
    private GroupInfo mGroupInfo;
    private String quitPromptStr;
    private final int TYPE_PROMPT_QUIT_GROUP = 0;
    private final int TYPE_PROMPT_CLEAR_CONVERSATION = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_group_info;
    }

    @Override
    protected void initViews() {
        avatarIv = findViewById(R.id.civ_contact_group_info_avatar);
        chatIv = findViewById(R.id.iv_contact_group_info_chat);
        nameTv = findViewById(R.id.tv_contact_group_info_name);
        groupIdTv = findViewById(R.id.tv_contact_group_info_group_id);
        descTv = findViewById(R.id.tv_contact_group_info_desc);
        nicknameTv = findViewById(R.id.tv_contact_group_info_nickname);
        memberSumTv = findViewById(R.id.tv_contact_group_info_member_sum);
        clearConversationTv = findViewById(R.id.tv_contact_group_info_clear_conversation);
        exitGroupTv = findViewById(R.id.tv_contact_group_info_exit_group);
        inviteNewMemberFl = findViewById(R.id.fl_contact_group_info_invite_new_member);
        checkMemberFl = findViewById(R.id.fl_contact_group_info_check_group_member);
        announcementFl = findViewById(R.id.fl_contact_group_info_group_announcement);
        makeTopFl = findViewById(R.id.fl_contact_group_info_make_top);
        shieldFl = findViewById(R.id.fl_contact_group_info_shield_group_conversation);
        allowSearchFl = findViewById(R.id.fl_contact_group_info_allow_search);
        needVerifyFl = findViewById(R.id.fl_contact_group_info_need_verify);
        allowInviteFl = findViewById(R.id.fl_contact_group_info_allow_member_invite);
        bannedSpeakFl = findViewById(R.id.fl_contact_group_info_talk_banned);
        makeTopIv = findViewById(R.id.iv_contact_group_info_make_top);
        shieldIv = findViewById(R.id.iv_contact_group_info_shield_group_conversation);
        allowSearchIv = findViewById(R.id.iv_contact_group_info_allow_search);
        needVerifyIv = findViewById(R.id.iv_contact_group_info_need_verify);
        allowInviteIv = findViewById(R.id.iv_contact_group_info_allow_member_invite);
        bannedSpeakIv = findViewById(R.id.iv_contact_group_info_talk_banned);
        permissionContainerLl = findViewById(R.id.ll_contact_group_permission_container);
        allowAddFl = findViewById(R.id.fl_contact_group_info_allow_member_add);
        allowAddIv = findViewById(R.id.iv_contact_group_info_allow_member_add);
    }

    @Override
    protected void initViewsData() {
        setCenterTitle(R.string.group_info);
        setLeftText(R.string.back);
        String groupId = getIntent().getStringExtra(ContactCons.EXTRA_CONTACT_GROUP_ID);
        LogUtil.i("initViewData", groupId);
        if (!TextUtils.isEmpty(groupId)) {
            getGroupInfo(groupId, true);
        }
    }

    @Override
    protected void setListener() {
        chatIv.setOnClickListener(this);
        inviteNewMemberFl.setOnClickListener(this);
        checkMemberFl.setOnClickListener(this);
        announcementFl.setOnClickListener(this);
        makeTopFl.setOnClickListener(this);
        shieldFl.setOnClickListener(this);
        allowSearchFl.setOnClickListener(this);
        needVerifyFl.setOnClickListener(this);
        allowInviteFl.setOnClickListener(this);
        bannedSpeakFl.setOnClickListener(this);
        clearConversationTv.setOnClickListener(this);
        exitGroupTv.setOnClickListener(this);
        allowAddFl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contact_group_info_chat:
                if (mGroupInfo != null) {
                    ChatRoomManager.startGroupRoomActivity(getHostActivity(), mGroupInfo.getId());
                }
                break;

            case R.id.fl_contact_group_info_invite_new_member:
                getGroupInfo(mGroupInfo.getId(), false);
                break;

            case R.id.fl_contact_group_info_check_group_member:
                if (mGroupInfo != null) {
                    Intent intent2 = new Intent(getAppContext(), GroupMemberListActivity.class);
                    intent2.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
                    startActivity(intent2);
                }
                break;

            case R.id.fl_contact_group_info_group_announcement:
                if (mGroupInfo != null) {
                    Intent intent1 = new Intent(getAppContext(), AnnouncementListActivity.class);
                    intent1.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
                    startActivity(intent1);
                }
                break;

            case R.id.fl_contact_group_info_make_top:
                if (mGroupInfo != null) {
                    int topMark = 1 - mGroupInfo.getTopMark();
                    makeTopGroup(mGroupInfo.getId(), topMark);
                }
                break;

            case R.id.fl_contact_group_info_shield_group_conversation:
                if (mGroupInfo != null) {
                    int shieldMark = 1 - mGroupInfo.getShieldMark();
                    LogUtil.i("shieldMark", shieldMark);
                    shieldGroupConversation(mGroupInfo.getId(), shieldMark);
                }
                break;

            case R.id.fl_contact_group_info_allow_search:
                if (mGroupInfo != null) {
                    int allowSearch = 1 - mGroupInfo.getSearchFlag();
                    if (allowSearch == 1) {
                        editGroupPermission(mGroupInfo.getId(), allowSearch, -1, -1, -1,-1);
                    } else if (allowSearch == 0) {
                        editGroupPermission(mGroupInfo.getId(), allowSearch, 0, -1, -1,-1);
                    }
                }
                break;

            case R.id.fl_contact_group_info_need_verify:
                if (mGroupInfo != null && allowSearchIv.isSelected()) {
                    needVerifyIv.setEnabled(true);
                    int needVerify = 1 - mGroupInfo.getGroupValid();
                    editGroupPermission(mGroupInfo.getId(), -1, needVerify, -1, -1,-1);
                } else {
                    needVerifyIv.setEnabled(false);
                }
                break;

            case R.id.fl_contact_group_info_allow_member_invite:
                if (mGroupInfo != null) {
                    int allowInvite = 1 - mGroupInfo.getInviteFlag();
                    editGroupPermission(mGroupInfo.getId(), -1, -1, allowInvite, -1,-1);
                }
                break;

            case R.id.fl_contact_group_info_talk_banned:
                if (mGroupInfo != null) {
                    int bannedTalk = 1 - mGroupInfo.getGroupSpeak();
                    editGroupPermission(mGroupInfo.getId(), -1, -1, -1, bannedTalk,-1);
                }
                break;

            case R.id.tv_contact_group_info_clear_conversation:
                showPromptDialog(TYPE_PROMPT_CLEAR_CONVERSATION,
                        getString(R.string.prompt_confirm_clear_conversation_record));
                break;

            case R.id.tv_contact_group_info_exit_group:
                showPromptDialog(TYPE_PROMPT_QUIT_GROUP, quitPromptStr);
                break;

            case R.id.fl_contact_group_info_allow_member_add:
                if (mGroupInfo != null) {
                    int addFriendMark = 1 - mGroupInfo.getAddFriendMark();
                    editGroupPermission(mGroupInfo.getId(), -1, -1, -1, -1,addFriendMark);

                }
                break;
            default:

                break;
        }
    }

    @Override
    protected void onRightTvClick() {
        if (mGroupInfo != null) {
            Intent intent = new Intent(getAppContext(), GroupInfoEditActivity.class);
            intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_INFO, mGroupInfo);
            startActivity(intent);
        }
    }

    private void showPromptDialog(final int promptType, String promptStr) {
        PromptDialog.PromptBuilder builder = new PromptDialog.PromptBuilder(this);
        PromptDialog promptDialog = builder.setPromptContent(promptStr)
                .setOnConfirmClickListener(new PromptDialog.PromptBuilder.OnConfirmClickListener() {
                    @Override
                    public void confirmClick() {
                        if (mGroupInfo != null && promptType == TYPE_PROMPT_CLEAR_CONVERSATION) {
                            ChatRoomDao.clearConversation(mGroupInfo.getId(), ChatRoom.TYPE_GROUP);
                            MessageDao.updateShowMark(mGroupInfo.getId(), ChatRoom.TYPE_GROUP);
                            EventTrans.post(EventMsg.CONVERSATION_CLEAR_REFRESH, mGroupInfo.getId());

                        } else if (mGroupInfo != null && promptType == TYPE_PROMPT_QUIT_GROUP) {
                            quitDisbandGroup(mGroupInfo.getId());
                        }
                    }
                }).create();
        promptDialog.show();
    }

    private void getGroupMemberList(final String groupId) {
        HttpContact.getGroupMemberList(groupId, -1, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    List<FriendInfo> dataList = JSON.parseArray(data.toString(), FriendInfo.class);
                    LogUtil.i("getGroupMemberList", dataList);
                    ContactUserDao.addContactUserList(dataList);
                    GroupMemberDao.addGroupMemberList(groupId, dataList);
                    if (dataList == null) {
                        memberSumTv.setText("成员" + 0 + "人");
                    } else {
                        memberSumTv.setText("成员" + dataList.size() + "人");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGroupInfo(final String groupId, final boolean needRefreshUI) {
        HttpContact.getGroupInfo(groupId, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (data == null) {
                    return;
                }
                try {
                    mGroupInfo = JSON.parseObject(data.toString(), GroupInfo.class);
                    LogUtil.i("getGroupInfo", mGroupInfo);
                    if (mGroupInfo == null) {
                        return;
                    }
                    setMemberView();
                    setGroupPermissionView(needRefreshUI);
                    getGroupMemberList(groupId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void editGroupPermission(String groupId, final int allowSearch, final int needVerify,
                                     final int allowInvite, final int banSpeak,final int addFriendMark) {
        HttpContact.editGroupPermission(groupId, -1, banSpeak, needVerify, allowInvite, allowSearch,addFriendMark,
                new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (banSpeak != -1) {
                            mGroupInfo.setGroupSpeak(banSpeak);
                        }
                        if (needVerify != -1) {
                            mGroupInfo.setGroupValid(needVerify);
                        }
                        if (allowInvite != -1) {
                            mGroupInfo.setInviteFlag(allowInvite);
                        }
                        if (allowSearch != -1) {
                            mGroupInfo.setSearchFlag(allowSearch);
                        }
                        if(addFriendMark!=-1){
                            mGroupInfo.setAddFriendMark(addFriendMark);
                        }
                        GroupDao.updatePermission(mGroupInfo);
                        EventTrans.post(EventMsg.CONTACT_GROUP_PERMISSION_REFRESH, mGroupInfo);
                        setGroupPermissionView(true);
                    }
                });
    }

    private void makeTopGroup(final String groupId, final int topMark) {
        HttpContact.makeTopGroup(groupId, topMark, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (mGroupInfo != null) {
                    mGroupInfo.setTopMark(topMark);
                    makeTopIv.setSelected(TransUtil.intToBoolean(topMark));
                    ChatRoomDao.updateTopMark(groupId, ChatRoom.TYPE_GROUP, topMark);
                    GroupDao.updateTopMark(groupId, topMark);
                    EventTrans.post(EventMsg.CONTACT_GROUP_MAKE_TOP_REFRESH, groupId, topMark);
                }
            }
        });
    }

    private void shieldGroupConversation(final String groupId, final int shieldMark) {
        HttpContact.shieldGroupConversation(groupId, shieldMark, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                if (mGroupInfo != null) {
                    mGroupInfo.setShieldMark(shieldMark);
                    shieldIv.setSelected(TransUtil.intToBoolean(shieldMark));
                    LogUtil.i("shieldCb.isChecked", shieldIv.isSelected());
                    ChatRoomDao.updateShieldMark(groupId, ChatRoom.TYPE_GROUP, shieldMark);
                    GroupDao.updateShieldMark(groupId, shieldMark);
                    EventTrans.post(EventMsg.CONTACT_GROUP_SHIELD_REFRESH, mGroupInfo.getId(), shieldMark);
                }
            }
        });
    }

    private void quitDisbandGroup(final String groupId) {
        HttpContact.disbandGroup(groupId, new HttpCallBack(getAppContext(), ClassUtil.classMethodName()) {
            @Override
            public void success(Object data, int count) {
                ToastUtil.showShort(getAppContext(), R.string.commit_success);
                MessageDao.updateShowMark(groupId, ChatRoom.TYPE_GROUP);
                ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                GroupDao.deleteGroup(groupId);
                if (mGroupInfo.getGrade() == GroupInfo.TYPE_GRADE_OWNER) {
                    EventTrans.post(EventMsg.CONTACT_GROUP_DISBAND, groupId);
                } else {
                    EventTrans.post(EventMsg.CONTACT_GROUP_QUIT, mGroupInfo.getId(),
                            SpCons.getUser(getAppContext()).getId());
                }
                GroupInfoActivity.this.finish();
            }
        });
    }

    private void setGroupPermissionView(boolean needRefreshUI) {
        if (mGroupInfo == null) {
            return;
        }
        allowSearchIv.setSelected(TransUtil.intToBoolean(mGroupInfo.getSearchFlag()));
        if (mGroupInfo.getSearchFlag() == 1) {
            needVerifyFl.setBackgroundResource(R.color.white);
            needVerifyIv.setSelected(TransUtil.intToBoolean(mGroupInfo.getGroupValid()));
        } else {
            needVerifyFl.setBackgroundResource(R.color.gray_e1);
            needVerifyIv.setSelected(false);
        }
        allowInviteIv.setSelected(TransUtil.intToBoolean(mGroupInfo.getInviteFlag()));
        allowAddIv.setSelected(TransUtil.intToBoolean(mGroupInfo.getAddFriendMark()));
        if (mGroupInfo.getInviteFlag() == 1) {
            inviteNewMemberFl.setVisibility(View.VISIBLE);
            if (!needRefreshUI) {
                Intent intent = new Intent(getAppContext(), GroupInviteMemberActivity.class);
                intent.putExtra(ContactCons.EXTRA_CONTACT_GROUP_ID, mGroupInfo.getId());
                startActivity(intent);
            }
        } else if (needRefreshUI) {
            inviteNewMemberFl.setVisibility(View.GONE);
        } else {
            ToastUtil.showShort(getAppContext(), R.string.not_allow_invite_prompt);
            EventTrans.post(EventMsg.CONTACT_GROUP_PERMISSION_REFRESH, mGroupInfo);
        }
        bannedSpeakIv.setSelected(TransUtil.intToBoolean(1 - mGroupInfo.getGroupSpeak()));
    }

    private void setMemberView() {
        if (mGroupInfo.getGrade() == GroupInfo.TYPE_GRADE_OWNER) {
            exitGroupTv.setText(R.string.disband_group);
            quitPromptStr = getString(R.string.prompt_disband_group_confirm);
            permissionContainerLl.setVisibility(View.VISIBLE);
            setRightTv(R.string.edit);

        } else {
            if (mGroupInfo.getGrade() == GroupInfo.TYPE_GRADE_MANAGER) {
                setRightTv(R.string.edit);
                permissionContainerLl.setVisibility(View.VISIBLE);
            } else {
                permissionContainerLl.setVisibility(View.GONE);
            }
            exitGroupTv.setText(R.string.quit_group);
            quitPromptStr = getString(R.string.prompt_quit_group_confirm);
        }
        ImgUtil.load(getAppContext(), mGroupInfo.getGroupAvatar(), avatarIv,
                R.drawable.img_avatar_group_default);
        nicknameTv.setText(mGroupInfo.getGroupName());
        groupIdTv.setText("ID: " + mGroupInfo.getGroupNum());
        nameTv.setText(mGroupInfo.getGroupName());
        if (!TextUtils.isEmpty(mGroupInfo.getDescription())) {
            descTv.setText(mGroupInfo.getDescription());
        } else {
            descTv.setText(getString(R.string.no_introduction));
        }
        makeTopIv.setSelected(TransUtil.intToBoolean(mGroupInfo.getTopMark()));
        shieldIv.setSelected(TransUtil.intToBoolean(mGroupInfo.getShieldMark()));
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.CONTACT_GROUP_INFO_REFRESH:
                GroupInfo groupInfo = (GroupInfo) msg.getData();
                if (groupInfo == null || TextUtils.isEmpty(groupInfo.getId())
                        || !groupInfo.getId().equals(mGroupInfo.getId())) {
                    return;
                }
                if (!TextUtils.isEmpty(groupInfo.getGroupName())) {
                    mGroupInfo.setGroupName(groupInfo.getGroupName());
                    nameTv.setText(groupInfo.getGroupName());
                }
                if (!TextUtils.isEmpty(groupInfo.getDescription())) {
                    mGroupInfo.setDescription(groupInfo.getDescription());
                    descTv.setText(groupInfo.getDescription());
                }
                break;

            case EventMsg.CONTACT_GROUP_AVATAR_REFRESH:
                GroupInfo groupInfo1 = (GroupInfo) msg.getData();
                if (groupInfo1 == null || TextUtils.isEmpty(groupInfo1.getId())
                        || TextUtils.isEmpty(groupInfo1.getGroupAvatar())) {
                    return;
                }
                if (groupInfo1.getId().equals(mGroupInfo.getId())) {
                    mGroupInfo.setGroupAvatar(groupInfo1.getGroupAvatar());
                    ImgUtil.load(GroupInfoActivity.this, groupInfo1.getGroupAvatar(), avatarIv,
                            R.drawable.img_group);
                }
                break;

            case EventMsg.CONTACT_GROUP_QUIT:
            case EventMsg.CONTACT_GROUP_INVITE_MEMBER_REFRESH:
            case EventMsg.CONTACT_GROUP_AGREE_JOIN:
                getGroupMemberList(mGroupInfo.getId());
                break;

            case EventMsg.CONTACT_GROUP_DISBAND:
                String groupId1 = msg.getData().toString();
                if (!TextUtils.isEmpty(groupId1) && groupId1.equals(mGroupInfo.getId())) {
                    getHostActivity().finish();
                }
                break;

            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
                GroupInfo groupInfo3 = (GroupInfo) msg.getData();
                FriendInfo receiver3 = (FriendInfo) msg.getThirdData();
                if (groupInfo3 == null || TextUtils.isEmpty(groupInfo3.getId())
                        || receiver3 == null || TextUtils.isEmpty(receiver3.getId())) {
                    return;
                }
                if (groupInfo3.getId().equals(mGroupInfo.getId())) {
                    if (receiver3.getId().equals(SpCons.getUser(getAppContext()).getId())) {
                        ToastUtil.showShort(getAppContext(), R.string.you_are_moved_out_from_group);
                        getHostActivity().finish();
                    } else {
                        getGroupMemberList(mGroupInfo.getId());

                    }
                }
                break;

            case EventMsg.CONTACT_GROUP_PERMISSION_REFRESH:
                GroupInfo groupInfo2 = (GroupInfo) msg.getData();
                if (groupInfo2 == null || TextUtils.isEmpty(groupInfo2.getId())
                        || !groupInfo2.getId().equals(mGroupInfo.getId())) {
                    return;
                }
                if (groupInfo2.getAddFriendMark() != -1) {
                    mGroupInfo.setAddFriendMark(groupInfo2.getAddFriendMark());
                }
                if (groupInfo2.getInviteFlag() != -1) {
                    mGroupInfo.setInviteFlag(groupInfo2.getInviteFlag());
                }
                if (groupInfo2.getSearchFlag() != -1) {
                    mGroupInfo.setSearchFlag(groupInfo2.getSearchFlag());
                }
                if (groupInfo2.getGroupSpeak() != -1) {
                    mGroupInfo.setGroupSpeak(groupInfo2.getGroupSpeak());
                }
                if (groupInfo2.getGroupValid() != -1) {
                    mGroupInfo.setGroupValid(groupInfo2.getGroupValid());
                }
                setGroupPermissionView(true);
                break;

            default:

                break;
        }
    }

}
