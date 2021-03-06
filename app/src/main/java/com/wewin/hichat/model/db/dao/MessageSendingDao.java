package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.UUIDUtil;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.VoiceCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Darren on 2019/4/30
 **/
public class MessageSendingDao {

    public static final String TABLE_NAME = "h_message_sending";
    public static final String LOCAL_ID = "local_id";
    public static final String MSG_ID = "msg_id";
    public static final String LOCAL_MSG_ID = "local_msg_id";
    public static final String ROOM_ID = "room_id";
    public static final String ROOM_TYPE = "room_type";
    public static final String SENDER_ID = "sender_id";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String GROUP_ID = "group_id";
    public static final String CONTENT_TYPE = "content_type";
    public static final String CONTENT = "content";
    public static final String CREATE_TIMESTAMP = "create_timestamp";
    public static final String CREATE_TIME = "create_time";
    public static final String REPLY_MSG_ID = "reply_msg_id";
    public static final String AT_FRIEND_MAP = "at_friend_map";
    public static final String FILE_ID = "file_id";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_TYPE = "file_type";
    public static final String FILE_LENGTH = "file_length";
    public static final String ORIGIN_PATH = "origin_path";
    public static final String DOWNLOAD_PATH = "download_path";
    public static final String DOWNLOAD_STATE = "download_state";
    public static final String SAVE_PATH = "save_path";
    public static final String DURATION = "duration";
    public static final String VOICE_INVITE_ID = "voice_invite_id";
    public static final String VOICE_CHANNEL = "voice_channel";
    public static final String VOICE_CONNECT_STATE = "voice_connect_state";
    public static final String SERVER_ACTION_TYPE = "server_action_type";
    public static final String SENDER_NAME = "sender_name";
    public static final String SENDER_AVATAR = "sender_avatar";
    public static final String TAPE_UNREAD_MARK = "tape_unread_mark";
    public static final String SEND_STATE = "send_state";
    public static final String EMO_MARK = "emo_mark";
    public static final String PHONE_MARK = "phone_mark";
    public static final String URL_MARK = "url_mark";
    public static final String USER_ID = "user_id";
    public static final String COMPRESS_PATH="compress_path";


