package com.wewin.hichat.model.socket;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.manage.RingVibrateManager;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.threadpool.CustomThreadPool;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.CommonUtil;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.androidlib.utils.MyLifecycleHandler;
import com.wewin.hichat.androidlib.utils.NotificationUtil;
import com.wewin.hichat.component.constant.ContactCons;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.manager.ChatRoomManager;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.component.constant.HttpCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.MessageSendingDao;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;
import com.wewin.hichat.model.db.entity.Heart;
import com.wewin.hichat.model.db.entity.LoginUser;
import com.wewin.hichat.model.db.entity.SocketReceive;
import com.wewin.hichat.model.db.entity.SocketServer;
import com.wewin.hichat.model.db.entity.SocketServer.ServerData;
import com.wewin.hichat.model.db.entity.Subgroup;
import com.wewin.hichat.view.conversation.ChatRoomActivity;
import com.wewin.hichat.view.login.LoginActivity;

import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


/**
 * @author Darren
 * Created by Darren on 2018/12/21.
 */
public class ChatSocket {

    private static ChatSocket chatSocket;
    private WebSocket okHttpWebSocket;
    private Handler handler = new Handler();
    private OkHttpClient okHttpClient;
    private Request request;
    private final int INTERVAL_HEART = 5 * 1000;//心跳间隔
    private final int CODE_CLOSE_NORMALLY = 1001;
    private String heartBack = "";
    private long heartSendTimestamp = 0;
    private boolean connectState = false;//socket连接状态
    /**
     * 用于检测短时间内有没有收到消息，避免handler无限循环发送ping失败
     */
    private boolean receipt = false;
    private NotificationUtil notificationUtil;
    private final int TYPE_RECONNECT_NOT = 0;//未进行重连
    private final int TYPE_RECONNECTING = 1;//重连中
    private final int TYPE_RECONNECT_SUCCESS = 2;//重连成功
    private final int TYPE_RECONNECT_FAILURE = 3;//重连失败
    private int reconnectType = TYPE_RECONNECT_NOT;//0非重连；1重连中；2重连成功；3重连失败；
    private Context mContext;

    private ChatSocket() {
    }

    public static ChatSocket getInstance() {
        if (chatSocket == null) {
            synchronized (ChatSocket.class) {
                if (chatSocket == null) {
                    chatSocket = new ChatSocket();
                }
            }
        }
        return chatSocket;
    }

