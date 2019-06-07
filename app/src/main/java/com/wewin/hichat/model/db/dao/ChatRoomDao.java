package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.model.db.entity.ChatMsg;
import com.wewin.hichat.model.db.entity.ChatRoom;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.GroupInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Darren on 2019/4/18
 **/
public class ChatRoomDao {

    public static final String TABLE_NAME = "h_chat_room";
    public static final String LOCAL_ID = "local_id";
    public static final String ROOM_ID = "room_id";
    public static final String ROOM_TYPE = "room_type";
    public static final String LOCAL_MSG_ID = "local_msg_id";
    public static final String LAST_MSG_ID = "last_msg_id";
    public static final String LAST_MSG_TIME = "last_msg_time";
    public static final String TOP_MARK = "top_mark";
    public static final String SHIELD_MARK = "shield_mark";
    public static final String UNREAD_NUM = "unread_num";
    public static final String AT_MARK = "at_mark";
    public static final String AT_TYPE = "at_type";
    public static final String FRIENDSHIP_MARK = "friendship_mark";
    public static final String USER_ID = "user_id";

    public static void addRoom(ChatRoom chatRoom) {
        List<ChatRoom> roomList = new ArrayList<>();
        roomList.add(chatRoom);
        addRoomList(roomList);
    }

