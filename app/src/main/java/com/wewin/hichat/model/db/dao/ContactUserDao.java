package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友+群成员+临时会话陌生人
 * Created by Darren on 2019/4/17
 **/
public class ContactUserDao {

    public static final String TABLE_NAME = "h_contact_user";
    public static final String LOCAL_ID = "local_id";
    public static final String CONTACT_ID = "contact_id";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_NOTE = "contact_note";
    public static final String CONTACT_AVATAR = "contact_avatar";
    public static final String TOP_MARK = "top_mark";
    public static final String SHIELD_MARK = "shield_mark";
    public static final String USER_ID = "user_id";

    public static void addContactUser(FriendInfo friendInfo) {
        if (friendInfo == null){
            return;
        }
        List<FriendInfo> friendInfoList = new ArrayList<>();
        friendInfoList.add(friendInfo);
        addContactUserList(friendInfoList);
    }

    public static void addContactUserList(List<FriendInfo> friendList) {
        if (friendList == null || friendList.isEmpty()){
            return;
        }
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = CONTACT_ID + " =? and " + USER_ID + " =?";
            for (FriendInfo friendInfo : friendList) {
                if (getContactUser(friendInfo.getId()) != null) {
                    db.update(TABLE_NAME, packContentValue(friendInfo), whereSql,
                            new String[]{friendInfo.getId(), UserDao.user.getId()});
                } else {
                    db.insert(TABLE_NAME, null, packContentValue(friendInfo));
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

    public static FriendInfo getContactUser(String friendId) {
        FriendInfo friendInfo = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + CONTACT_ID + " =? and " +
                    USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{friendId, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    friendInfo = parseCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendInfo;
    }

    public static void updateName(String friendId, String friendName) {
        update(friendId, CONTACT_NAME, friendName);
    }

    public static void updateNote(String friendId, String friendNote) {
        update(friendId, CONTACT_NOTE, friendNote);
    }

    public static void updateAvatar(String friendId, String avatarUrl) {
        update(friendId, CONTACT_AVATAR, avatarUrl);
    }

    public static void updateTopMark(String friendId, int topMark){
        update(friendId, TOP_MARK, String.valueOf(topMark));
    }

    public static void updateShieldMark(String friendId, int shieldMark){
        update(friendId, SHIELD_MARK, String.valueOf(shieldMark));
    }

    private static void update(String friendId, String columnName, String columnValue) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + columnName + " = ? where " + CONTACT_ID +
                    " = ? and " + USER_ID + " = ?";
            db.execSQL(sql, new String[]{columnValue, friendId, UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFriend(String friendId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, CONTACT_ID + " = ? and " + USER_ID + " = ?",
                    new String[]{friendId, UserDao.user.getId()});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    private static ContentValues packContentValue(FriendInfo friendInfo) {
        ContentValues values = new ContentValues();
        values.put(CONTACT_ID, friendInfo.getId());
        values.put(CONTACT_NAME, friendInfo.getUsername());
        values.put(CONTACT_NOTE, friendInfo.getFriendNote());
        values.put(CONTACT_AVATAR, friendInfo.getAvatar());
        values.put(TOP_MARK, friendInfo.getTopMark());
        values.put(SHIELD_MARK, friendInfo.getShieldMark());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

    private static FriendInfo parseCursorData(Cursor cursor) {
        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setId(cursor.getString(cursor.getColumnIndex(CONTACT_ID)));
        friendInfo.setUsername(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
        friendInfo.setFriendNote(cursor.getString(cursor.getColumnIndex(CONTACT_NOTE)));
        friendInfo.setAvatar(cursor.getString(cursor.getColumnIndex(CONTACT_AVATAR)));
        friendInfo.setTopMark(cursor.getInt(cursor.getColumnIndex(TOP_MARK)));
        friendInfo.setShieldMark(cursor.getInt(cursor.getColumnIndex(SHIELD_MARK)));
        return friendInfo;
    }

}