    public void init(Context context) {
        this.mContext = context;
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.retryOnConnectionFailure(false);
            SSLSocketFactory socketFactory = HttpUtil.getSSLSocketFactory(HttpCons.HTTPS_CER);
            if (socketFactory != null) {
                builder.sslSocketFactory(socketFactory, new HttpUtil.TrustAllCerts());
            }
            okHttpClient = builder.build();
        }
        if (notificationUtil == null) {
            notificationUtil = new NotificationUtil();
            notificationUtil.setNotification(context);
        }
        String userId = SpCons.getUser(mContext).getId();
        String cuid = SpCons.getCuid(mContext);
        String socketPath = HttpCons.SOCKET_IM + "?sessionId=" + userId + "&terminal=ANDROID&cuid=" + cuid;
        request = new Request.Builder().url(socketPath).build();
        connect();
    }

    public void send(ChatMsg chatMsg) {
        String msgJsonStr;
        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_FILE) {
            if (chatMsg.getFileInfo() == null) {
                return;
            }
            if (TextUtils.isEmpty(chatMsg.getFileInfo().getDownloadPath())) {
                ChatRoomManager.saveChatMsg(mContext, chatMsg, true);
            } else if (okHttpWebSocket != null) {
                ChatMsg msg = ChatRoomManager.changeSendMsgRoomType(chatMsg, chatMsg.getRoomId());
                msgJsonStr = JSON.toJSONString(msg);
                okHttpWebSocket.send(ByteString.encodeUtf8(msgJsonStr));
                LogUtil.i("send jsonStr", msgJsonStr);
            }
        } else {
            ChatRoomManager.saveChatMsg(mContext, chatMsg, true);
            if (okHttpWebSocket != null) {
                ChatMsg msg = ChatRoomManager.changeSendMsgRoomType(chatMsg, chatMsg.getRoomId());
                msgJsonStr = JSON.toJSONString(msg);
                okHttpWebSocket.send(ByteString.encodeUtf8(msgJsonStr));
                LogUtil.i("send jsonStr", msgJsonStr);
            }
        }
    }

    public void reconnect() {
        if (okHttpWebSocket != null) {
            okHttpWebSocket.close(CODE_CLOSE_NORMALLY, "stop");
            okHttpWebSocket = null;
            okHttpClient = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        init(mContext);
        reconnectType = TYPE_RECONNECTING;
    }

    public boolean isConnectState() {
        return connectState;
    }

    public boolean isReceipt() {
        return receipt;
    }

    public void setReceipt(boolean state) {
        receipt = state;
    }

    public void setConnectState(boolean state) {
        connectState = state;
    }

    public void stop() {
        if (okHttpWebSocket != null) {
            okHttpWebSocket.close(CODE_CLOSE_NORMALLY, "stop");
            okHttpWebSocket = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        connectState = false;
    }

    /**
     * 建立连接
     */
    private void connect() {
        okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LogUtil.i("webSocket onOpen");
                connectState = true;
                okHttpWebSocket = webSocket;
                sendHeart();
                resendMsgList();
                //socket重连成功后，同步服务器会话列表
                if (reconnectType == TYPE_RECONNECTING) {
                    reconnectType = TYPE_RECONNECT_SUCCESS;
                    ChatRoomManager.syncServerConversationList();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, final ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (bytes == null) {
                    LogUtil.i("webSocket onMessage bytes == null");
                    return;
                }
                try {
                    String backDataStr = bytes.utf8();
                    Heart heart = JSON.parseObject(backDataStr, Heart.class);
                    if (heart != null && !TextUtils.isEmpty(heart.getValue())) {
                        heartBack = heart.getValue();
                        long serverTimestamp = heart.getServerTimestamp();
                        long diffTimestamp = SystemClock.elapsedRealtime() - heartSendTimestamp;
                        TimeUtil.initTime(serverTimestamp + diffTimestamp);

                    } else {
                        LogUtil.i("webSocket onMessage", backDataStr);
                        SocketReceive socketReceive = JSON.parseObject(backDataStr,
                                SocketReceive.class);
                        if (socketReceive == null) {
                            return;
                        }
                        processReceivedMsg(socketReceive);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                if (code != CODE_CLOSE_NORMALLY) {
                    connectState = false;
                    reconnectType = TYPE_RECONNECT_FAILURE;
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                LogUtil.i("webSocket onClosed", "code: " + code + ", reason: " + reason);
                if (code != CODE_CLOSE_NORMALLY) {
                    connectState = false;
                    reconnectType = TYPE_RECONNECT_FAILURE;
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                LogUtil.i("webSocket onFailure", "t: " + t);
                connectState = false;
                reconnectType = TYPE_RECONNECT_FAILURE;
            }
        });
    }

    /**
     * 心跳
     */
    private void sendHeart() {
        connectState = true;
        String jsonStr = JSON.toJSONString(new Heart("ping"));
        if (okHttpWebSocket != null) {
            okHttpWebSocket.send(ByteString.encodeUtf8(jsonStr));
            heartSendTimestamp = SystemClock.elapsedRealtime();
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                receipt = true;
                connectState = "pong".equals(heartBack);
                heartBack = "";
                String jsonStr = JSON.toJSONString(new Heart("ping"));
                if (okHttpWebSocket != null) {
                    okHttpWebSocket.send(ByteString.encodeUtf8(jsonStr));
                    heartSendTimestamp = SystemClock.elapsedRealtime();
                }
                handler.postDelayed(this, INTERVAL_HEART);
            }
        }, INTERVAL_HEART);
    }

    /**
     * socket连上后将MessageSendingDao中的消息重新发送
     */
    private void resendMsgList() {
        if (MessageSendingDao.getCount() <= 0 || okHttpWebSocket == null) {
            return;
        }
        List<ChatMsg> sendingMsgList = MessageSendingDao.getMessageList();
        for (final ChatMsg msg : sendingMsgList) {
            if (msg.getContentType()==ChatMsg.TYPE_CONTENT_FILE){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ChatRoomManager.uploadFile(mContext,msg,msg.getFileInfo());
                    }
                });
            }else {
                String msgJsonStr = JSON.toJSONString(msg);
                okHttpWebSocket.send(ByteString.encodeUtf8(msgJsonStr));
            }
        }
        MessageSendingDao.clear();
    }

    /**
     * 收到socket消息进行处理
     */
    private void processReceivedMsg(SocketReceive socketReceive) {
        if ("remote".equals(socketReceive.getType())) {
            ChatMsg receiveMsg = socketReceive.getRemote();
            if (receiveMsg == null) {
                return;
            }
            ChatRoomManager.parseReceiveMsg(mContext, receiveMsg);
            if (!MyLifecycleHandler.isApplicationVisible()) {
                //后台情况
                setNotificationUtil(receiveMsg);
            }
            //响铃和振动
            startRingVibrate(receiveMsg);

        } else if ("server".equals(socketReceive.getType())) {
            SocketServer socketServer = socketReceive.getServer();
            if (socketServer == null || socketServer.getData() == null) {
                return;
            }
            ServerData serverData = socketServer.getData();
            switch (socketServer.getAction()) {
                //聊天消息回执
                case "messageReceipt":
                    MessageDao.updateMsgId(serverData.getLocalMsgId(), serverData.getMsgId());
                    MessageSendingDao.deleteMessage(serverData.getLocalMsgId());
                    EventTrans.post(EventMsg.CONVERSATION_CHAT_REFRESH,serverData.getLocalMsgId(),serverData.getMsgId());
                    break;

                //同步用户信息
                case "syncAccountInfo":
                    syncAccountInfo(serverData);
                    break;

                //同步好友相关操作
                case "syncFriendCommand":
                    syncFriendCommand(serverData);
                    break;

                //同步消息相关操作
                case "syncMessageCommand":
                    syncMessageCommand(serverData);
                    break;

                //同步群组信息
                case "syncGroupInfo":
                    syncGroupInfo(serverData);
                    break;

                //同步群组相关操作
                case "syncGroupCommand":
                    syncGroupCommand(serverData);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 同步用户信息
     */
    private void syncAccountInfo(@NonNull ServerData serverData) {
        String friendId = serverData.getFromUid();
        LoginUser userInfo = serverData.getUserInfo();
        if (TextUtils.isEmpty(friendId) || userInfo == null) {
            return;
        }
        switch (serverData.getCmd()) {
            //1
            case "updAvatar":
                if (TextUtils.isEmpty(userInfo.getAvatar())) {
                    return;
                }
                FriendDao.updateAvatar(friendId, userInfo.getAvatar());
                ContactUserDao.updateAvatar(friendId, userInfo.getAvatar());
                EventTrans.post(EventMsg.CONTACT_FRIEND_AVATAR_REFRESH, friendId);
                break;

            //1
            case "updGender":
                FriendDao.updateGender(friendId, userInfo.getGender());
                EventTrans.post(EventMsg.CONTACT_FRIEND_INFO_REFRESH, friendId);
                break;

            //1
            case "updSign":
                FriendDao.updateSign(friendId, userInfo.getSign());
                EventTrans.post(EventMsg.CONTACT_FRIEND_INFO_REFRESH, friendId);
                break;

            //1
            case "updUsername":
                FriendDao.updateName(friendId, userInfo.getUsername());
                ContactUserDao.updateName(friendId, userInfo.getUsername());
                EventTrans.post(EventMsg.CONTACT_FRIEND_NAME_REFRESH, friendId);
                break;

            default:
                break;
        }
    }

    /**
     * 同步好友相关操作
     */
    private void syncFriendCommand(@NonNull ServerData serverData) {
        String friendId = serverData.getFromUid();
        if (TextUtils.isEmpty(friendId)) {
            return;
        }
        switch (serverData.getCmd()) {
            //1
            case "delFriend":
                MessageDao.deleteRoomMsg(friendId, ChatRoom.TYPE_SINGLE);
                ChatRoomDao.deleteRoom(friendId, ChatRoom.TYPE_SINGLE);
                FriendDao.deleteFriend(friendId);
                ContactUserDao.deleteFriend(friendId);
                EventTrans.post(EventMsg.CONTACT_FRIEND_DELETE_REFRESH, friendId);
                break;

            case "updFriendNote":
                SocketServer.FriendData friendData = serverData.getFriendData();
                if (friendData == null) {
                    return;
                }
                FriendDao.updateNote(friendId, friendData.getFriendNote());
                ContactUserDao.updateNote(friendId, friendData.getFriendNote());
                EventTrans.post(EventMsg.CONTACT_FRIEND_NOTE_REFRESH, friendId);
                break;

            case "delSubgroup":
                SocketServer.FriendData friendData1 = serverData.getFriendData();
                if (friendData1 == null) {
                    return;
                }
                EventTrans.post(EventMsg.CONTACT_FRIEND_SUBGROUP_REFRESH);
                break;

            case "blackFriend":
                EventTrans.post(EventMsg.CONTACT_FRIEND_BLACK_REFRESH, friendId);
                break;

            case "pushTyping":

                break;

            case "addSubgroup":
                EventTrans.post(EventMsg.CONTACT_FRIEND_LIST_REFRESH);
                break;

            case "moveSubgroup":
                EventTrans.post(EventMsg.CONTACT_FRIEND_LIST_REFRESH);
                break;

            case "renameSubgroup":
                EventTrans.post(EventMsg.CONTACT_FRIEND_LIST_REFRESH);
                break;

            case "agreeFriend":
                FriendInfo friend = serverData.getFriendInfo();
                SocketServer.FriendData friendData2 = serverData.getFriendData();
                if (friend != null && friendData2 != null) {
                    friend.setFriendship(1);
                    Subgroup subgroup = new Subgroup();
                    friend.setId(friendData2.getFriendId());
                    subgroup.setId(friendData2.getSubgroupId());
                    subgroup.setGroupName(friendData2.getSubgroupName());
                    FriendDao.addFriend(friend, subgroup);
                    ContactUserDao.addContactUser(friend);
                    EventTrans.post(EventMsg.CONTACT_FRIEND_AGREE_REFRESH, friendId);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 同步消息相关操作
     */
    private void syncMessageCommand(@NonNull ServerData serverData) {
        SocketServer.MessageInfo messageInfo = serverData.getMessageInfo();
        switch (serverData.getCmd()) {
            case "removeConversation":
                if (messageInfo == null) {
                    return;
                }
                if (ChatRoom.TYPE_SINGLE.equals(messageInfo.getConversationType())) {
                    MessageDao.deleteRoomMsg(messageInfo.getConversationId(), ChatRoom.TYPE_SINGLE);
                    ChatRoomDao.deleteRoom(messageInfo.getConversationId(), ChatRoom.TYPE_SINGLE);
                    EventTrans.post(EventMsg.CONVERSATION_DELETE_ROOM, messageInfo.getConversationId(),
                            ChatRoom.TYPE_SINGLE);

                } else if (ChatRoom.TYPE_GROUP.equals(messageInfo.getConversationType())) {
                    MessageDao.deleteRoomMsg(messageInfo.getConversationId(), ChatRoom.TYPE_GROUP);
                    ChatRoomDao.deleteRoom(messageInfo.getConversationId(), ChatRoom.TYPE_GROUP);
                    EventTrans.post(EventMsg.CONVERSATION_DELETE_ROOM, messageInfo.getConversationId(),
                            ChatRoom.TYPE_GROUP);
                }
                break;

            case "shieldConversation":
                if (messageInfo == null) {
                    return;
                }
                if (ChatRoom.TYPE_SINGLE.equals(messageInfo.getConversationType())) {
                    FriendDao.updateShieldMark(messageInfo.getConversationId(), messageInfo.getShieldMark());
                    ChatRoomDao.updateShieldMark(messageInfo.getConversationId(), ChatRoom.TYPE_SINGLE,
                            messageInfo.getShieldMark());
                    EventTrans.post(EventMsg.CONTACT_FRIEND_SHIELD_REFRESH,
                            messageInfo.getConversationId(), messageInfo.getShieldMark());

                } else if (ChatRoom.TYPE_GROUP.equals(messageInfo.getConversationType())) {
                    GroupDao.updateShieldMark(messageInfo.getConversationId(), messageInfo.getShieldMark());
                    ChatRoomDao.updateShieldMark(messageInfo.getConversationId(), ChatRoom.TYPE_GROUP,
                            messageInfo.getShieldMark());
                    EventTrans.post(EventMsg.CONTACT_GROUP_SHIELD_REFRESH,
                            messageInfo.getConversationId(), messageInfo.getShieldMark());
                }
                break;

            case "topConversation":
                if (messageInfo == null) {
                    return;
                }
                if (ChatRoom.TYPE_SINGLE.equals(messageInfo.getConversationType())) {
                    FriendDao.updateTopMark(messageInfo.getConversationId(), messageInfo.getTopMark());
                    ChatRoomDao.updateTopMark(messageInfo.getConversationId(),
                            messageInfo.getConversationType(), messageInfo.getTopMark());
                    EventTrans.post(EventMsg.CONTACT_FRIEND_MAKE_TOP_REFRESH,
                            messageInfo.getConversationId(), messageInfo.getTopMark());

                } else if (ChatRoom.TYPE_GROUP.equals(messageInfo.getConversationType())) {
                    GroupDao.updateTopMark(messageInfo.getConversationId(), messageInfo.getTopMark());
                    ChatRoomDao.updateTopMark(messageInfo.getConversationId(),
                            messageInfo.getConversationType(), messageInfo.getTopMark());
                    EventTrans.post(EventMsg.CONTACT_GROUP_MAKE_TOP_REFRESH,
                            messageInfo.getConversationId(), messageInfo.getTopMark());
                }
                break;

            case "clearSessionList":
                MessageDao.deleteAllMsg();
                ChatRoomDao.deleteAllRoom();
                EventTrans.post(EventMsg.CONVERSATION_DELETE_ALL_ROOM);
                break;

            case "chatMsgRead":
                if (messageInfo == null) {
                    return;
                }
                EventTrans.post(EventMsg.CONVERSATION_UNREAD_NUM_REFRESH, messageInfo.getConversationId(),
                        messageInfo.getConversationType());
                break;

            case "runMsgBox":
                SpCons.setNotifyRedPointVisible(mContext, true);
                EventTrans.post(EventMsg.CONTACT_NOTIFY_REFRESH, serverData.getNotifyInfo());
                break;

            case "closeMsgBox":
                SpCons.setNotifyRedPointVisible(mContext, false);
                EventTrans.post(EventMsg.CONTACT_NOTIFY_REFRESH);
                break;

            case "expireLogin":
                if (messageInfo == null) {
                    return;
                }
                serverCookieInvalid(messageInfo.getExpireMessage());
                break;
            case "removeMessage":
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.setMsgId(messageInfo.getMsgId());
                chatMsg.setRoomId(messageInfo.getConversationId());
                chatMsg.setRoomType(messageInfo.getConversationType());
                if (MessageDao.findMsg(chatMsg.getMsgId())) {
                    MessageDao.deleteSingle(chatMsg.getMsgId());
                }
                ChatMsg latestMessage = messageInfo.getLatestMessage();
                //添加最后一条消息
                if(latestMessage!=null){
                    latestMessage.setRoomId(messageInfo.getConversationId());
                    latestMessage.setRoomType(messageInfo.getConversationType());
                    MessageDao.addMessage(latestMessage);
                }
                ChatRoom room = ChatRoomDao.getRoom(chatMsg.getRoomId(), chatMsg.getRoomType());
                if (messageInfo.getIsRead() == 0 && room != null) {
                    if (room.getUnreadNum() > 0) {
                        room.setUnreadNum(room.getUnreadNum() - 1);
                    }
                    if (room.getUnreadNum() <= 0) {
                        room.setAtType(ChatMsg.TYPE_AT_NORMAL);
                    }
                    ChatRoomDao.addRoom(room);
                }
                EventTrans.post(EventMsg.CONVERSATION_DELETE_MSG, chatMsg);
                break;
            default:
                break;
        }
    }

    /**
     * 同步群组信息
     */
    private void syncGroupInfo(@NonNull ServerData serverData) {
        String groupId = serverData.getFromGroupId();
        SocketServer.GroupData groupData = serverData.getGroupInfo();
        if (TextUtils.isEmpty(groupId)) {
            return;
        }
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(groupId);
        switch (serverData.getCmd()) {
            case "createGroup":
                EventTrans.post(EventMsg.CONTACT_GROUP_CREATE_REFRESH);
                break;

            //1
            case "updAvatar":
                if (groupData == null || TextUtils.isEmpty(groupData.getAvatar())) {
                    return;
                }
                groupInfo.setGroupAvatar(groupData.getAvatar());
                GroupDao.updateAvatar(groupId, groupData.getAvatar());
                EventTrans.post(EventMsg.CONTACT_GROUP_AVATAR_REFRESH, groupInfo);
                break;

            //1
            case "updDesc":
                if (groupData == null || TextUtils.isEmpty(groupData.getDescription())) {
                    return;
                }
                groupInfo.setDescription(groupData.getDescription());
                GroupDao.updateDesc(groupId, groupData.getDescription());
                EventTrans.post(EventMsg.CONTACT_GROUP_INFO_REFRESH, groupInfo);
                break;

            //1
            case "updGroupName":
                if (groupData == null || TextUtils.isEmpty(groupData.getGroupName())) {
                    return;
                }
                groupInfo.setGroupName(groupData.getGroupName());
                GroupDao.updateName(groupId, groupData.getGroupName());
                EventTrans.post(EventMsg.CONTACT_GROUP_INFO_REFRESH, groupInfo);
                break;

            //1
            case "updGroupPermission":
                if (groupData == null) {
                    return;
                }
                groupInfo.setAddFriendMark(groupData.getAddFriendMark());
                groupInfo.setInviteFlag(groupData.getInviteMark());
                groupInfo.setSearchFlag(groupData.getSearchMark());
                groupInfo.setGroupSpeak(groupData.getSpeechMark());
                groupInfo.setGroupValid(groupData.getVerifyMark());
                GroupDao.updatePermission(groupInfo);
                EventTrans.post(EventMsg.CONTACT_GROUP_PERMISSION_REFRESH, groupInfo);
                break;

            default:
                break;
        }
    }

    /**
     * 同步群组相关操作
     */
    private void syncGroupCommand(@NonNull ServerData serverData) {
        String groupId = serverData.getFromGroupId();
        if (TextUtils.isEmpty(groupId)) {
            return;
        }
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(groupId);
        FriendInfo executor = serverData.getExecutorInfo();
        if (executor != null) {
            executor.setId(serverData.getExecutorId());
        }
        List<FriendInfo> receiverList = serverData.getRecipientInfo();

        switch (serverData.getCmd()) {
            case "agreeGroup":
                if (executor == null || receiverList == null || receiverList.isEmpty()) {
                    return;
                }
                ContactUserDao.addContactUserList(receiverList);
                GroupMemberDao.addGroupMemberList(groupId, receiverList);
                EventTrans.post(EventMsg.CONTACT_GROUP_AGREE_JOIN, groupInfo, executor,
                        receiverList.get(0));
                break;

            //1
            case "inviteFriendJoin":
                if (receiverList == null || receiverList.isEmpty()) {
                    return;
                }
                ContactUserDao.addContactUserList(receiverList);
                GroupMemberDao.addGroupMemberList(groupId, receiverList);
                EventTrans.post(EventMsg.CONTACT_GROUP_INVITE_MEMBER_REFRESH, receiverList);
                break;

            case "addGroupPost":
                EventTrans.post(EventMsg.CONVERSATION_ADD_GROUP_POST, groupId, serverData.getGroupPostInfo().getPostTitle());
                break;

            case "delGroupPost":

                break;

            case "modifyGroupPost":

                break;

            //1
            case "giveAdmin":
                if (executor == null || receiverList == null || receiverList.isEmpty()) {
                    return;
                }
                groupInfo.setGrade(GroupInfo.TYPE_GRADE_MANAGER);
                GroupDao.updateGroupGrade(groupId, GroupInfo.TYPE_GRADE_MANAGER);
                EventTrans.post(EventMsg.CONTACT_GROUP_SET_MANAGER, groupInfo, executor,
                        receiverList.get(0));
                break;

            //1
            case "cancelAdmin":
                if (executor == null || receiverList == null || receiverList.isEmpty()) {
                    return;
                }
                groupInfo.setGrade(GroupInfo.TYPE_GRADE_NORMAL);
                GroupDao.updateGroupGrade(groupId, GroupInfo.TYPE_GRADE_NORMAL);
                EventTrans.post(EventMsg.CONTACT_GROUP_SET_MANAGER, groupInfo, executor,
                        receiverList.get(0));
                break;

            //1
            case "expelMember":
                if (executor == null || receiverList == null || receiverList.isEmpty()) {
                    return;
                }
                if (SpCons.getUser(mContext).getId().equals(receiverList.get(0).getId())) {
                    GroupDao.deleteGroup(groupId);
                    ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                    MessageDao.deleteRoomMsg(groupId, ChatRoom.TYPE_GROUP);
                }
                EventTrans.post(EventMsg.CONTACT_GROUP_REMOVE_MEMBER, groupInfo, executor,
                        receiverList.get(0));
                break;

            //1
            case "quitGroup":
                if (executor == null) {
                    return;
                }
                if (SpCons.getUser(mContext).getId().equals(executor.getId())) {
                    GroupDao.deleteGroup(groupId);
                    ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                    MessageDao.deleteRoomMsg(groupId, ChatRoom.TYPE_GROUP);
                }
                EventTrans.post(EventMsg.CONTACT_GROUP_QUIT, groupId, executor.getId());
                break;

            case "dismissGroup":
                GroupDao.deleteGroup(groupId);
                ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                MessageDao.deleteRoomMsg(groupId, ChatRoom.TYPE_GROUP);
                EventTrans.post(EventMsg.CONTACT_GROUP_DISBAND, groupId);
                break;

            default:
                break;
        }
    }

    /**
     * 接收到消息是否响铃/震动
     */
    private void startRingVibrate(@NonNull final ChatMsg chatMsg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL) {
                    return;
                }
                //未被屏蔽的好友/群/临时会话才会响铃震动
                if (ChatRoom.TYPE_SINGLE.equals(chatMsg.getRoomType())) {
                    FriendInfo friendInfo = FriendDao.getFriendInfo(chatMsg.getRoomId());
                    if (friendInfo == null) {
                        FriendInfo contactUser = ContactUserDao.getContactUser(chatMsg.getRoomId());
                        if (contactUser != null && contactUser.getShieldMark() == 0) {
                            if (SpCons.getUser(mContext).getAudioCues() == 1) {
                                RingVibrateManager.getInstance().playSmsRingtone(mContext);
                            }
                            if (SpCons.getUser(mContext).getVibratesCues() == 1) {
                                RingVibrateManager.getInstance().vibrate(mContext);
                            }
                        }

                    } else if (friendInfo.getShieldMark() == 0) {
                        if (SpCons.getUser(mContext).getAudioCues() == 1) {
                            RingVibrateManager.getInstance().playSmsRingtone(mContext);
                        }
                        if (SpCons.getUser(mContext).getVibratesCues() == 1) {
                            RingVibrateManager.getInstance().vibrate(mContext);
                        }
                    }

                } else if (ChatRoom.TYPE_GROUP.equals(chatMsg.getRoomType())) {
                    GroupInfo groupInfo = GroupDao.getGroup(chatMsg.getRoomId());
                    if (groupInfo != null && groupInfo.getShieldMark() == 0) {
                        if (SpCons.getUser(mContext).getAudioCues() == 1) {
                            RingVibrateManager.getInstance().playSmsRingtone(mContext);
                        }
                        if (SpCons.getUser(mContext).getVibratesCues() == 1) {
                            RingVibrateManager.getInstance().vibrate(mContext);
                        }
                    }
                }

            }
        });

    }

    /**
     * cookie失效推送
     */
    private void serverCookieInvalid(String expireMessage) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, true);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_DIALOG, true);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_INFO, expireMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        CommonUtil.clearDataByLogout(mContext);
    }

    private void setNotificationUtil(ChatMsg lastMsg) {
        ChatRoom chatRoom = ChatRoomManager.getChatRoom(lastMsg, true);
        if (notificationUtil == null || chatRoom == null) {
            return;
        }
        ChatRoomDao.addRoom(chatRoom);
        String title;
        String roomName = "";
        String roomNote = "";
        if (ChatRoom.TYPE_SINGLE.equals(chatRoom.getRoomType())) {
            FriendInfo contactUser = ContactUserDao.getContactUser(chatRoom.getRoomId());
            if (contactUser != null) {
                roomName = contactUser.getUsername();
                roomNote = contactUser.getFriendNote();
            }
        } else if (ChatRoom.TYPE_GROUP.equals(chatRoom.getRoomType())) {
            GroupInfo groupInfo = GroupDao.getGroup(chatRoom.getRoomId());
            if (groupInfo != null) {
                roomName = groupInfo.getGroupName();
            }
        }
        if (!TextUtils.isEmpty(roomNote)) {
            title = roomNote;
        } else {
            title = roomName;
        }

        if (lastMsg.getFileInfo() != null) {
            FileInfo resource = lastMsg.getFileInfo();
            switch (resource.getFileType()) {
                case FileInfo.TYPE_IMG:
                    notificationUtil.setContent(title, "[" + mContext.getString(R.string.image) + "]");
                    break;

                case FileInfo.TYPE_VIDEO:
                    notificationUtil.setContent(title, "[" + mContext.getString(R.string.video) + "]");
                    break;

                case FileInfo.TYPE_DOC:
                    notificationUtil.setContent(title, "[" + mContext.getString(R.string.file) + "]");
                    break;

                case FileInfo.TYPE_MUSIC:
                    notificationUtil.setContent(title, "[" + mContext.getString(R.string.music) + "]");
                    break;

                case FileInfo.TYPE_TAPE_RECORD:
                    notificationUtil.setContent(title, "[" + mContext.getString(R.string.audio) + "]");
                    break;

                default:
                    if (!TextUtils.isEmpty(lastMsg.getContentDesc())) {
                        notificationUtil.setContent(title, "[" + lastMsg.getContentDesc() + "]");

                    } else {
                        notificationUtil.setContent(title, "[版本不支持]");
                    }
                    break;
            }

        } else if (lastMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL) {
            notificationUtil.setContent(title, "[" + mContext.getString(R.string.voice_calls) + "]");
        } else if (lastMsg.getContentType() == ChatMsg.TYPE_CONTENT_AT) {
            notificationUtil.setContent(title, EmoticonUtil.getEmoSpanStr(mContext,
                    new SpannableString(lastMsg.getContent())));
        } else if (!TextUtils.isEmpty(lastMsg.getContent())) {
            notificationUtil.setContent(title, EmoticonUtil.getEmoSpanStr(mContext,
                    new SpannableString(lastMsg.getContent())));
        } else {
            notificationUtil.setContent(title, lastMsg.getContent());
        }
        Intent intent = new Intent(mContext, ChatRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, 0);
        notificationUtil.setIntent(intent, ActivityUtil.isAppRunning(mContext)).notifyNot();
    }

}
