package com.wewin.hichat.component.manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.manage.RingVibrateManager;
import com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener;
import com.wewin.hichat.androidlib.rxjava.RxJavaObserver;
import com.wewin.hichat.androidlib.rxjava.RxJavaScheduler;
import com.wewin.hichat.androidlib.utils.ClassUtil;
import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.utils.EntityUtil;
import com.wewin.hichat.androidlib.utils.HyperLinkUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
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
import com.wewin.hichat.model.db.entity.ServerConversation;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.model.db.entity.VoiceCall;
import com.wewin.hichat.model.http.HttpContact;
import com.wewin.hichat.model.http.HttpMessage;
import com.wewin.hichat.model.socket.ChatSocket;
import com.wewin.hichat.view.conversation.ChatRoomActivity;
import com.wewin.hichat.view.conversation.SelectSendActivity;

import java.util.List;
import java.util.Map;

import io.reactivex.ObservableEmitter;


/**
 * Created by Darren on 2019/4/15
 **/
public class ChatRoomManager {

    //当前已开启的聊天页面的roomId
    private static String currentRoomId = "";
    //当前已开启的聊天页面的roomType
    private static String currentRoomType = "";

    public static void setCurrentRoomId(String currentRoomId) {
        ChatRoomManager.currentRoomId = currentRoomId;
    }

    public static void setCurrentRoomType(String currentRoomType) {
        ChatRoomManager.currentRoomType = currentRoomType;
    }

    public static void clearRoomInfo() {
        ChatRoomManager.currentRoomId = "";
        ChatRoomManager.currentRoomType = "";
    }

    /**
     * 开启单聊
     */
    public static void startSingleRoomActivity(Context context, String friendId) {
        startSingleRoomActivity(context, friendId, null, 0);
    }

    public static void startSingleRoomActivity(Context context, String friendId, long startTimestamp) {
        startSingleRoomActivity(context, friendId, null, startTimestamp);
    }

    public static void startSingleRoomActivity(Context context, FriendInfo friendInfo) {
        startSingleRoomActivity(context, null, friendInfo, 0);
    }

