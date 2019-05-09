package com.wewin.hichat.model.socket;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.umeng.commonsdk.statistics.common.ReportPolicy;
import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.manage.RingVibrateManager;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.utils.CommonUtil;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
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
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.MessageSendingDao;
import com.wewin.hichat.model.db.dao.UserDao;
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
import com.wewin.hichat.view.conversation.ChatRoomActivity;
import com.wewin.hichat.view.login.LoginActivity;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


/**
 * Created by Darren on 2018/12/21.
 */
public class ChatSocket {

    private WebSocket okHttpWebSocket;
    private Handler handler = new Handler();
    private OkHttpClient okHttpClient;
    private Request request;
    private static ChatSocket chatSocket;
    private final int INTERVAL_HEART = 5 * 1000;//心跳间隔
    private final int CODE_CLOSE_NORMALLY = 1001;
    private String heartBack = "";
    private long heartSendTimestamp = 0;
    private boolean connectState = false;//socket连接状态
    private NotificationUtil notificationUtil;

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
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)//不允许失败重试
                    .build();
        }
        if (notificationUtil == null) {
            notificationUtil = new NotificationUtil();
            notificationUtil.setNotification(context);
        }
        UserDao.user = UserDao.getUser();
        if (UserDao.user == null) {
            return;
        }
        String userId = UserDao.user.getId();
        String cuid = SpCons.getCuid(context);
        String socketPath = HttpCons.SOCKET_IM + "?sessionId=" + userId + "&terminal=ANDROID&cuid=" + cuid;
        request = new Request.Builder().url(socketPath).build();
