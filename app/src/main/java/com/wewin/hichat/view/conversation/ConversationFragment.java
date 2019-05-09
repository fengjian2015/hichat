package com.wewin.hichat.view.conversation;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wewin.hichat.MainActivity;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.EntityUtil;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.base.BaseFragment;
import com.wewin.hichat.component.base.BaseRcvAdapter;
import com.wewin.hichat.component.adapter.ConversationListRcvAdapter;
import com.wewin.hichat.component.dialog.SelectDialog;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.ServerConversation;
import com.wewin.hichat.model.db.entity.ServerConversation.DeleteConversation;
import com.wewin.hichat.model.db.entity.ServerConversation.UpdateConversation;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.model.http.HttpMessage;
import com.wewin.hichat.view.search.ChatRecordSearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 主页-会话页
 * Created by Darren on 2018/12/13.
 */
public class ConversationFragment extends BaseFragment {

    private TextView editTv, selectAllTv, makeTopTv, deleteTv;
    private ImageView newMsgIv;
    private LinearLayout cancelLl;
    private FrameLayout searchFl;
    private RelativeLayout botEditContainerRl;
    private RecyclerView containerRcv;
    private List<ChatRoom> mRoomList = new ArrayList<>();
    private ConversationListRcvAdapter rcvAdapter;
    private boolean isEditMode = false;
    private boolean isSelectAll = false;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initViews() {
        containerRcv = parentView.findViewById(R.id.rcv_conversation_container);
        editTv = parentView.findViewById(R.id.tv_conversation_edit);
        newMsgIv = parentView.findViewById(R.id.iv_conversation_new_message);
        selectAllTv = parentView.findViewById(R.id.tv_conversation_select_all);
        makeTopTv = parentView.findViewById(R.id.tv_conversation_make_top);
        deleteTv = parentView.findViewById(R.id.tv_conversation_delete);
        botEditContainerRl = parentView.findViewById(R.id.rl_conversation_bot_edit_container);
        cancelLl = parentView.findViewById(R.id.ll_main_conversation_cancel_container);
        searchFl = parentView.findViewById(R.id.fl_conversation_search);
    }

    @Override
    protected void initViewsData() {
        initRecyclerView();
        setViewByCache();
        getServerConversationList();
    }

