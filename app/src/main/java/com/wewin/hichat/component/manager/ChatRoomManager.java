package com.wewin.hichat.component.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.UUIDUtil;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.MessageSendingDao;
import com.wewin.hichat.model.db.dao.UserDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.VoiceCall;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.model.socket.ChatSocket;
import com.wewin.hichat.view.conversation.ChatRoomActivity;
import com.wewin.hichat.view.conversation.ChatVoiceCallActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by Darren on 2019/4/15
 **/
public class ChatRoomManager {

    //开启单聊
    public static void startSingleRoomActivity(Context context, String friendId) {
        startSingleRoomActivity(context, friendId, null, 0);
    }

    //开启单聊
    public static void startSingleRoomActivity(Context context, String friendId, long startTimestamp) {
        startSingleRoomActivity(context, friendId, null, startTimestamp);
    }

    //开启单聊
    public static void startSingleRoomActivity(Context context, FriendInfo friendInfo) {
        startSingleRoomActivity(context, null, friendInfo, 0);
    }

    private static void startSingleRoomActivity(Context context, String friendId, FriendInfo friendInfo, long startTimestamp) {
        ChatRoom chatRoom;
        if (friendInfo == null && friendId != null) {
            chatRoom = packChatRoom(friendId, ChatRoom.TYPE_SINGLE);
        } else if (friendId == null && friendInfo != null) {
            chatRoom = packChatRoom(friendInfo.getId(), ChatRoom.TYPE_SINGLE);
        } else {
            return;
        }
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, startTimestamp);
        context.startActivity(intent);
    }

    //开启群聊
    public static void startGroupRoomActivity(Context context, String groupId) {
        startGroupRoomActivity(context, groupId, 0);
    }

    public static void startGroupRoomActivity(Context context, String groupId, long startTimestamp) {
        if (TextUtils.isEmpty(groupId)) {
            return;
        }
        ChatRoom chatRoom = packChatRoom(groupId, ChatRoom.TYPE_GROUP);
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, startTimestamp);
        context.startActivity(intent);
    }

    /**
     * 获取房间,解析消息时创建的房间
     */
    public static ChatRoom getChatRoom(ChatMsg chatMsg) {
        ChatRoom chatRoom = packChatRoom(chatMsg.getRoomId(), chatMsg.getRoomType());
        //处理@功能文本内容
        if (chatMsg.getAtFriendMap() != null
                && chatRoom.getAtType() != ChatMsg.TYPE_AT_ALL) {
            if (chatMsg.getAtFriendMap().containsKey("0")) {
                chatRoom.setAtType(ChatMsg.TYPE_AT_ALL);
            } else if (chatMsg.getAtFriendMap().containsKey(UserDao.user.getId())) {
                chatRoom.setAtType(ChatMsg.TYPE_AT_SINGLE);
            }
        }
        chatRoom.setLastChatMsg(chatMsg);
        return chatRoom;
    }

    /**
     * 获取房间，跳转界面时使用
     */
    public static ChatRoom getChatRoom(String roomId, String roomType) {
        return packChatRoom(roomId, roomType);
    }

    private static ChatRoom packChatRoom(String roomId, String roomType) {
        if (roomId == null || roomType == null) return null;
        ChatRoom chatRoom = ChatRoomDao.getRoom(roomId, roomType);
        if (chatRoom == null) {
            chatRoom = new ChatRoom(roomId, roomType);
            chatRoom.setUnreadNum(0);
        }
        if (ChatRoom.TYPE_SINGLE.equals(chatRoom.getRoomType())) {
            FriendInfo friendInfo = FriendDao.getFriendInfo(chatRoom.getRoomId());
            if (friendInfo == null || friendInfo.getFriendship() == 0) {
                friendInfo = ContactUserDao.getContactUser(chatRoom.getRoomId());
                if (friendInfo != null) {
                    chatRoom.setBlackMark(friendInfo.getBlackMark());
                    chatRoom.setTopMark(friendInfo.getTopMark());
                    chatRoom.setShieldMark(friendInfo.getShieldMark());
                    chatRoom.setUnreadNum(ChatRoomDao.getUnreadNum(friendInfo.getId(), ChatRoom.TYPE_SINGLE));
                    if (ContactUserDao.getContactUser(roomId).getId() == null) {
                        friendInfo.setId(roomId);
                        ContactUserDao.addContactUser(friendInfo);
                    }
                }
            } else {
                chatRoom.setTopMark(friendInfo.getTopMark());
                chatRoom.setShieldMark(friendInfo.getShieldMark());
                chatRoom.setBlackMark(friendInfo.getBlackMark());
                chatRoom.setUnreadNum(ChatRoomDao.getUnreadNum(friendInfo.getId(), ChatRoom.TYPE_SINGLE));
            }
        } else if (ChatRoom.TYPE_GROUP.equals(chatRoom.getRoomType())) {
            GroupInfo groupInfo = GroupDao.getGroup(chatRoom.getRoomId());
            if (groupInfo != null) {
                chatRoom.setTopMark(groupInfo.getTopMark());
                chatRoom.setShieldMark(groupInfo.getShieldMark());
                chatRoom.setGroupSpeakMark(groupInfo.getGroupSpeak());
                chatRoom.setInviteMark(groupInfo.getInviteFlag());
                chatRoom.setGroupGrade(groupInfo.getGrade());
                chatRoom.setUnreadNum(ChatRoomDao.getUnreadNum(groupInfo.getId(), ChatRoom.TYPE_GROUP));
            }
        }
        ChatRoomDao.addRoom(chatRoom);
        return chatRoom;
    }

    //启动语音通话邀请界面
    public static void startVoiceCallInviteActivity(Context context, ChatRoom chatRoom) {
        Intent intent = new Intent(context, ChatVoiceCallActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_CALL_TYPE, ChatVoiceCallActivity.TYPE_CALL_INVITING);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //启动语音通话等待接听界面
    private static void startVoiceCallWaitActivity(Context context, ChatRoom chatRoom) {
        Intent intent = new Intent(context, ChatVoiceCallActivity.class);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_CALL_TYPE, ChatVoiceCallActivity.TYPE_CALL_WAIT_ANSWER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //封装语音通话邀请消息
    public static ChatMsg packVoiceCallInviteMsg(ChatRoom chatRoom, String channel) {
        VoiceCall voiceCall = new VoiceCall(UserDao.user.getId(), channel, VoiceCall.INVITE, 0);
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_VOICE_CALL, "", null, voiceCall, null);
    }

    //封装语音通话消息
    public static ChatMsg packVoiceCallMsg(ChatRoom chatRoom, VoiceCall voiceCall) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_VOICE_CALL, "", null, voiceCall, null);
    }

    //封装file消息
    private static ChatMsg packFileMsg(ChatRoom chatRoom, FileInfo fileInfo) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_FILE, "", fileInfo, null, null);
    }

    //封装text消息
    public static ChatMsg packTextMsg(ChatRoom chatRoom, String contentStr) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_TEXT, contentStr, null, null, null);
    }

    //封装@文字消息
    public static ChatMsg packTextMsg(ChatRoom chatRoom, String contentStr, String atMember) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_AT, contentStr, null, null, atMember);
    }

    //封装发送的消息实体
    private static ChatMsg packChatMsg(ChatRoom chatRoom, int contentType, String contentStr,
                                       FileInfo fileInfo, VoiceCall voiceCall, String atMember) {
        if (chatRoom == null) {
            return null;
        }
        ChatMsg chatMsg = new ChatMsg(chatRoom.getRoomId(), chatRoom.getRoomType(),
                UserDao.user.getId(), contentType, contentStr, TimeUtil.getServerTimestamp());
        if (ChatRoom.TYPE_SINGLE.equals(chatRoom.getRoomType())) {
            chatMsg.setReceiverId(chatRoom.getRoomId());
        } else {
            try {
                if (!TextUtils.isEmpty(atMember)) {
                    chatMsg.setAtFriendMap(JSONObject.parseObject(atMember, Map.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            chatMsg.setGroupId(chatRoom.getRoomId());
        }
        chatMsg.setLocalMsgId(UUIDUtil.get32UUID());
        chatMsg.setFileInfo(fileInfo);
        chatMsg.setVoiceCall(voiceCall);
        chatMsg.setSendState(ChatMsg.TYPE_SENDING);
        return chatMsg;
    }

    //发送的消息保存到MessageDao
    public static void saveChatMsg(ChatMsg chatMsg) {
        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && chatMsg.getVoiceCall() != null
                && (chatMsg.getVoiceCall().getConnectState() == VoiceCall.INVITE
                || chatMsg.getVoiceCall().getConnectState() == VoiceCall.CONNECT)) {
            return;
        }
        MessageDao.addMessage(chatMsg);
        MessageSendingDao.addMessage(chatMsg);
        EventTrans.post(EventMsg.SOCKET_ON_MESSAGE, chatMsg);
    }

    //修改发送的消息内容，专用于临时和好友的区分
    public static ChatMsg changeSendMsgRoomType(ChatMsg chatMsg, String id) {
        if (ChatRoom.TYPE_SINGLE.equals(chatMsg.getRoomType())) {
            FriendInfo friendInfo = FriendDao.getFriendInfo(id);
            //临时会话，单方面好友状况下，发生消息类型为好友类型
            if (friendInfo != null && friendInfo.getFriendship() == 0) {
                chatMsg.setFriendshipMark(1);
            }
        }
        return chatMsg;
    }

    //修改接收的消息内容，专用于临时和好友的区分
    private static ChatMsg changeReceiverMsgRoomType(ChatMsg chatMsg, String id) {
        //临时会话，单方面好友状况下，发生消息类型为好友类型
        if (ChatRoom.TYPE_SINGLE.equals(chatMsg.getRoomType())) {
            FriendInfo friendInfo = FriendDao.getFriendInfo(id);
            if (friendInfo != null && friendInfo.getFriendship() == 0) {
                chatMsg.setFriendshipMark(0);
            }
        }
        return chatMsg;
    }

    //解析收到的聊天消息
    public static void parseReceiveMsg(Context context, ChatMsg receiveMsg) {
        if (receiveMsg == null) {
            return;
        }
        if (ChatRoom.TYPE_SINGLE.equals(receiveMsg.getRoomType())) {
            receiveMsg = changeReceiverMsgRoomType(receiveMsg, receiveMsg.getSenderId());
            receiveMsg.setRoomId(receiveMsg.getSenderId());
        } else if (ChatRoom.TYPE_GROUP.equals(receiveMsg.getRoomType())) {
            receiveMsg.setRoomId(receiveMsg.getGroupId());
        }
        receiveMsg.setSendState(ChatMsg.TYPE_SEND_SUCCESS);
        receiveMsg.setLocalMsgId(UUIDUtil.get32UUID());

        switch (receiveMsg.getContentType()) {
            case ChatMsg.TYPE_CONTENT_AT:
                MessageDao.addMessage(receiveMsg);
                EventTrans.post(EventMsg.SOCKET_ON_MESSAGE, receiveMsg);
                break;
            case ChatMsg.TYPE_CONTENT_TEXT:
                MessageDao.addMessage(receiveMsg);
                EventTrans.post(EventMsg.SOCKET_ON_MESSAGE, receiveMsg);
                break;

            case ChatMsg.TYPE_CONTENT_FILE:
                if (receiveMsg.getFileInfo() != null) {
                    receiveMsg.getFileInfo().setOriginPath("");
                    receiveMsg.getFileInfo().setSavePath("");
                    receiveMsg.getFileInfo().setTapeUnreadMark(1);
                }
                MessageDao.addMessage(receiveMsg);
                EventTrans.post(EventMsg.SOCKET_ON_MESSAGE, receiveMsg);
                break;

            case ChatMsg.TYPE_CONTENT_VOICE_CALL:
                //语音通话状态如果为邀请或接通，不保存消息记录
                VoiceCall voiceCall = receiveMsg.getVoiceCall();
                if (voiceCall == null) {
                    return;
                }
                if (voiceCall.getConnectState() == VoiceCall.INVITE) {
                    ChatRoom chatRoom = new ChatRoom(receiveMsg.getRoomId(), receiveMsg.getRoomType());
                    chatRoom.setLastChatMsg(receiveMsg);
                    startVoiceCallWaitActivity(context, chatRoom);

                } else {
                    EventTrans.post(EventMsg.MESSAGE_CHAT_VOICE_CALL_REFRESH,
                            voiceCall.getConnectState());
                    if (voiceCall.getConnectState() != VoiceCall.CONNECT) {
                        MessageDao.addMessage(receiveMsg);
                        EventTrans.post(EventMsg.SOCKET_ON_MESSAGE, receiveMsg);
                    }
                }
                break;
        }
    }

    //移除语音通话状态为邀请或接通的记录
    public static void removeVoiceCallInviteConnect(List<ChatMsg> dataMsgList) {
        if (dataMsgList == null || dataMsgList.isEmpty()) {
            return;
        }
        try {
            for (int i = dataMsgList.size() - 1; i >= 0; i--) {
                if (dataMsgList.get(i).getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL) {
                    VoiceCall voiceCall = dataMsgList.get(i).getVoiceCall();
                    if (voiceCall != null && voiceCall.getConnectState() == VoiceCall.INVITE
                            && voiceCall.getConnectState() == VoiceCall.CONNECT) {
                        dataMsgList.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //非语音通话&&消息发送者非自己&&（（栈顶!=聊天室）||（栈顶==聊天室&&聊天室roomId !=该条消息的roomId））
    //or语音通话&&timeout/cancel&&inviteId非自己&&（（栈顶!=聊天室）||（栈顶==聊天室&&聊天室roomId !=该条消息的roomId））
    //则未读数量+1
    public static boolean isUnreadNumAdd(Context context, ChatMsg receiveMsg) {
        if (receiveMsg != null && receiveMsg.getContentType() != ChatMsg.TYPE_CONTENT_VOICE_CALL
                && !UserDao.user.getId().equals(receiveMsg.getSenderId())) {
            String stackTopActivityName = ClassUtil.getStackTopActivity(context);
            if (!TextUtils.isEmpty(stackTopActivityName)) {
                if (!stackTopActivityName.contains("ChatRoomActivity")) {
                    return true;
                } else {
                    return !receiveMsg.getRoomId().equals(SpCons.getCurrentRoomId(context))
                            && !receiveMsg.getRoomType().equals(SpCons.getCurrentRoomType(context));
                }
            }

        } else if (receiveMsg != null && receiveMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && receiveMsg.getVoiceCall() != null
                && (receiveMsg.getVoiceCall().getConnectState() == VoiceCall.CANCEL
                || receiveMsg.getVoiceCall().getConnectState() == VoiceCall.TIME_OUT)
                && !receiveMsg.getVoiceCall().getInviteUserId().equals(UserDao.user.getId())) {

            String stackTopActivityName = ClassUtil.getStackTopActivity(context);
            if (!TextUtils.isEmpty(stackTopActivityName)) {
                if (!stackTopActivityName.contains("ChatRoomActivity")) {
                    return true;
                } else {
                    return !receiveMsg.getRoomId().equals(SpCons.getCurrentRoomId(context))
                            && !receiveMsg.getRoomType().equals(SpCons.getCurrentRoomType(context));
                }
            }
        }
        return false;
    }

    //聊天消息的文件发送
    public static void uploadFile(Context context, ChatRoom chatRoom, FileInfo fileInfo) {
        final ChatMsg chatMsg = ChatRoomManager.packFileMsg(chatRoom, fileInfo);
        if (chatMsg.getFileInfo() == null) {
            return;
        }
        ChatSocket.getInstance().send(chatMsg);
        HttpContact.uploadFile(chatMsg.getFileInfo().getOriginPath(), chatMsg.getFileInfo().getFileType(),
                chatRoom.getRoomId(), chatRoom.getRoomType(), new HttpCallBack(context, ClassUtil.classMethodName()) {
                    @Override
                    public void success(Object data, int count) {
                        if (data == null) {
                            return;
                        }
                        try {
                            FileInfo fileData = JSON.parseObject(data.toString(), FileInfo.class);
                            if (fileData == null) {
                                return;
                            }
                            chatMsg.getFileInfo().setId(fileData.getId());
                            chatMsg.getFileInfo().setDownloadPath(fileData.getDownloadPath().replace("\\", "/"));
                            MessageDao.updateFileInfo(chatMsg.getLocalMsgId(), chatMsg.getFileInfo());
                            ChatSocket.getInstance().send(chatMsg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