//        LogUtil.i("需要的参数：" + userId + "   " + cuid + "    " + socketPath);
        connect(context);

    }

    public void send(ChatMsg chatMsg) {
        String msgJsonStr;
        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_FILE) {
            if (chatMsg.getFileInfo() == null) {
                return;
            }
            if (TextUtils.isEmpty(chatMsg.getFileInfo().getDownloadPath())) {
                ChatRoomManager.saveChatMsg(chatMsg);
            } else if (okHttpWebSocket != null) {
                ChatMsg msg = ChatRoomManager.changeSendMsgRoomType(chatMsg, chatMsg.getRoomId());
                msgJsonStr = JSON.toJSONString(msg);
                okHttpWebSocket.send(ByteString.encodeUtf8(msgJsonStr));
                LogUtil.i("send jsonStr", msgJsonStr);
            }
        } else {
            ChatRoomManager.saveChatMsg(chatMsg);
            if (okHttpWebSocket != null) {
                ChatMsg msg = ChatRoomManager.changeSendMsgRoomType(chatMsg, chatMsg.getRoomId());
                msgJsonStr = JSON.toJSONString(msg);
                okHttpWebSocket.send(ByteString.encodeUtf8(msgJsonStr));
                LogUtil.i("send jsonStr", msgJsonStr);
            }
        }
    }

    public void reconnect(final Context context) {
        if (okHttpWebSocket != null) {
            okHttpWebSocket.close(CODE_CLOSE_NORMALLY, "stop");
            okHttpWebSocket = null;
            okHttpClient = null;
        }
        handler.removeCallbacksAndMessages(null);
        init(context);
    }

    public boolean isConnectState() {
        return connectState;
    }

    public void stop() {
        if (okHttpWebSocket != null) {
            okHttpWebSocket.close(CODE_CLOSE_NORMALLY, "stop");
            okHttpWebSocket = null;
        }
        handler.removeCallbacksAndMessages(null);
        connectState = false;
    }

    //建立连接
    private void connect(final Context context) {
//        LogUtil.i("socket的context" + context + "   " + request.toString());
        okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LogUtil.i("webSocket onOpen");
                connectState = true;
                okHttpWebSocket = webSocket;
                sendHeart();
                resendMsgList();
            }

            @Override
            public void onMessage(WebSocket webSocket, final ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (bytes == null) {
                    LogUtil.i("webSocket onMessage bytes == null");
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
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
                                processReceivedMsg(context, socketReceive);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                if (code != CODE_CLOSE_NORMALLY) {
                    connectState = false;
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                LogUtil.i("webSocket onClosed", "code: " + code + ", reason: " + reason);
                if (code != CODE_CLOSE_NORMALLY) {
                    connectState = false;
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                LogUtil.i("webSocket onFailure", "t: " + t);
                connectState = false;
            }
        });
    }

    //心跳
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

    //socket连上后将MessageSendingDao中的消息重新发送
    private void resendMsgList() {
        if (MessageSendingDao.getCount() <= 0 || okHttpWebSocket == null) {
            return;
        }
        List<ChatMsg> sendingMsgList = MessageSendingDao.getMessageList();
        for (ChatMsg msg : sendingMsgList) {
            String msgJsonStr = JSON.toJSONString(msg);
            okHttpWebSocket.send(ByteString.encodeUtf8(msgJsonStr));
        }
        MessageSendingDao.clear();
    }

    //收到socket消息进行处理
    private void processReceivedMsg(Context context, SocketReceive socketReceive) {
        if ("remote".equals(socketReceive.getType())) {
            ChatMsg receiveMsg = socketReceive.getRemote();
            if (receiveMsg == null) {
                return;
            }
            ChatRoomManager.parseReceiveMsg(context, receiveMsg);
            if (!MyLifecycleHandler.isApplicationVisible()) {
                //后台情况
                setNotificationUtil(context, receiveMsg);
            }
            //响铃和振动
//            startRingVibrate(context, receiveMsg);

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
                    EventTrans.post(EventMsg.CONVERSATION_CHAT_REFRESH);
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
                    syncMessageCommand(context, serverData);
                    break;

                //同步群组信息
                case "syncGroupInfo":
                    syncGroupInfo(serverData);
                    break;

                //同步群组相关操作
                case "syncGroupCommand":
                    syncGroupCommand(serverData);
                    break;
            }
        }
    }

    //同步用户信息
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

            case "updPassword":

                break;
        }
    }

    //同步好友相关操作
    private void syncFriendCommand(@NonNull ServerData serverData) {
        String friendId = serverData.getFromUid();
        if (TextUtils.isEmpty(friendId)) {
            return;
        }
        switch (serverData.getCmd()) {
            //1
            case "delFriend":
                MessageDao.updateShowMark(friendId, ChatRoom.TYPE_SINGLE);
                ChatRoomDao.deleteRoom(friendId, ChatRoom.TYPE_SINGLE);
                FriendDao.deleteFriend(friendId);
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

            case "shieldFriend":

                break;

            case "blackFriend":

                break;

            case "topFriendMsg":

                break;

            case "pushTyping":

                break;

            case "addSubgroup":

                break;

            case "moveSubgroup":

                break;

            case "renameSubgroup":

                break;

            case "agreeFriend":

                break;

            case "refuseFriend":

                break;
        }
    }

    //同步消息相关操作
    private void syncMessageCommand(Context context, @NonNull ServerData serverData) {
        switch (serverData.getCmd()) {
            case "removeFriendSession":
                SocketServer.MessageInfo messageInfo = serverData.getMessageInfo();
                if (messageInfo == null || !TextUtils.isEmpty(messageInfo.getFriendId())) {
                    return;
                }
                String friendId = messageInfo.getFriendId();
                MessageDao.updateShowMark(friendId, ChatRoom.TYPE_SINGLE);
                ChatRoomDao.deleteRoom(friendId, ChatRoom.TYPE_SINGLE);
                EventTrans.post(EventMsg.CONVERSATION_DELETE_ROOM, friendId, ChatRoom.TYPE_SINGLE);
                break;

            case "removeGroupSession":
                SocketServer.MessageInfo messageInfo1 = serverData.getMessageInfo();
                if (messageInfo1 == null || TextUtils.isEmpty(messageInfo1.getGroupId())) {
                    return;
                }
                String groupId = messageInfo1.getGroupId();
                MessageDao.updateShowMark(groupId, ChatRoom.TYPE_GROUP);
                ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                EventTrans.post(EventMsg.CONVERSATION_DELETE_ROOM, groupId, ChatRoom.TYPE_GROUP);
                break;

            case "clearSessionList":
                MessageDao.clearShow();
                ChatRoomDao.deleteAllRoom();
                EventTrans.post(EventMsg.CONVERSATION_DELETE_ALL_ROOM);
                break;

            case "chatMsgRead":

                break;

            case "runMsgBox":
                SpCons.setNotifyRedPointVisible(context, true);
                EventTrans.post(EventMsg.CONTACT_NOTIFY_REFRESH);
                break;

            case "closeMsgBox":
                SpCons.setNotifyRedPointVisible(context, false);
                EventTrans.post(EventMsg.CONTACT_NOTIFY_REFRESH);
                break;
        }
    }

    //同步群组信息
    private void syncGroupInfo(@NonNull ServerData serverData) {
        String groupId = serverData.getFromGroupId();
        SocketServer.GroupData groupData = serverData.getGroupInfo();
        if (TextUtils.isEmpty(groupId) || groupData == null) {
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
                if (TextUtils.isEmpty(groupData.getAvatar())) {
                    return;
                }
                groupInfo.setGroupAvatar(groupData.getAvatar());
                GroupDao.updateAvatar(groupId, groupData.getAvatar());
                EventTrans.post(EventMsg.CONTACT_GROUP_AVATAR_REFRESH, groupInfo);
                break;

            //1
            case "updDesc":
                if (TextUtils.isEmpty(groupData.getDescription())) {
                    return;
                }
                groupInfo.setDescription(groupData.getDescription());
                GroupDao.updateDesc(groupId, groupData.getDescription());
                EventTrans.post(EventMsg.CONTACT_GROUP_INFO_REFRESH, groupInfo);
                break;

            //1
            case "updGroupName":
                if (TextUtils.isEmpty(groupData.getGroupName())) {
                    return;
                }
                groupInfo.setGroupName(groupData.getGroupName());
                GroupDao.updateName(groupId, groupData.getGroupName());
                EventTrans.post(EventMsg.CONTACT_GROUP_INFO_REFRESH, groupInfo);
                break;

            //1
            case "updGroupPermission":
                groupInfo.setAddFriendMark(groupData.getAddFriendMark());
                groupInfo.setInviteFlag(groupData.getInviteMark());
                groupInfo.setSearchFlag(groupData.getSearchMark());
                groupInfo.setGroupSpeak(groupData.getSpeechMark());
                groupInfo.setGroupValid(groupData.getVerifyMark());
                EventTrans.post(EventMsg.CONTACT_GROUP_PERMISSION_REFRESH, groupInfo);
                break;
        }
    }

    //同步群组相关操作
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
                EventTrans.post(EventMsg.CONTACT_GROUP_AGREE_JOIN, groupInfo, executor,
                        receiverList.get(0));
                break;

            //1
            case "inviteFriendJoin":
                if (executor == null || receiverList == null || receiverList.isEmpty()) {
                    return;
                }
                EventTrans.post(EventMsg.CONTACT_GROUP_INVITE_MEMBER_REFRESH, groupInfo, executor,
                        receiverList.get(0));
                break;

            case "addGroupPost":

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
                if (UserDao.user.getId().equals(receiverList.get(0).getId())) {
                    GroupDao.deleteGroup(groupId);
                    ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                    MessageDao.updateShowMark(groupId, ChatRoom.TYPE_GROUP);
                }
                EventTrans.post(EventMsg.CONTACT_GROUP_REMOVE_MEMBER, groupInfo, executor,
                        receiverList.get(0));
                break;

            //1
            case "quitGroup":
                if (executor == null) {
                    return;
                }
                if (UserDao.user.getId().equals(executor.getId())) {
                    GroupDao.deleteGroup(groupId);
                    ChatRoomDao.deleteRoom(groupId, ChatRoom.TYPE_GROUP);
                    MessageDao.updateShowMark(groupId, ChatRoom.TYPE_GROUP);
                }
                EventTrans.post(EventMsg.CONTACT_GROUP_QUIT, groupId, executor.getId());
                break;
        }
    }

    //接收到消息是否响铃/震动
    private void startRingVibrate(Context context, ChatMsg chatMsg) {
        //未被屏蔽的好友/群才会响铃震动提醒
        if (ChatRoom.TYPE_SINGLE.equals(chatMsg.getRoomType())) {
            for (FriendInfo friend : FriendDao.getFriendList()) {
                if (friend.getId().equals(chatMsg.getRoomId())) {
                    if (UserDao.user != null && UserDao.user.getAudioCues() == 1
                            && friend.getShieldMark() == 0) {
                        RingVibrateManager.getInstance().playRingTone(context);
                    }
                    if (UserDao.user != null && UserDao.user.getVibratesCues() == 1
                            && friend.getShieldMark() == 0) {
                        RingVibrateManager.getInstance().vibrate(context);
                    }
                    break;
                }
            }

        } else if ("group".equals(chatMsg.getRoomType())) {
            for (GroupInfo group : GroupDao.getGroupList()) {
                if (group.getId().equals(chatMsg.getRoomId())) {
                    if (UserDao.user != null && UserDao.user.getAudioCues() == 1
                            && group.getShieldMark() == 0) {
                        RingVibrateManager.getInstance().playRingTone(context);
                    }
                    if (UserDao.user != null && UserDao.user.getVibratesCues() == 1
                            && group.getShieldMark() == 0) {
                        RingVibrateManager.getInstance().vibrate(context);
                    }
                    break;
                }
            }
        }
    }

    //cookie失效推送
    private void serverCookieInvalid(Context context, SocketServer server) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID, true);
        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_DIALOG, true);
