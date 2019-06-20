package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.TimeUtil;
import com.wewin.hichat.androidlib.utils.UUIDUtil;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FileInfo;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.ReplyMsgInfo;
import com.wewin.hichat.model.db.entity.VoiceCall;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Darren on 2018/12/21.
 */
public class MessageDao {

    public static final String TABLE_NAME = "h_message";
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
    public static final String UN_SYNC_MSG_ID = "un_sync_msg_id";
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
    public static final String RECEIVER_NAME = "receiver_name";
    public static final String RECEIVER_AVATAR = "receiver_avatar";
    public static final String TAPE_UNREAD_MARK = "tape_unread_mark";
    public static final String SEND_STATE = "send_state";
    public static final String EMO_MARK = "emo_mark";
    public static final String PHONE_MARK = "phone_mark";
    public static final String URL_MARK = "url_mark";
    public static final String SHOW_MARK = "show_mark";
    public static final String USER_ID = "user_id";
    public static final String FRIENDSHIP_MARK = "friendship_mark";
    public static final String DELETE_MARK = "delete_mark";
    //2019/06/18新增回复的对象字段
    public static final String REPLY_CONTENT="reply_content";
    public static final String REPLY_CONTENT_TYPE="reply_content_type";
    public static final String REPLY_DOWNLOAD_PATH="reply_download_path";
    public static final String REPLY_DURATION="reply_duration";
    public static final String REPLY_FILE_LENGTH="reply_file_length";
    public static final String REPLY_FILE_NAME="reply_file_name";
    public static final String REPLY_FILE_TYPE="reply_file_type";
    public static final String REPLY_FILE_ID="reply_file_id";
    public static final String REPLY_SENDER_ID="reply_sender_id";
    public static final String REPLY_DOWNLOAD_STATE="reply_download_state";
    public static final String REPLY_SAVE_PATH="reply_save_path";
    public static final String REPLY_TAPE_UNREAD_MARK="reply_tape_unread_mark";

    private static final int PAGE_SIZE = 60;//每页分页条数


    public static void addMessage(ChatMsg chatMsg) {
        if (chatMsg == null) {
            return;
        }
        List<ChatMsg> dataList = new ArrayList<>();
        dataList.add(chatMsg);
        addMessageList(dataList);
    }