    public static void addMessage(ChatMsg chatMsg) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.insert(TABLE_NAME, null, packContentValue(chatMsg));
        DbManager.getInstance().closeDatabase();
    }

    public static List<ChatMsg> getMessageList() {
        List<ChatMsg> msgList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId()});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msgList.add(parseCursorData(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }

    public static int getCount() {
        int count = 0;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId()});
            if (cursor != null) {
                count = cursor.getCount();
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void deleteMessage(String localMsgId) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "delete from " + TABLE_NAME + " where " + LOCAL_MSG_ID + " = ? and " +
                    USER_ID + " =?";
            db.execSQL(sql, new String[]{localMsgId, UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "delete from " + TABLE_NAME + " where " + USER_ID + " = ?";
            db.execSQL(sql, new String[]{UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ChatMsg parseCursorData(@NonNull Cursor cursor) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setLocalId(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
        chatMsg.setMsgId(cursor.getString(cursor.getColumnIndex(MSG_ID)));
        chatMsg.setLocalMsgId(cursor.getString(cursor.getColumnIndex(LOCAL_MSG_ID)));
        chatMsg.setRoomId(cursor.getString(cursor.getColumnIndex(ROOM_ID)));
        chatMsg.setRoomType(cursor.getString(cursor.getColumnIndex(ROOM_TYPE)));
        chatMsg.setSenderId(cursor.getString(cursor.getColumnIndex(SENDER_ID)));
        chatMsg.setReceiverId(cursor.getString(cursor.getColumnIndex(RECEIVER_ID)));
        chatMsg.setGroupId(cursor.getString(cursor.getColumnIndex(GROUP_ID)));
        chatMsg.setContentType(cursor.getInt(cursor.getColumnIndex(CONTENT_TYPE)));
        chatMsg.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
        chatMsg.setCreateTimestamp(cursor.getLong(cursor.getColumnIndex(CREATE_TIMESTAMP)));
        chatMsg.setReplyMsgId(cursor.getString(cursor.getColumnIndex(REPLY_MSG_ID)));
        String atFriendMapStr = cursor.getString(cursor.getColumnIndex(AT_FRIEND_MAP));
        chatMsg.setAtFriendMap(JSONObject.parseObject(atFriendMapStr, Map.class));

        if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_FILE) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(cursor.getString(cursor.getColumnIndex(FILE_ID)));
            fileInfo.setFileName(cursor.getString(cursor.getColumnIndex(FILE_NAME)));
            fileInfo.setFileType(cursor.getInt(cursor.getColumnIndex(FILE_TYPE)));
            fileInfo.setFileLength(cursor.getLong(cursor.getColumnIndex(FILE_LENGTH)));
            fileInfo.setOriginPath(cursor.getString(cursor.getColumnIndex(ORIGIN_PATH)));
            fileInfo.setDownloadPath(cursor.getString(cursor.getColumnIndex(DOWNLOAD_PATH)));
            fileInfo.setDownloadState(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_STATE)));
            fileInfo.setSavePath(cursor.getString(cursor.getColumnIndex(SAVE_PATH)));
            fileInfo.setDuration(cursor.getLong(cursor.getColumnIndex(DURATION)));
            fileInfo.setTapeUnreadMark(cursor.getInt(cursor.getColumnIndex(TAPE_UNREAD_MARK)));
            fileInfo.setCompressPath(cursor.getString(cursor.getColumnIndex(COMPRESS_PATH)));
            chatMsg.setFileInfo(fileInfo);
        } else if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL) {
            VoiceCall voiceCall = new VoiceCall();
            voiceCall.setInviteUserId(cursor.getString(cursor.getColumnIndex(VOICE_INVITE_ID)));
            voiceCall.setChannel(cursor.getString(cursor.getColumnIndex(VOICE_CHANNEL)));
            voiceCall.setConnectState(cursor.getInt(cursor.getColumnIndex(VOICE_CONNECT_STATE)));
            voiceCall.setDuration(cursor.getLong(cursor.getColumnIndex(DURATION)));
            chatMsg.setVoiceCall(voiceCall);
        }
        if (chatMsg.getFriendshipMark() == 0) {
            FriendInfo sender = new FriendInfo();
            sender.setUsername(cursor.getString(cursor.getColumnIndex(SENDER_NAME)));
            sender.setAvatar(cursor.getString(cursor.getColumnIndex(SENDER_AVATAR)));
            chatMsg.setSenderInfo(sender);
        }
        chatMsg.setSendState(cursor.getInt(cursor.getColumnIndex(SEND_STATE)));
        chatMsg.setEmoMark(cursor.getInt(cursor.getColumnIndex(EMO_MARK)));
        chatMsg.setPhoneMark(cursor.getInt(cursor.getColumnIndex(PHONE_MARK)));
        chatMsg.setUrlMark(cursor.getInt(cursor.getColumnIndex(URL_MARK)));
        return chatMsg;
    }

    private static ContentValues packContentValue(ChatMsg chatMsg) {
        ContentValues values = new ContentValues();
        values.put(MSG_ID, chatMsg.getMsgId());
        values.put(LOCAL_MSG_ID, chatMsg.getLocalMsgId());
        values.put(ROOM_ID, chatMsg.getRoomId());
        values.put(ROOM_TYPE, chatMsg.getRoomType());
        values.put(SENDER_ID, chatMsg.getSenderId());
        values.put(RECEIVER_ID, chatMsg.getReceiverId());
        values.put(GROUP_ID, chatMsg.getGroupId());
        values.put(CONTENT_TYPE, chatMsg.getContentType());
        values.put(CONTENT, chatMsg.getContent());
        values.put(CREATE_TIMESTAMP, chatMsg.getCreateTimestamp());
        values.put(CREATE_TIME, TimeUtil.timestampToStr(chatMsg.getCreateTimestamp()));
        values.put(REPLY_MSG_ID, chatMsg.getReplyMsgId());
        if ((chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_AT
                ||chatMsg.getContentType()==ChatMsg.TYPE_CONTENT_REPLY_AT)
                &&chatMsg.getAtFriendMap()!=null) {
            values.put(AT_FRIEND_MAP, JSON.toJSONString(chatMsg.getAtFriendMap()));
        } else if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_FILE
                && chatMsg.getFileInfo() != null) {
            values.put(FILE_ID, chatMsg.getFileInfo().getId());
            values.put(FILE_NAME, chatMsg.getFileInfo().getFileName());
            values.put(FILE_TYPE, chatMsg.getFileInfo().getFileType());
            values.put(FILE_LENGTH, chatMsg.getFileInfo().getFileLength());
            values.put(ORIGIN_PATH, chatMsg.getFileInfo().getOriginPath());
            values.put(DOWNLOAD_PATH, chatMsg.getFileInfo().getDownloadPath());
            values.put(DOWNLOAD_STATE, chatMsg.getFileInfo().getDownloadState());
            values.put(SAVE_PATH, chatMsg.getFileInfo().getSavePath());
            values.put(DURATION, chatMsg.getFileInfo().getDuration());
            values.put(TAPE_UNREAD_MARK, chatMsg.getFileInfo().getTapeUnreadMark());
            values.put(COMPRESS_PATH,chatMsg.getFileInfo().getCompressPath());

        } else if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && chatMsg.getVoiceCall() != null) {
            values.put(VOICE_INVITE_ID, chatMsg.getVoiceCall().getInviteUserId());
            values.put(VOICE_CHANNEL, chatMsg.getVoiceCall().getChannel());
            values.put(VOICE_CONNECT_STATE, chatMsg.getVoiceCall().getConnectState());
            values.put(DURATION, chatMsg.getVoiceCall().getDuration());
        }
        if (chatMsg.getFriendshipMark() == 0
                && chatMsg.getSenderInfo() != null) {
            values.put(SENDER_NAME, chatMsg.getSenderInfo().getUsername());
            values.put(SENDER_AVATAR, chatMsg.getSenderInfo().getAvatar());
        }
        if (!TextUtils.isEmpty(chatMsg.getMsgId())) {
            values.put(SEND_STATE, 1);
        } else {
            values.put(SEND_STATE, 0);
        }
        values.put(EMO_MARK, chatMsg.getEmoMark());
        values.put(PHONE_MARK, chatMsg.getPhoneMark());
        values.put(URL_MARK, chatMsg.getUrlMark());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

}