    private static void startSingleRoomActivity(Context context, String friendId, FriendInfo friendInfo,
                                                long startTimestamp) {
        ChatRoom chatRoom;
        if (friendInfo == null && !TextUtils.isEmpty(friendId)) {
            chatRoom = packChatRoom(friendId, ChatRoom.TYPE_SINGLE);
        } else if (TextUtils.isEmpty(friendId) && friendInfo != null) {
            chatRoom = packChatRoom(friendInfo.getId(), ChatRoom.TYPE_SINGLE);
        } else {
            return;
        }
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, startTimestamp);
        context.startActivity(intent);
    }

    /**
     * 开启群聊
     */
    public static void startGroupRoomActivity(Context context, String groupId) {
        startGroupRoomActivity(context, groupId, 0);
    }

    public static void startGroupRoomActivity(Context context, String groupId, long startTimestamp) {
        if (TextUtils.isEmpty(groupId)) {
            return;
        }
        ChatRoom chatRoom = packChatRoom(groupId, ChatRoom.TYPE_GROUP);
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, startTimestamp);
        context.startActivity(intent);
    }

    /**
     * 获取房间,解析消息时创建的房间
     */
    public static ChatRoom getChatRoom(ChatMsg chatMsg,boolean at) {
        ChatRoom chatRoom = packChatRoom(chatMsg.getRoomId(), chatMsg.getRoomType());
        if (chatRoom == null) {
            return null;
        }
        //处理@功能文本内容
        if (chatMsg.getAtFriendMap() != null &&at) {
            if (chatMsg.getAtFriendMap().containsKey("0")) {
                chatRoom.setAtType(ChatMsg.TYPE_AT_ALL);
            } else if (chatMsg.getAtFriendMap().containsKey(UserDao.user.getId())) {
                chatRoom.setAtType(ChatMsg.TYPE_AT_SINGLE);
            }
        }
        chatRoom.setLastChatMsg(chatMsg);
        chatRoom.setLastMsgTime(chatMsg.getCreateTimestamp());
        return chatRoom;
    }

    /**
     * 获取房间，跳转界面时使用
     */
    public static ChatRoom getChatRoom(String roomId, String roomType) {
        return packChatRoom(roomId, roomType);
    }

    private static ChatRoom packChatRoom(String roomId, String roomType) {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(roomType)) {
            return null;
        }
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
                    if (TextUtils.isEmpty(friendInfo.getId())) {
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
        return chatRoom;
    }

    /**
     * 封装语音通话邀请消息
     */
    public static ChatMsg packVoiceCallInviteMsg(ChatRoom chatRoom, String channel) {
        VoiceCall voiceCall = new VoiceCall(UserDao.user.getId(), channel, VoiceCall.INVITE, 0);
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_VOICE_CALL, "", null, voiceCall, null);
    }

    /**
     * 封装语音通话消息
     */
    public static ChatMsg packVoiceCallMsg(ChatRoom chatRoom, VoiceCall voiceCall) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_VOICE_CALL, "", null, voiceCall, null);
    }

    /**
     * 封装file消息
     */
    private static ChatMsg packFileMsg(ChatRoom chatRoom, FileInfo fileInfo) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_FILE, "", fileInfo, null, null);
    }

    /**
     * 封装text消息
     */
    public static ChatMsg packTextMsg(ChatRoom chatRoom, String contentStr) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_TEXT, contentStr, null, null, null);
    }

    /**
     * 封装@文字消息
     */
    public static ChatMsg packTextMsg(ChatRoom chatRoom, String contentStr, String atMember) {
        return packChatMsg(chatRoom, ChatMsg.TYPE_CONTENT_AT, contentStr, null, null, atMember);
    }

    /**
     * 转发消息
     */
    public static ChatMsg packForwardMsg(ChatMsg chatMsg,String roomType,String roomId){
        chatMsg.setRoomType(roomType);
        chatMsg.setRoomId(roomId);
        chatMsg.setSenderId(UserDao.user.getId());
        chatMsg.setCreateTimestamp(TimeUtil.getServerTimestamp());
        chatMsg.setLocalMsgId(UUIDUtil.get32UUID());
        chatMsg.setSendState(ChatMsg.TYPE_SENDING);
        if (ChatRoom.TYPE_SINGLE.equals(roomType)) {
            chatMsg.setReceiverId(roomId);
            //非好友关系，则赋值senderInfo, receiverInfo
            FriendInfo friend = FriendDao.getFriendInfo(roomId);
            if (friend == null || friend.getFriendship() == 0) {
                FriendInfo senderInfo = new FriendInfo();
                senderInfo.setUsername(UserDao.user.getUsername());
                senderInfo.setAvatar(UserDao.user.getAvatar());
                chatMsg.setSenderInfo(senderInfo);

                FriendInfo receiverInfo = ContactUserDao.getContactUser(chatMsg.getReceiverId());
                chatMsg.setReceiverInfo(receiverInfo);
            }
        }else{
            chatMsg.setGroupId(roomId);
        }
        return chatMsg;
    }

    /**
     * 封装发送的消息实体
     */
    private static ChatMsg packChatMsg(ChatRoom chatRoom, int contentType, String contentStr,
                                       FileInfo fileInfo, VoiceCall voiceCall, String atMember) {
        if (chatRoom == null) {
            return null;
        }
        ChatMsg chatMsg = new ChatMsg(chatRoom.getRoomId(), chatRoom.getRoomType(),
                UserDao.user.getId(), contentType, contentStr, TimeUtil.getServerTimestamp());
        if (contentType == ChatMsg.TYPE_CONTENT_TEXT || contentType == ChatMsg.TYPE_CONTENT_AT) {
            if (EmoticonUtil.isContainEmotion(contentStr)) {
                chatMsg.setEmoMark(1);
            }
            if (HyperLinkUtil.isContainPhone(contentStr)) {
                chatMsg.setPhoneMark(1);
            }
            if (HyperLinkUtil.isContainUrl(contentStr)) {
                chatMsg.setUrlMark(1);
            }
        }
        if (ChatRoom.TYPE_SINGLE.equals(chatRoom.getRoomType())) {
            chatMsg.setReceiverId(chatRoom.getRoomId());
            //非好友关系，则赋值senderInfo, receiverInfo
            FriendInfo friend = FriendDao.getFriendInfo(chatRoom.getRoomId());
            if (friend == null || friend.getFriendship() == 0) {
                FriendInfo senderInfo = new FriendInfo();
                senderInfo.setUsername(UserDao.user.getUsername());
                senderInfo.setAvatar(UserDao.user.getAvatar());
                chatMsg.setSenderInfo(senderInfo);

                FriendInfo receiverInfo = ContactUserDao.getContactUser(chatMsg.getReceiverId());
                chatMsg.setReceiverInfo(receiverInfo);
            }
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

    /**
     * 保存消息到数据库
     */
    public static void saveChatMsg(Context context, ChatMsg chatMsg, boolean isSend) {
        if (chatMsg == null || chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && chatMsg.getVoiceCall() != null
                && (chatMsg.getVoiceCall().getConnectState() == VoiceCall.INVITE
                || chatMsg.getVoiceCall().getConnectState() == VoiceCall.CONNECT
                || chatMsg.getVoiceCall().getConnectState() == VoiceCall.BUSY)) {
            return;
        }
        if (chatMsg.getVoiceCall()!=null){
            ChatMsg chatMsg1=MessageDao.getVoiceCall(chatMsg.getVoiceCall().getChannel());
            if (chatMsg1!=null){
                LogUtil.e("走入删除操作"+chatMsg1.getVoiceCall().getChannel());
                MessageDao.deleteVoiceChannel(chatMsg1.getVoiceCall().getChannel());
            }
        }
        MessageDao.addMessage(chatMsg);
        ChatRoom chatRoom;
        if (isSend) {
            MessageSendingDao.addMessage(chatMsg);
             chatRoom = ChatRoomManager.getChatRoom(chatMsg,false);
        }else {
             chatRoom = ChatRoomManager.getChatRoom(chatMsg,true);
        }

        if (chatRoom == null) {
            return;
        }
        if (ChatRoomManager.isUnreadNumAdd(context, chatMsg)) {
            chatRoom.setUnreadNum(chatRoom.getUnreadNum() + 1);
        }
        ChatRoomDao.addRoom(chatRoom);
        EventTrans.post(EventMsg.SOCKET_ON_MESSAGE, chatMsg);

        checkRoomNameAvailable(chatRoom);
    }

    /**
     * 修改发送的消息内容，专用于临时和好友的区分
     */
    public static ChatMsg changeSendMsgRoomType(@NonNull ChatMsg chatMsg, String id) {
        if (ChatRoom.TYPE_SINGLE.equals(chatMsg.getRoomType())) {
            FriendInfo friendInfo = FriendDao.getFriendInfo(id);
            //临时会话，单方面好友状况下，发生消息类型为好友类型
            if (friendInfo != null && friendInfo.getFriendship() == 0) {
                chatMsg.setFriendshipMark(1);
            }
        }
        return chatMsg;
    }

    /**
     * 修改接收的消息内容，专用于临时和好友的区分
     */
    private static ChatMsg changeReceiverMsgRoomType(@NonNull ChatMsg chatMsg) {
        //临时会话，单方面好友状况下，发生消息类型为好友类型
        FriendInfo friendInfo = FriendDao.getFriendInfo(chatMsg.getRoomId());
        if (friendInfo != null && friendInfo.getFriendship() == 0) {
            chatMsg.setFriendshipMark(0);
        }
        return chatMsg;
    }

    /**
     * 解析收到的聊天消息
     */
    public static void parseReceiveMsg(final Context context, ChatMsg receiveMsg) {
        if (receiveMsg == null) {
            return;
        }
        if (ChatRoom.TYPE_SINGLE.equals(receiveMsg.getRoomType())) {
            //senderId不等于自己的id，则为对方发送的消息
            if (!TextUtils.isEmpty(receiveMsg.getSenderId())
                    && !receiveMsg.getSenderId().equals(SpCons.getUser(context).getId())) {
                receiveMsg.setRoomId(receiveMsg.getSenderId());
                if (receiveMsg.getSenderInfo() != null) {
                    FriendInfo contactUser = ContactUserDao.getContactUser(receiveMsg.getSenderId());
                    if (contactUser == null) {
                        receiveMsg.getSenderInfo().setId(receiveMsg.getSenderId());
                        ContactUserDao.addContactUser(receiveMsg.getSenderInfo());
                    }
                }
                receiveMsg = changeReceiverMsgRoomType(receiveMsg);

            } else if (!TextUtils.isEmpty(receiveMsg.getReceiverId())
                    && !receiveMsg.getReceiverId().equals(SpCons.getUser(context).getId())) {
                //senderId等于自己的id，则为自己发送的消息
                receiveMsg.setRoomId(receiveMsg.getReceiverId());
                if (receiveMsg.getReceiverInfo() != null) {
                    receiveMsg.getReceiverInfo().setId(receiveMsg.getReceiverId());
                    ContactUserDao.addContactUser(receiveMsg.getReceiverInfo());
                }
                receiveMsg = changeReceiverMsgRoomType(receiveMsg);

            } else {
                return;
            }

        } else if (ChatRoom.TYPE_GROUP.equals(receiveMsg.getRoomType())) {
            receiveMsg.setRoomId(receiveMsg.getGroupId());
        }
        receiveMsg.setSendState(ChatMsg.TYPE_SEND_SUCCESS);
        receiveMsg.setLocalMsgId(UUIDUtil.get32UUID());

        switch (receiveMsg.getContentType()) {
            case ChatMsg.TYPE_CONTENT_TEXT:
            case ChatMsg.TYPE_CONTENT_AT:
                receiveMsg.setContent(receiveMsg.getContent().trim());
                if (EmoticonUtil.isContainEmotion(receiveMsg.getContent())) {
                    receiveMsg.setEmoMark(1);
                }
                if (HyperLinkUtil.isContainPhone(receiveMsg.getContent())) {
                    receiveMsg.setPhoneMark(1);
                }
                if (HyperLinkUtil.isContainUrl(receiveMsg.getContent())) {
                    receiveMsg.setUrlMark(1);
                }
                break;

            case ChatMsg.TYPE_CONTENT_FILE:
                if (receiveMsg.getFileInfo() != null) {
                    receiveMsg.getFileInfo().setOriginPath("");
                    receiveMsg.getFileInfo().setSavePath("");
                    receiveMsg.getFileInfo().setTapeUnreadMark(1);
                }
                break;

            case ChatMsg.TYPE_CONTENT_VOICE_CALL:
                //语音通话状态如果不为邀请或接通，则挂断
                final VoiceCall voiceCall = receiveMsg.getVoiceCall();
                if (voiceCall == null) {
                    return;
                }
                if (voiceCall.getConnectState() == VoiceCall.BUSY) {
                    //对方忙线状态则挂断，延时0.5秒再关闭通话页面
                    RxJavaScheduler.execute(new OnRxJavaProcessListener() {
                        @Override
                        public void process(ObservableEmitter<Object> emitter) {
                            VoiceCallManager.get().stop(context);
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new RxJavaObserver<Object>() {
                        @Override
                        public void onComplete() {
                            super.onComplete();
                            EventTrans.post(EventMsg.CONVERSATION_VOICE_CALL_FINISH,
                                    voiceCall.getConnectState());
                        }
                    });

                } else if (VoiceCallManager.get().isRunning()
                        && !TextUtils.isEmpty(voiceCall.getInviteUserId())
                        && VoiceCallManager.get().getVoiceCall() != null
                        && !voiceCall.getInviteUserId().equals(VoiceCallManager.get()
                        .getVoiceCall().getInviteUserId())) {
                    ChatRoom chatRoom = new ChatRoom(receiveMsg.getRoomId(), receiveMsg.getRoomType());
                    voiceCall.setConnectState(VoiceCall.BUSY);
                    ChatSocket.getInstance().send(packVoiceCallMsg(chatRoom, voiceCall));

                } else if (voiceCall.getConnectState() == VoiceCall.INVITE) {
                    ChatRoom chatRoom = new ChatRoom(receiveMsg.getRoomId(), receiveMsg.getRoomType());
                    chatRoom.setLastChatMsg(receiveMsg);
                    VoiceCallManager.get().setVoiceCall(receiveMsg.getVoiceCall());
                    VoiceCallManager.get().startVoiceCallWaitActivity(context, chatRoom);
                    VoiceCallManager.get().startWaitTimeCount();

                } else {
                    VoiceCallManager.get().setVoiceCall(receiveMsg.getVoiceCall());
                    RingVibrateManager.getInstance().stop();
                    if (voiceCall.getConnectState() == VoiceCall.CONNECT) {
                        VoiceCallManager.get().joinChannel(context, voiceCall.getChannel());
                        VoiceCallManager.get().setCallType(VoiceCallManager.TYPE_CALL_CALLING);
                        VoiceCallManager.get().cancelWaitTimeCount();
                        VoiceCallManager.get().startCallTimeCount();
                    } else {
                        VoiceCallManager.get().stop(context);
                    }
                    EventTrans.post(EventMsg.MESSAGE_VOICE_CALL_REFRESH, voiceCall.getConnectState());
                }
                break;

            default:
                break;
        }
        saveChatMsg(context, receiveMsg, false);
    }

    /**
     * 非语音通话&&消息发送者非自己&&（（栈顶!=聊天室）||（栈顶==聊天室&&聊天室roomId !=该条消息的roomId））
     * or语音通话&&timeout/cancel&&inviteId非自己&&（（栈顶!=聊天室）||（栈顶==聊天室&&聊天室roomId !=该条消息的roomId））
     * 则未读数量+1
     */
    private static boolean isUnreadNumAdd(Context context, ChatMsg receiveMsg) {
        if (receiveMsg != null && receiveMsg.getContentType() != ChatMsg.TYPE_CONTENT_VOICE_CALL
                && !SpCons.getUser(context).getId().equals(receiveMsg.getSenderId())) {
            String stackTopActivityName = ClassUtil.getStackTopActivity(context);
            if (!TextUtils.isEmpty(stackTopActivityName)) {
                if (!stackTopActivityName.contains("ChatRoomActivity")) {
                    return true;
                } else {
                    return !receiveMsg.getRoomId().equals(currentRoomId)
                            || !receiveMsg.getRoomType().equals(currentRoomType);
                }
            }
        } else if (receiveMsg != null && receiveMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && receiveMsg.getVoiceCall() != null
                && (receiveMsg.getVoiceCall().getConnectState() == VoiceCall.CANCEL
                || receiveMsg.getVoiceCall().getConnectState() == VoiceCall.TIME_OUT)
                && !receiveMsg.getVoiceCall().getInviteUserId().equals(SpCons.getUser(context).getId())) {

            String stackTopActivityName = ClassUtil.getStackTopActivity(context);
            if (!TextUtils.isEmpty(stackTopActivityName)) {
                if (!stackTopActivityName.contains("ChatRoomActivity")) {
                    return true;
                } else {
                    return !receiveMsg.getRoomId().equals(currentRoomId)
                            || !receiveMsg.getRoomType().equals(currentRoomType);
                }
            }
        }
        return false;
    }

    /**
     * 聊天消息的文件发送
     */
    public static void uploadFile(final Context context, ChatRoom chatRoom, FileInfo fileInfo) {
        final ChatMsg chatMsg = ChatRoomManager.packFileMsg(chatRoom, fileInfo);
        if (chatMsg.getFileInfo() == null) {
            return;
        }
        ChatSocket.getInstance().send(chatMsg);
        String url=chatMsg.getFileInfo().getOriginPath();
        if (fileInfo.getFileType()==FileInfo.TYPE_IMG){
            url=chatMsg.getFileInfo().getCompressPath();
        }
        HttpContact.uploadFile(url, chatMsg.getFileInfo().getFileType(),
                chatRoom.getRoomId(), chatRoom.getRoomType(), fileInfo.getDuration(), new HttpCallBack(context, ClassUtil.classMethodName()) {
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

    /**
     * 判断构造出的ChatRoom是否有name，如果没有则调接口获取
     */
    private static void checkRoomNameAvailable(@NonNull ChatRoom chatRoom) {
        if (ChatRoom.TYPE_SINGLE.equals(chatRoom.getRoomType())) {
            FriendInfo contactUser = ContactUserDao.getContactUser(chatRoom.getRoomId());
            if (contactUser == null || TextUtils.isEmpty(contactUser.getUsername())) {
                getFriendInfo(chatRoom.getRoomId());
            }

        } else if (ChatRoom.TYPE_GROUP.equals(chatRoom.getRoomType())) {
            GroupInfo groupInfo = GroupDao.getGroup(chatRoom.getRoomId());
            if (groupInfo == null || TextUtils.isEmpty(groupInfo.getGroupName())) {
                getGroupInfo(chatRoom.getRoomId());
            }
        }
    }

    /**
     * 构造ChatRoom时，如果ChatRoom没有name，则通过id调接口获取个人详细信息
     */
    private static void getFriendInfo(final String friendId) {
        HttpContact.getFriendInfo(friendId,
                new HttpCallBack(ClassUtil.classMethodName()) {
                    @Override
                    public void successOnChildThread(Object data, int count, int pages) {
                        if (data == null) {
                            return;
                        }
                        try {
                            FriendInfo friendInfo = JSON.parseObject(data.toString(), FriendInfo.class);
                            if (friendInfo == null) {
                                return;
                            }
                            if (friendInfo.getFriendship() == 1) {
                                Subgroup subgroup = new Subgroup();
                                subgroup.setId(friendInfo.getGroupId());
                                subgroup.setGroupName(friendInfo.getGroupName());
                                subgroup.setIsDefault(friendInfo.getFlag());
                                FriendDao.addFriend(friendInfo, subgroup);
                                ContactUserDao.addContactUser(friendInfo);
                            } else {
                                ContactUserDao.addContactUser(friendInfo);
                            }

                            //切到主线程
                            RxJavaScheduler.execute(new OnRxJavaProcessListener() {
                                @Override
                                public void process(ObservableEmitter<Object> emitter) {

                                }
                            }, new RxJavaObserver<Object>() {
                                @Override
                                public void onComplete() {
                                    EventTrans.post(EventMsg.CONTACT_FRIEND_NAME_REFRESH, friendId);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 构造ChatRoom时，如果ChatRoom没有name，则通过id调接口获取群组详细信息
     */
    private static void getGroupInfo(String groupId) {
        HttpContact.getGroupInfo(groupId,
                new HttpCallBack(ClassUtil.classMethodName()) {
                    @Override
                    public void successOnChildThread(Object data, int count, int pages) {
                        if (data == null) {
                            return;
                        }
                        try {
                            final GroupInfo groupInfo = JSON.parseObject(data.toString(), GroupInfo.class);
                            if (groupInfo == null) {
                                return;
                            }
                            GroupDao.addGroup(groupInfo);

                            //切到主线程
                            RxJavaScheduler.execute(new OnRxJavaProcessListener() {
                                @Override
                                public void process(ObservableEmitter<Object> emitter) {

                                }
                            }, new RxJavaObserver<Object>() {
                                @Override
                                public void onComplete() {
                                    EventTrans.post(EventMsg.CONTACT_GROUP_INFO_REFRESH, groupInfo);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 同步服务器会话列表
     */
    public static void syncServerConversationList() {
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
                new HttpCallBack(ClassUtil.classMethodName()) {
                    @Override
                    public void successOnChildThread(Object data, int count, int pages) {
                        if (data == null) {
                            return;
                        }
                        try {
                            ServerConversation serverConversation = JSON.parseObject(data.toString(),
                                    ServerConversation.class);
                            if (serverConversation != null) {
                                parseServerConversationResult(serverConversation);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 解析同步服务器会话列表结果
     */
    private static void parseServerConversationResult(@NonNull ServerConversation serverConversation) {
        List<ServerConversation.DeleteConversation> deleteList = serverConversation.getDeleteConversations();
        List<ServerConversation.UpdateConversation> updateList = serverConversation.getUpdateConversations();
        if (deleteList != null && !deleteList.isEmpty()) {
            for (ServerConversation.DeleteConversation deleteConversation : deleteList) {
                MessageDao.updateShowMark(deleteConversation.getConversationId(),
                        deleteConversation.getConversationType());
                ChatRoomDao.deleteRoom(deleteConversation.getConversationId(),
                        deleteConversation.getConversationType());
            }
        }
        if (updateList != null && !updateList.isEmpty()) {
            final List<ChatRoom> updateRoomList = EntityUtil.parseUpdateConversation(updateList);
            for (ChatRoom room : updateRoomList) {
                if (!TextUtils.isEmpty(room.getUnSyncMsgFirstId())
                        && !"0".equals(room.getUnSyncMsgFirstId()) && room.getLastChatMsg() != null) {
                    room.getLastChatMsg().setUnSyncMsgFirstId(room.getUnSyncMsgFirstId());
                    if (room.getLastChatMsg().getContentType() == ChatMsg.TYPE_CONTENT_TEXT
                            || room.getLastChatMsg().getContentType() == ChatMsg.TYPE_CONTENT_AT) {
                        room.getLastChatMsg().setContent(room.getLastChatMsg().getContent().trim());
                        if (EmoticonUtil.isContainEmotion(room.getLastChatMsg().getContent())) {
                            room.getLastChatMsg().setEmoMark(1);
                        }
                        if (HyperLinkUtil.isContainPhone(room.getLastChatMsg().getContent())) {
                            room.getLastChatMsg().setPhoneMark(1);
                        }
                        if (HyperLinkUtil.isContainUrl(room.getLastChatMsg().getContent())) {
                            room.getLastChatMsg().setUrlMark(1);
                        }
                    }
                }
                MessageDao.addMessage(room.getLastChatMsg());
                checkRoomNameAvailable(room);
            }
            ChatRoomDao.addRoomList(updateRoomList);

            //切到主线程
            RxJavaScheduler.execute(new OnRxJavaProcessListener() {
                @Override
                public void process(ObservableEmitter<Object> emitter) {

                }
            }, new RxJavaObserver<Object>() {
                @Override
                public void onComplete() {
                    EventTrans.post(EventMsg.CONVERSATION_SYNC_SERVER_LIST, updateRoomList);
                }
            });
        }
    }

}