    public static void addRoomList(List<ChatRoom> roomList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = ROOM_ID + " =? and " + ROOM_TYPE + " =? and " + USER_ID + " =?";
            for (ChatRoom chatRoom : roomList) {
                if (getRoom(chatRoom.getRoomId(), chatRoom.getRoomType()) != null) {
                    db.update(TABLE_NAME, packRoomContentValue(chatRoom), whereSql,
                            new String[]{chatRoom.getRoomId(), chatRoom.getRoomType(),
                                    UserDao.user.getId()});
                } else {
                    db.insert(TABLE_NAME, null, packRoomContentValue(chatRoom));
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

    public static List<ChatRoom> getRoomList() {
        List<ChatRoom> chatRoomList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + USER_ID + " = ? ";
            Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId()});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    chatRoomList.add(packCursorData(cursor));
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatRoomList;
    }

    public static ChatRoom getRoom(String roomId, String roomType) {
        ChatRoom chatRoom = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + ROOM_ID + " = ? and " +
                    ROOM_TYPE + " = ? and " + USER_ID + " = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{roomId, roomType, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    chatRoom = packCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatRoom;
    }

    public static int getUnreadNum(String roomId, String roomType) {
        if (getRoom(roomId, roomType) != null) {
            return getRoom(roomId, roomType).getUnreadNum();
        }
        return 0;
    }

    public static int getTotalUnreadNum() {
        int unreadNum = 0;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select sum( " + UNREAD_NUM + " ) from " + TABLE_NAME + " where " + USER_ID + " =? and " + SHIELD_MARK + " !=1";
            Cursor cursor = db.rawQuery(sql, new String[]{UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    unreadNum = cursor.getInt(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unreadNum;
    }

    /**
     * 获取@type
     */
    public static int getAtType(String roomId,String roomType) {
        int atType = 0;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            Cursor cursor = db.rawQuery("select " + AT_TYPE + " from " + TABLE_NAME + " where " + ROOM_ID + " = ? and " +
                    ROOM_TYPE + " = ? and " + USER_ID + " = ?",
                    new String[]{roomId,roomType, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    atType = cursor.getInt(cursor.getColumnIndex(AT_TYPE));
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return atType;
    }

    public static void updateTopMark(String roomId, String roomType, int topMark) {
        update(roomId, roomType, TOP_MARK, String.valueOf(topMark));
    }

    public static void updateShieldMark(String roomId, String roomType, int shieldMark) {
        update(roomId, roomType, SHIELD_MARK, String.valueOf(shieldMark));
    }

    public static void updateUnreadNum(String roomId, String roomType, int unreadNum) {
        update(roomId, roomType, UNREAD_NUM, String.valueOf(unreadNum));
    }

    public static void updateAtType(String roomId, String roomType, int atType) {
        update(roomId, roomType, AT_TYPE, String.valueOf(atType));
    }

    private static void update(String roomId, String roomType, String columnName, String columnValue) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + columnName + " = ? where " + ROOM_ID +
                    " = ? and " + ROOM_TYPE + " = ? and " + USER_ID + " = ?";
            db.execSQL(sql, new String[]{columnValue, roomId, roomType, UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTopShieldByFriendList(List<FriendInfo> friendList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + TOP_MARK + " =?, " + SHIELD_MARK +
                    " =? where " + ROOM_ID + " =? and " + ROOM_TYPE + " =? and " + USER_ID + " =?";
            for (FriendInfo friend : friendList) {
                db.execSQL(sql, new String[]{String.valueOf(friend.getTopMark()),
                        String.valueOf(friend.getShieldMark()), friend.getId(),
                        ChatRoom.TYPE_SINGLE, UserDao.user.getId()});
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void updateTopShieldByGroupList(List<GroupInfo> groupList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + TOP_MARK + " =?, " + SHIELD_MARK +
                    " =? where " + ROOM_ID + " =? and " + ROOM_TYPE + " =? and " + USER_ID + " =?";
            for (GroupInfo group : groupList) {
                db.execSQL(sql, new String[]{String.valueOf(group.getTopMark()),
                        String.valueOf(group.getShieldMark()), group.getId(),
                        ChatRoom.TYPE_GROUP, UserDao.user.getId()});
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void clearConversation(String roomId, String roomType) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = "update " + TABLE_NAME + " set " + LAST_MSG_ID + " = ? where " + ROOM_ID +
                    " = ? and " + ROOM_TYPE + " = ? and " + USER_ID + " =? ";
            db.execSQL(sql, new String[]{"0", roomId, roomType, UserDao.user.getId()});
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void deleteRoom(String roomId, String roomType) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = ROOM_ID + " = ? and " + ROOM_TYPE + " = ? and " + USER_ID + " = ?";
            db.delete(TABLE_NAME, sql, new String[]{roomId, roomType, UserDao.user.getId()});
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void deleteRoomList(List<ChatRoom> roomList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String sql = ROOM_ID + " = ? and " + ROOM_TYPE + " = ? and " + USER_ID + " = ?";
            for (ChatRoom chatRoom : roomList) {
                db.delete(TABLE_NAME, sql, new String[]{chatRoom.getRoomId(), chatRoom.getRoomType(),
                        UserDao.user.getId()});
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    public static void deleteAllRoom() {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = USER_ID + " = ?";
            db.delete(TABLE_NAME, sql, new String[]{UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ContentValues packRoomContentValue(@NonNull ChatRoom chatRoom) {
        ContentValues values = new ContentValues();
        values.put(ROOM_ID, chatRoom.getRoomId());
        values.put(ROOM_TYPE, chatRoom.getRoomType());
        if (chatRoom.getLastChatMsg() != null) {
            values.put(LAST_MSG_ID, chatRoom.getLastChatMsg().getMsgId());
            values.put(LAST_MSG_TIME, chatRoom.getLastChatMsg().getCreateTimestamp());
        } else {
            values.put(LAST_MSG_ID, "0");
            values.put(LAST_MSG_TIME, "0");
        }
        values.put(TOP_MARK, chatRoom.getTopMark());
        values.put(SHIELD_MARK, chatRoom.getShieldMark());
        values.put(UNREAD_NUM, chatRoom.getUnreadNum());
        values.put(AT_MARK, chatRoom.getAtMark());
        values.put(AT_TYPE, chatRoom.getAtType());
        values.put(FRIENDSHIP_MARK, chatRoom.getFriendshipMark());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

    private static ChatRoom packCursorData(@NonNull Cursor cursor) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(cursor.getString(cursor.getColumnIndex(ROOM_ID)));
        chatRoom.setRoomType(cursor.getString(cursor.getColumnIndex(ROOM_TYPE)));
        chatRoom.setLastChatMsg(MessageDao.getLastMessageByRoomId(chatRoom.getRoomId(),
                chatRoom.getRoomType()));
        chatRoom.setLastMsgTime(cursor.getLong(cursor.getColumnIndex(LAST_MSG_TIME)));
        chatRoom.setTopMark(cursor.getInt(cursor.getColumnIndex(TOP_MARK)));
        chatRoom.setShieldMark(cursor.getInt(cursor.getColumnIndex(SHIELD_MARK)));
        chatRoom.setUnreadNum(cursor.getInt(cursor.getColumnIndex(UNREAD_NUM)));
        chatRoom.setAtMark(cursor.getInt(cursor.getColumnIndex(AT_MARK)));
        chatRoom.setAtType(cursor.getInt(cursor.getColumnIndex(AT_TYPE)));
        chatRoom.setFriendshipMark(cursor.getInt(cursor.getColumnIndex(FRIENDSHIP_MARK)));
        return chatRoom;
    }

}