    @Override
    protected void setListener() {
        editTv.setOnClickListener(this);
        newMsgIv.setOnClickListener(this);
        selectAllTv.setOnClickListener(this);
        makeTopTv.setOnClickListener(this);
        deleteTv.setOnClickListener(this);
        cancelLl.setOnClickListener(this);
        searchFl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_conversation_edit:
                if (!isEditMode) {
                    isEditMode = true;
                    ((MainActivity) getHostActivity()).botContainerFl.setVisibility(View.GONE);
                    botEditContainerRl.setVisibility(View.VISIBLE);
                    editTv.setVisibility(View.INVISIBLE);
                    cancelLl.setVisibility(View.VISIBLE);
                }
                rcvAdapter.setEditMode(isEditMode);
                updateRcv();
                break;

            case R.id.ll_main_conversation_cancel_container:
                setEditCancelView();
                break;

            case R.id.iv_conversation_new_message:
                startActivity(new Intent(getHostActivity(), ConversationAddActivity.class));
                break;

            case R.id.tv_conversation_select_all:
                if (!isSelectAll) {
                    for (ChatRoom chatRoom : mRoomList) {
                        chatRoom.setChecked(true);
                    }
                    updateRcv();
                    isSelectAll = true;
                    selectAllTv.setText(R.string.cancel_all);
                } else {
                    for (ChatRoom chatRoom : mRoomList) {
                        chatRoom.setChecked(false);
                    }
                    updateRcv();
                    isSelectAll = false;
                    selectAllTv.setText(R.string.select_all);
                }
                break;

            case R.id.tv_conversation_make_top:
                for (ChatRoom chatRoom : mRoomList) {
                    if (chatRoom.isChecked()) {
                        int topMark = 0;
                        if (chatRoom.getTopMark() == 0) {
                            topMark = 1;
                        }
                        if (ChatRoom.TYPE_SINGLE.equals(chatRoom.getRoomType())) {
                            makeTopFriend(chatRoom, topMark);

                        } else if ("group".equals(chatRoom.getRoomType())) {
                            makeTopGroup(chatRoom, topMark);
                        }
                        break;
                    }
                }
                break;

            case R.id.tv_conversation_delete:
                showDeleteDialog();
                break;

            case R.id.fl_conversation_search:
                startActivity(new Intent(getHostActivity(), ChatRecordSearchActivity.class));
                break;
        }
    }

    private void initRecyclerView() {
        rcvAdapter = new ConversationListRcvAdapter(getHostActivity(), mRoomList);
        final LinearLayoutManager manager = new LinearLayoutManager(getHostActivity());
        containerRcv.setAdapter(rcvAdapter);
        containerRcv.setLayoutManager(manager);
        rcvAdapter.setOnItemClickListener(new BaseRcvAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                if (!isEditMode) {
                    if (ChatRoom.TYPE_SINGLE.equals(mRoomList.get(position).getRoomType())) {
                        ChatRoomManager.startSingleRoomActivity(getHostActivity(), mRoomList.get(position).getRoomId());

                    } else if (ChatRoom.TYPE_GROUP.equals(mRoomList.get(position).getRoomType())) {
                        ChatRoomManager.startGroupRoomActivity(getHostActivity(),
                                mRoomList.get(position).getRoomId());
                    }
                } else {
                    mRoomList.get(position).setChecked(!mRoomList.get(position).isChecked());
                    updateRcv();
                }
            }
        });
    }

    private void updateRcv() {
        if (rcvAdapter != null) {
            Collections.sort(mRoomList, new ChatRoom.TopComparator());
            rcvAdapter.notifyDataSetChanged();
        }
        if (isEditMode) {
            int checkedCount = 0;
            int topMark = 0;
            for (ChatRoom chatRoom : mRoomList) {
                if (chatRoom.isChecked()) {
                    checkedCount++;
                    topMark = chatRoom.getTopMark();
                }
            }
            if (checkedCount == 0) {
                makeTopTv.setEnabled(false);
                deleteTv.setEnabled(false);

            } else if (checkedCount == 1) {
                makeTopTv.setEnabled(true);
                deleteTv.setEnabled(true);
                if (topMark == 1) {
                    makeTopTv.setText(getString(R.string.cancel_make_top));
                } else {
                    makeTopTv.setText(getString(R.string.make_top));
                }
            } else {
                makeTopTv.setEnabled(false);
                deleteTv.setEnabled(true);
            }
        }
    }

    private void setEditCancelView() {
        if (isEditMode) {
            isEditMode = false;
            ((MainActivity) getHostActivity()).botContainerFl.setVisibility(View.VISIBLE);
            botEditContainerRl.setVisibility(View.GONE);
            editTv.setVisibility(View.VISIBLE);
            cancelLl.setVisibility(View.INVISIBLE);
            for (ChatRoom room : mRoomList) {
                room.setChecked(false);
            }
        }
        rcvAdapter.setEditMode(isEditMode);
        updateRcv();
    }

    private void setViewByCache() {
        List<ChatRoom> roomList = ChatRoomDao.getRoomList();
        LogUtil.i("setViewByCache", roomList);
        if (roomList != null) {
            mRoomList.clear();
            mRoomList.addAll(roomList);
            updateRcv();
        }
    }

    private void processMakeTopResult(ChatRoom chatRoom, int topMark) {
        chatRoom.setTopMark(topMark);
        ChatRoomDao.updateTopMark(chatRoom.getRoomId(), chatRoom.getRoomType(), topMark);
        for (ChatRoom room : mRoomList) {
            room.setChecked(false);
        }
        LogUtil.i("makeTop", mRoomList);
        setEditCancelView();
    }

    private void showDeleteDialog() {
        SelectDialog.SelectBuilder selectBuilder = new SelectDialog.SelectBuilder(getHostActivity());
        SelectDialog deleteDialog = selectBuilder.setSelectStrArr(new String[]{getString(R.string.delete)})
                .setTextColor(R.color.red_light2)
                .setTextColorPosition(0)
                .setOnLvItemClickListener(new SelectDialog.SelectBuilder.OnLvItemClickListener() {
                    @Override
                    public void itemClick(int lvItemPosition) {
                        if (lvItemPosition == 0) {
                            List<ChatRoom> deleteList = new ArrayList<>();
                            for (int i = mRoomList.size() - 1; i >= 0; i--) {
                                if (mRoomList.get(i).isChecked()) {
                                    deleteList.add(mRoomList.get(i));
                                    mRoomList.remove(i);
                                }
                            }
                            MessageDao.updateRoomShowMarkList(deleteList);
                            ChatRoomDao.deleteRoomList(deleteList);
                            setEditCancelView();
                        }
                    }
                }).create();
        deleteDialog.show();
    }

    private void makeTopFriend(final ChatRoom chatRoom, final int topMark) {
        HttpContact.makeTopFriend(chatRoom.getRoomId(), topMark,FriendDao.findFriendshipMark(chatRoom.getRoomId()),
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        processMakeTopResult(chatRoom, topMark);
                        LogUtil.i("chatId", chatRoom.getRoomId());
                        LogUtil.i("topMark", topMark);
                    }
                });
    }

    private void makeTopGroup(final ChatRoom chatRoom, final int topMark) {
        HttpContact.makeTopGroup(chatRoom.getRoomId(), topMark,
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        processMakeTopResult(chatRoom, topMark);
                    }
                });
    }

    //增量获取会话列表进行同步
    private void getServerConversationList() {
        List<ChatRoom> roomList = ChatRoomDao.getRoomList();
        String roomIdStr = "";
        if (roomList != null && !roomList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ChatRoom room : roomList) {
                String maxMsgId = MessageDao.getMaxMsgIdByRoomId(room.getRoomId(), room.getRoomType());
                if (TextUtils.isEmpty(maxMsgId)) {
                    maxMsgId = "0";
                }
                sb.append(room.getRoomId()).append("-").append(room.getRoomType()).append("-")
                        .append(maxMsgId).append(",");
            }
            roomIdStr = sb.toString().substring(0, sb.toString().length() - 1);
        }
        LogUtil.i("roomIdStr", roomIdStr);
        HttpMessage.getServerConversationList(roomIdStr,
                new HttpCallBack(getHostActivity(), ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            ServerConversation serverConversation = JSON.parseObject(data.toString(),
                                    ServerConversation.class);

                            List<DeleteConversation> deleteList = serverConversation.getDeleteConversations();
                            List<UpdateConversation> updateList = serverConversation.getUpdateConversations();
                            if (deleteList != null) {
                                for (DeleteConversation deleteConversation : deleteList) {
                                    MessageDao.updateShowMark(deleteConversation.getConversationId(),
                                            deleteConversation.getConversationType());
                                    ChatRoomDao.deleteRoom(deleteConversation.getConversationId(),
                                            deleteConversation.getConversationType());
                                }
                            }
                            if (updateList != null) {
                                List<ChatRoom> chatRoomList = EntityUtil
                                        .parseUpdateConversation(updateList);
                                for (ChatRoom room : chatRoomList) {
                                    if (!TextUtils.isEmpty(room.getUnSyncMsgFirstId())
                                            && !"0".equals(room.getUnSyncMsgFirstId())
                                            && room.getLastChatMsg() != null) {
                                        LogUtil.i("unSyncId", room.getRoomId() + room.getRoomType() +
                                                room.getUnSyncMsgFirstId());
                                        room.getLastChatMsg().setUnSyncMsgFirstId(room.getUnSyncMsgFirstId());
                                    }
                                    MessageDao.addMessage(room.getLastChatMsg());
                                }
                                ChatRoomDao.addRoomList(chatRoomList);
                            }
                            mRoomList.clear();
                            mRoomList.addAll(ChatRoomDao.getRoomList());
                            updateRcv();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onEventTrans(EventMsg msg) {
        switch (msg.getKey()) {
            case EventMsg.SOCKET_ON_MESSAGE:
                ChatMsg receiveMsg = (ChatMsg) msg.getData();
                LogUtil.i("SOCKET_ON_MESSAGE", receiveMsg);
                if (receiveMsg == null) {
                    return;
                }
               ChatRoom chatRoom=ChatRoomManager.getChatRoom(receiveMsg);
                if (ChatRoomManager.isUnreadNumAdd(getHostActivity(), receiveMsg)) {
                    chatRoom.setUnreadNum(chatRoom.getUnreadNum() + 1);
                }
                ChatRoomDao.addRoom(chatRoom);
                mRoomList.clear();
                mRoomList.addAll(ChatRoomDao.getRoomList());
                updateRcv();
                if (getHostActivity() instanceof MainActivity) {
                    ((MainActivity) getHostActivity()).setUnreadNumView();
                }
                break;

            case EventMsg.CONTACT_GROUP_AVATAR_REFRESH:
            case EventMsg.CONTACT_FRIEND_NOTE_REFRESH:
            case EventMsg.CONTACT_GROUP_NAME_REFRESH:
            case EventMsg.CONTACT_FRIEND_AVATAR_REFRESH:
            case EventMsg.CONTACT_FRIEND_NAME_REFRESH:
            case EventMsg.CONTACT_GROUP_INFO_REFRESH:
                updateRcv();
                break;

            case EventMsg.CONTACT_DELETE_BY_OTHER:
            case EventMsg.CONTACT_GROUP_REMOVE_MEMBER:
            case EventMsg.CONTACT_GROUP_QUIT:
            case EventMsg.CONTACT_FRIEND_BLACK_REFRESH:
            case EventMsg.CONTACT_GROUP_DISBAND:
            case EventMsg.CONTACT_FRIEND_MAKE_TOP_REFRESH:
            case EventMsg.CONTACT_FRIEND_SHIELD_REFRESH:
            case EventMsg.CONTACT_GROUP_MAKE_TOP_REFRESH:
            case EventMsg.CONTACT_GROUP_SHIELD_REFRESH:
            case EventMsg.CONVERSATION_CLEAR_REFRESH:
            case EventMsg.CONTACT_FRIEND_DELETE_REFRESH:
            case EventMsg.CONVERSATION_DELETE_ROOM:
            case EventMsg.CONVERSATION_DELETE_ALL_ROOM:
                mRoomList.clear();
                mRoomList.addAll(ChatRoomDao.getRoomList());
                updateRcv();
                break;

            case EventMsg.CONTACT_FRIEND_LIST_GET_REFRESH:
                List<FriendInfo> friendList = FriendDao.getFriendList();
                for (FriendInfo friend : friendList) {
                    for (ChatRoom roomData : mRoomList) {
                        if (friend.getId().equals(roomData.getRoomId())
                                && ChatRoom.TYPE_SINGLE.equals(roomData.getRoomType())) {
                            roomData.setTopMark(friend.getTopMark());
                            roomData.setShieldMark(friend.getShieldMark());
                        }
                    }
                }
                updateRcv();
                LogUtil.i("FRIEND_LIST_GET_REFRESH", mRoomList);
                ChatRoomDao.addRoomList(mRoomList);
                break;

            case EventMsg.CONTACT_GROUP_LIST_GET_REFRESH:
                List<GroupInfo> groupList = GroupDao.getGroupList();
                for (GroupInfo group : groupList) {
                    for (ChatRoom roomData : mRoomList) {
                        if (group.getId().equals(roomData.getRoomId()) && "group".equals(roomData.getRoomType())) {
                            roomData.setTopMark(group.getTopMark());
                            roomData.setShieldMark(group.getShieldMark());
                        }
                    }
                }
                updateRcv();
                LogUtil.i("GROUP_LIST_GET_REFRESH", mRoomList);
                ChatRoomDao.addRoomList(mRoomList);
                break;

            case EventMsg.CONVERSATION_UNREAD_NUM_REFRESH:
                LogUtil.i("CONVERSATION_UNREAD_NUM_REFRESH");
                ChatRoom chatRoom1 = (ChatRoom) msg.getData();
                if (chatRoom1 != null) {
                    for (ChatRoom roomData : mRoomList) {
                        if (roomData.getRoomId().equals(chatRoom1.getRoomId())
                                && roomData.getRoomType().equals(chatRoom1.getRoomType())) {
                            roomData.setUnreadNum(0);
                            roomData.setAtType(ChatMsg.TYPE_AT_NORMAL);
                            ChatRoomDao.updateUnreadNum(roomData.getRoomId(),
                                    roomData.getRoomType(), 0);
                            ChatRoomDao.updateAtType(roomData.getRoomId(), roomData.getRoomType(),
                                    ChatMsg.TYPE_AT_NORMAL);
                            updateRcv();
                            break;
                        }
                    }
                }
                if (getHostActivity() instanceof MainActivity) {
                    ((MainActivity) getHostActivity()).setUnreadNumView();
                }
                break;
        }
    }

}