    public static List<ChatMsg> addMessageList(List<ChatMsg> msgList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = MSG_ID + " =? and " + USER_ID + " =?";
            for (ChatMsg chatMsg : msgList) {
                ChatMsg oldChatMsg=getMessage(chatMsg.getMsgId(),chatMsg.getLocalMsgId());
                if (oldChatMsg != null) {
                    chatMsg=saveLocChat(chatMsg,oldChatMsg);
                    db.update(TABLE_NAME, packContentValue(chatMsg), whereSql,
                            new String[]{chatMsg.getMsgId(), UserDao.user.getId()});
                } else {
                    db.insert(TABLE_NAME, null, packContentValue(chatMsg));
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
        return msgList;
    }

    //打开聊天页，获取当前时间戳之前的消息列表(升序)
    public static List<ChatMsg> getMessageBeforeList(String roomId, String roomType,
                                                     long startTimestamp) {
        List<ChatMsg> msgList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            if (UserDao.user != null) {
                String sql = "select * from ( select * from " + TABLE_NAME + " where " + USER_ID +
                        " = ? and " + ROOM_ID + " = ? and " +DELETE_MARK+" = 0 and " + SHOW_MARK + " = 1 and " + ROOM_TYPE +
                        " = ? and " + CREATE_TIMESTAMP + " < ? order by " + CREATE_TIMESTAMP +
                        " desc limit ? ) order by " + CREATE_TIMESTAMP;
                Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId(), roomId, roomType,
                        startTimestamp + "", PAGE_SIZE + ""});
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        msgList.add(parseCursorData(cursor));
                    }
                    cursor.close();
                }
            }
            DbManager.getInstance().closeDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }

    //打开聊天页，获取当前时间戳之前的消息列表(升序)
    public static List<ChatMsg> getMessageBeforeListAll(String roomId, String roomType, long startTimestamp) {
        List<ChatMsg> msgList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            if (UserDao.user != null) {
                String sql = "select * from "+TABLE_NAME+" where "+ USER_ID +
                        " = ? and " + ROOM_ID + " = ? and " +DELETE_MARK+" = 0 and " + SHOW_MARK + " = 1 and " + ROOM_TYPE +
                        " = ? and " + CREATE_TIMESTAMP + " <= ? order by " + CREATE_TIMESTAMP;
                Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId(), roomId, roomType, startTimestamp + ""});
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        msgList.add(parseCursorData(cursor));
                    }
                    cursor.close();
                }
            }
            DbManager.getInstance().closeDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }

    //搜索聊天记录，获取时间戳之后的消息列表
    public static List<ChatMsg> getMessageAfterList(String roomId, String roomType,
                                                    long startTimestamp) {
        List<ChatMsg> msgList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            if (UserDao.user != null) {
                String sql = "select * from " + TABLE_NAME + " where " + USER_ID + " = ? and " + ROOM_ID +
                        " = ? and " + SHOW_MARK + " = 1 and "+ DELETE_MARK+" = 0 and " + ROOM_TYPE + " = ? and " + CREATE_TIMESTAMP +
                        " >= ? order by " + CREATE_TIMESTAMP;
                Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId(), roomId, roomType,
                        startTimestamp + ""});
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        msgList.add(parseCursorData(cursor));
                    }
                    cursor.close();
                }
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }

    public static List<ChatMsg> getMessageListBySearch(String searchStr) {
        List<ChatMsg> msgList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            if (UserDao.user != null) {
                String sql = "select * from " + TABLE_NAME + " where " +DELETE_MARK +" = 0 and "+ USER_ID + " = ? and (" +
                        CONTENT_TYPE + " =? or " + CONTENT_TYPE + " =?) and " + SHOW_MARK + " = 1 and " + CONTENT
                        + " like ? order by " + ROOM_ID + ", " + CREATE_TIMESTAMP + " desc";
                Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId(),
                        String.valueOf(ChatMsg.TYPE_CONTENT_TEXT), String.valueOf(ChatMsg.TYPE_CONTENT_AT),
                        "%" + searchStr + "%"});
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        msgList.add(parseCursorData(cursor));
                    }
                    cursor.close();
                }
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }


    public static ChatMsg getMessage(String msgId,String localMsgId) {
        ChatMsg chatMsg = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql;
            Cursor cursor;
            if (TextUtils.isEmpty(msgId)) {
                sql = "select * from " + TABLE_NAME + " where " + LOCAL_MSG_ID + " = ? and " + USER_ID + " = ?";
                cursor = db.rawQuery(sql, new String[]{localMsgId, UserDao.user.getId()});
            }else {
                sql = "select * from " + TABLE_NAME + " where " + MSG_ID + " = ? and "  + USER_ID + " = ?";
                cursor = db.rawQuery(sql, new String[]{msgId, UserDao.user.getId()});
            }

            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    chatMsg = parseCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMsg;
    }

    public static ChatMsg getVoiceCall(String channel) {
        ChatMsg chatMsg = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + VOICE_CHANNEL + " = ? and " + USER_ID + " = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{channel, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    chatMsg = parseCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMsg;
    }


    static ChatMsg getLastMessageByRoomId(String roomId, String roomType) {
        ChatMsg chatMsg = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + ROOM_ID + " = ? and " +
                    ROOM_TYPE + " =? and " + SHOW_MARK + " = 1 and " + DELETE_MARK + " = 0 and "+ USER_ID + " = ? order by " +
                    CREATE_TIMESTAMP + " desc";
            Cursor cursor = db.rawQuery(sql, new String[]{roomId, roomType, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    chatMsg = parseCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMsg;
    }

    public static String getMaxMsgIdByRoomId(String roomId, String roomType) {
        String msgId = "";
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "select max(" + MSG_ID + ") from " + TABLE_NAME + " where " + ROOM_ID +
                    " =? and " + ROOM_TYPE + " =? and " + USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{roomId, roomType, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    msgId = cursor.getString(0);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgId;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    public static void updateMsgId(String localMsgId, String msgId) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + MSG_ID + " = ?, " + SEND_STATE +
                    "= 1 where " + LOCAL_MSG_ID + " = ? and " + USER_ID + " =?";
            db.execSQL(sql, new String[]{msgId, localMsgId, UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSendStateFail() {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + SEND_STATE + "= -1 where " + SEND_STATE
                    + " = 0 and " + USER_ID + " =?";
            db.execSQL(sql, new String[]{UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateFileInfo(String localMsgId, FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        ContentValues values = new ContentValues();
        values.put(FILE_ID, fileInfo.getId());
        values.put(FILE_NAME, fileInfo.getFileName());
        values.put(FILE_TYPE, fileInfo.getFileType());
        values.put(FILE_LENGTH, fileInfo.getFileLength());
        values.put(DOWNLOAD_PATH, fileInfo.getDownloadPath());
        values.put(DOWNLOAD_STATE, fileInfo.getDownloadState());
        values.put(SAVE_PATH, fileInfo.getSavePath());
        values.put(DURATION, fileInfo.getDuration());
        values.put(TAPE_UNREAD_MARK, fileInfo.getTapeUnreadMark());
        String whereSql = LOCAL_MSG_ID + " = ? and " + USER_ID + " =?";
        db.update(TABLE_NAME, values, whereSql, new String[]{localMsgId, UserDao.user.getId()});
        DbManager.getInstance().closeDatabase();
    }

    public static void updateReplyFileInfo(String localMsgId, FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        ContentValues values = new ContentValues();
        values.put(REPLY_FILE_ID, fileInfo.getId());
        values.put(REPLY_FILE_NAME, fileInfo.getFileName());
        values.put(REPLY_FILE_TYPE, fileInfo.getFileType());
        values.put(REPLY_FILE_LENGTH, fileInfo.getFileLength());
        values.put(REPLY_DOWNLOAD_PATH, fileInfo.getDownloadPath());
        values.put(REPLY_DOWNLOAD_STATE, fileInfo.getDownloadState());
        values.put(REPLY_SAVE_PATH, fileInfo.getSavePath());
        values.put(REPLY_DURATION, fileInfo.getDuration());
        values.put(REPLY_TAPE_UNREAD_MARK, fileInfo.getTapeUnreadMark());
        String whereSql = LOCAL_MSG_ID + " = ? and " + USER_ID + " =?";
        db.update(TABLE_NAME, values, whereSql, new String[]{localMsgId, UserDao.user.getId()});
        DbManager.getInstance().closeDatabase();
    }

    public static String getMaxUnSyncMsgId(ChatRoom chatRoom) {
        if (chatRoom == null) {
            return "0";
        }
        return getMaxUnSyncMsgId(chatRoom.getRoomId(), chatRoom.getRoomType());
    }

    private static String getMaxUnSyncMsgId(String roomId, String roomType) {
        String maxUnSyncId = "0";
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select max(" + UN_SYNC_MSG_ID + ") from " + TABLE_NAME + " where " +
                    ROOM_ID + " =? and " + ROOM_TYPE + " =? and " + USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{roomId, roomType, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    maxUnSyncId = cursor.getString(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxUnSyncId;
    }

    public static String getMinMsgId(ChatRoom chatRoom) {
        if (chatRoom == null) {
            return "0";
        }
        return getMinMsgId(chatRoom.getRoomId(), chatRoom.getRoomType());
    }

    private static String getMinMsgId(String roomId, String roomType) {
        String maxUnSyncId = "0";
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select min(" + MSG_ID + ") from " + TABLE_NAME + " where " +
                    ROOM_ID + " =? and " + ROOM_TYPE + " =? and " + USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{roomId, roomType, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    maxUnSyncId = cursor.getString(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxUnSyncId;
    }

    /**
     * 移除所有比传入的id小的unSyncMsgId
     */
    public static void removeUnSyncMsgId(ChatRoom chatRoom, String msgId) {
        if (chatRoom == null || TextUtils.isEmpty(msgId)) {
            return;
        }
        removeUnSyncMsgId(chatRoom.getRoomId(), chatRoom.getRoomType(), msgId);
    }

    private static void removeUnSyncMsgId(String roomId, String roomType, String msgId) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + UN_SYNC_MSG_ID + "= 0 where " +
                    ROOM_ID + " =? and " + ROOM_TYPE + " =? and " + UN_SYNC_MSG_ID + " >=? and " +
                    USER_ID + " =?";
            db.execSQL(sql, new String[]{roomId, roomType, msgId, UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCount(String roomId, String type) {
        int count = 0;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where "
                    + ROOM_ID + " = ? and " + ROOM_TYPE + " = ? and " + USER_ID + " =? and "
                    + SHOW_MARK + " = 1 and "+DELETE_MARK+" = 0";
            Cursor cursor = db.rawQuery(sql, new String[]{roomId, type, UserDao.user.getId()});
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

    public static boolean findMsg(String msgId) {
        boolean find=false;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + USER_ID + " =? and "+MSG_ID+" = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId(),msgId});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    find=true;
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return find;
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

    public static void deleteVoiceChannel(String voiceChannel) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "delete from " + TABLE_NAME + " where " + USER_ID + " = ? and "+VOICE_CHANNEL+" = ?";
            db.execSQL(sql, new String[]{UserDao.user.getId(),voiceChannel});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSingle(String msgId) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + DELETE_MARK + " = 1 where " + USER_ID + " = ? and "+MSG_ID+" = ?";
            db.execSQL(sql, new String[]{UserDao.user.getId(),msgId});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteList(List<String> msgIdList) {
        if (msgIdList==null){
            return;
        }
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + DELETE_MARK + " = 1 where " + USER_ID + " = ? and "+MSG_ID+" = ?";
            for (String msgId : msgIdList) {
                if (findMsg(msgId)) {
                    db.execSQL(sql, new String[]{UserDao.user.getId(), msgId});
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void deleteRoomMsg(String roomId,String roomType) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + DELETE_MARK + " = 1 where " + USER_ID + " = ? and "+ROOM_ID +
                    " =? and " + ROOM_TYPE+" = ?";
                db.execSQL(sql, new String[]{UserDao.user.getId(), roomId,roomType});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void deleteRoomMsgList(List<ChatRoom> msgIdList) {
        if (msgIdList==null){
            return;
        }
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + DELETE_MARK + " = 1 where " + USER_ID + " = ? and "+ROOM_ID +
                    " =? and " + ROOM_TYPE+" = ?";
            for (ChatRoom chatRoom : msgIdList) {
                db.execSQL(sql, new String[]{UserDao.user.getId(), chatRoom.getRoomId(),chatRoom.getRoomType()});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void deleteAllMsg() {

        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + DELETE_MARK + " = 1 where " + USER_ID + " = ?";
                db.execSQL(sql, new String[]{UserDao.user.getId()});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    private static ChatMsg parseCursorData(Cursor cursor) {
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
        chatMsg.setUnSyncMsgFirstId(cursor.getString(cursor.getColumnIndex(UN_SYNC_MSG_ID)));
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
            chatMsg.setFileInfo(fileInfo);
        } else if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL) {
            VoiceCall voiceCall = new VoiceCall();
            voiceCall.setInviteUserId(cursor.getString(cursor.getColumnIndex(VOICE_INVITE_ID)));
            voiceCall.setChannel(cursor.getString(cursor.getColumnIndex(VOICE_CHANNEL)));
            voiceCall.setConnectState(cursor.getInt(cursor.getColumnIndex(VOICE_CONNECT_STATE)));
            voiceCall.setDuration(cursor.getLong(cursor.getColumnIndex(DURATION)));
            chatMsg.setVoiceCall(voiceCall);
        }else if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY){
            ReplyMsgInfo replyMsgInfo=new ReplyMsgInfo();
            replyMsgInfo.setContent(cursor.getString(cursor.getColumnIndex(REPLY_CONTENT)));
            replyMsgInfo.setContentType(cursor.getInt(cursor.getColumnIndex(REPLY_CONTENT_TYPE)));
            if (replyMsgInfo.getContentType() == ChatMsg.TYPE_CONTENT_FILE) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setDownloadPath(cursor.getString(cursor.getColumnIndex(REPLY_DOWNLOAD_PATH)));
                fileInfo.setDuration(cursor.getLong(cursor.getColumnIndex(REPLY_DURATION)));
                fileInfo.setFileLength(cursor.getLong(cursor.getColumnIndex(REPLY_FILE_LENGTH)));
                fileInfo.setFileName(cursor.getString(cursor.getColumnIndex(REPLY_FILE_NAME)));
                fileInfo.setFileType(cursor.getInt(cursor.getColumnIndex(REPLY_FILE_TYPE)));
                fileInfo.setId(cursor.getString(cursor.getColumnIndex(REPLY_FILE_ID)));
                replyMsgInfo.setFileInfo(fileInfo);
            }
            FriendInfo reply = new FriendInfo();
            reply.setId(cursor.getString(cursor.getColumnIndex(REPLY_SENDER_ID)));
            replyMsgInfo.setSenderInfo(reply);
            chatMsg.setReplyMsgInfo(replyMsgInfo);
        }
        String senderName = cursor.getString(cursor.getColumnIndex(SENDER_NAME));
        if (!TextUtils.isEmpty(senderName)) {
            FriendInfo sender = new FriendInfo();
            sender.setUsername(senderName);
            sender.setAvatar(cursor.getString(cursor.getColumnIndex(SENDER_AVATAR)));
            chatMsg.setSenderInfo(sender);
        }
        String receiverName = cursor.getString(cursor.getColumnIndex(RECEIVER_NAME));
        if (!TextUtils.isEmpty(receiverName)){
            FriendInfo receiver = new FriendInfo();
            receiver.setUsername(receiverName);
            receiver.setAvatar(cursor.getString(cursor.getColumnIndex(RECEIVER_AVATAR)));
            chatMsg.setReceiverInfo(receiver);
        }
        chatMsg.setSendState(cursor.getInt(cursor.getColumnIndex(SEND_STATE)));
        chatMsg.setEmoMark(cursor.getInt(cursor.getColumnIndex(EMO_MARK)));
        chatMsg.setPhoneMark(cursor.getInt(cursor.getColumnIndex(PHONE_MARK)));
        chatMsg.setUrlMark(cursor.getInt(cursor.getColumnIndex(URL_MARK)));
        chatMsg.setShowMark(cursor.getInt(cursor.getColumnIndex(SHOW_MARK)));
        chatMsg.setFriendshipMark(cursor.getInt(cursor.getColumnIndex(FRIENDSHIP_MARK)));
        chatMsg.setDeleteMark(cursor.getInt(cursor.getColumnIndex(DELETE_MARK)));
        return chatMsg;
    }

    private static ContentValues packContentValue(ChatMsg chatMsg) {
        ContentValues values = new ContentValues();
        values.put(MSG_ID, chatMsg.getMsgId());
        if (TextUtils.isEmpty(chatMsg.getLocalMsgId())) {
            values.put(LOCAL_MSG_ID, UUIDUtil.get32UUID());
        } else {
            values.put(LOCAL_MSG_ID, chatMsg.getLocalMsgId());
        }
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
        values.put(UN_SYNC_MSG_ID, chatMsg.getUnSyncMsgFirstId());
        if ((chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_AT
                ||chatMsg.getContentType()==ChatMsg.TYPE_CONTENT_REPLY)
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

        } else if (chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_VOICE_CALL
                && chatMsg.getVoiceCall() != null) {
            values.put(VOICE_INVITE_ID, chatMsg.getVoiceCall().getInviteUserId());
            values.put(VOICE_CHANNEL, chatMsg.getVoiceCall().getChannel());
            values.put(VOICE_CONNECT_STATE, chatMsg.getVoiceCall().getConnectState());
            values.put(DURATION, chatMsg.getVoiceCall().getDuration());
        }
        if (chatMsg.getSenderInfo() != null) {
            values.put(SENDER_NAME, chatMsg.getSenderInfo().getUsername());
            values.put(SENDER_AVATAR, chatMsg.getSenderInfo().getAvatar());
        }
        if (chatMsg.getReceiverInfo() != null){
            values.put(RECEIVER_NAME, chatMsg.getReceiverInfo().getUsername());
            values.put(RECEIVER_AVATAR, chatMsg.getReceiverInfo().getAvatar());
        }
        if (!TextUtils.isEmpty(chatMsg.getMsgId())) {
            values.put(SEND_STATE, 1);
        } else {
            values.put(SEND_STATE, 0);
        }
        values.put(FRIENDSHIP_MARK, chatMsg.getFriendshipMark());
        values.put(EMO_MARK, chatMsg.getEmoMark());
        values.put(PHONE_MARK, chatMsg.getPhoneMark());
        values.put(URL_MARK, chatMsg.getUrlMark());
        values.put(SHOW_MARK, 1);
        values.put(USER_ID, UserDao.user.getId());
        values.put(DELETE_MARK,chatMsg.getDeleteMark());
        if (chatMsg.getReplyMsgInfo()!=null
                &&chatMsg.getContentType() == ChatMsg.TYPE_CONTENT_REPLY) {
            values.put(REPLY_CONTENT, chatMsg.getReplyMsgInfo().getContent());
            values.put(REPLY_CONTENT_TYPE, chatMsg.getReplyMsgInfo().getContentType());
            if (chatMsg.getReplyMsgInfo().getFileInfo()!=null
                    &&chatMsg.getReplyMsgInfo().getContentType() == ChatMsg.TYPE_CONTENT_FILE) {
                values.put(REPLY_DOWNLOAD_PATH, chatMsg.getReplyMsgInfo().getFileInfo().getDownloadPath());
                values.put(REPLY_DURATION, chatMsg.getReplyMsgInfo().getFileInfo().getDuration());
                values.put(REPLY_FILE_LENGTH, chatMsg.getReplyMsgInfo().getFileInfo().getFileLength());
                values.put(REPLY_FILE_NAME, chatMsg.getReplyMsgInfo().getFileInfo().getFileName());
                values.put(REPLY_FILE_TYPE, chatMsg.getReplyMsgInfo().getFileInfo().getFileType());
                values.put(REPLY_FILE_ID, chatMsg.getReplyMsgInfo().getFileInfo().getId());
                values.put(REPLY_DOWNLOAD_STATE,chatMsg.getReplyMsgInfo().getFileInfo().getDownloadState());
                values.put(REPLY_SAVE_PATH,chatMsg.getReplyMsgInfo().getFileInfo().getSavePath());
                values.put(REPLY_TAPE_UNREAD_MARK,chatMsg.getReplyMsgInfo().getFileInfo().getTapeUnreadMark());
            }
            if (chatMsg.getReplyMsgInfo().getSenderInfo()!=null) {
                values.put(REPLY_SENDER_ID, chatMsg.getReplyMsgInfo().getSenderInfo().getId());
            }
        }
        return values;
    }

    /**
     * 需要使用本地存储的数据
     */
    private static ChatMsg saveLocChat(ChatMsg chatMsg,ChatMsg oldChatMsg){
        chatMsg.setDeleteMark(oldChatMsg.getDeleteMark());
        chatMsg.setShowMark(oldChatMsg.getShowMark());
        chatMsg.setEmoMark(oldChatMsg.getEmoMark());
        chatMsg.setPhoneMark(oldChatMsg.getPhoneMark());
        chatMsg.setUrlMark(oldChatMsg.getUrlMark());
        return chatMsg;
    }
}