//        intent.putExtra(LoginCons.EXTRA_LOGIN_COOKIE_INVALID_INFO, server.getContent());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        CommonUtil.clearDataByLogout(context);
    }

    private void setNotificationUtil(Context context, ChatMsg lastMsg) {
        if (notificationUtil == null) return;
        ChatRoom chatRoom = ChatRoomManager.getChatRoom(lastMsg);
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
                    notificationUtil.setContent(title, "[" + context.getString(R.string.image) + "]");
                    break;

                case FileInfo.TYPE_VIDEO:
                    notificationUtil.setContent(title, "[" + context.getString(R.string.video) + "]");
                    break;

                case FileInfo.TYPE_DOC:
                    notificationUtil.setContent(title, "[" + context.getString(R.string.file) + "]");
                    break;

                case FileInfo.TYPE_MUSIC:
                    notificationUtil.setContent(title, "[" + context.getString(R.string.music) + "]");
                    break;

                case FileInfo.TYPE_TAPE_RECORD:
                    notificationUtil.setContent(title, "[" + context.getString(R.string.audio) + "]");
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
            notificationUtil.setContent(title, "[" + context.getString(R.string.voice_calls) + "]");
        } else if (lastMsg.getContentType() == ChatMsg.TYPE_CONTENT_AT) {
            notificationUtil.setContent(title, EmoticonUtil.getExpressionString(context,
                    lastMsg.getContent()).toString());
        } else if (!TextUtils.isEmpty(lastMsg.getContent())) {
            notificationUtil.setContent(title, EmoticonUtil.getExpressionString(context,
                    lastMsg.getContent()));
        } else {
            notificationUtil.setContent(title, lastMsg.getContent());
        }
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ContactCons.EXTRA_CONTACT_CHAT_ROOM, chatRoom);
        intent.putExtra(ContactCons.EXTRA_MESSAGE_CHAT_START_TIMESTAMP, 0);
        notificationUtil.setIntent(intent);
        notificationUtil.notifyNot();
    }

}
