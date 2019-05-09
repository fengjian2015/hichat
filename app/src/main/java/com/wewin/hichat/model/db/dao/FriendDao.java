package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.model.db.entity.FriendInfo;
import com.wewin.hichat.model.db.entity.Subgroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/1/4.
 */
public class FriendDao {

    public static final String TABLE_NAME = "h_friend";
    public static final String LOCAL_ID = "local_id";
    public static final String FRIEND_ID = "friend_id";
    public static final String USERNAME = "username";
    public static final String AVATAR = "avatar";
    public static final String FRIEND_NOTE = "friend_note";
    public static final String PHONE = "phone";
    public static final String SIGN = "sign";
    public static final String GENDER = "gender";
    public static final String SUBGROUP_ID = "subgroup_id";
    public static final String SUBGROUP_NAME = "subgroup_name";
    public static final String SUBGROUP_TYPE = "subgroup_type";
    public static final String TOP_MARK = "top_mark";
    public static final String SHIELD_MARK = "shield_mark";
    public static final String BLACK_MARK = "black_mark";
    public static final String FRIENDSHIP_MARK = "friendship_mark";
    public static final String USER_ID = "user_id";

    public static void addFriend(FriendInfo friendInfo, Subgroup subgroup) {
        List<FriendInfo> friendList = new ArrayList<>();
        friendList.add(friendInfo);
        addFriendList(friendList, subgroup);
    }

    public static void addFriendList(List<FriendInfo> friendList, Subgroup subgroup) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = FRIEND_ID + " =? and " + USER_ID + " =?";
            for (FriendInfo friendInfo : friendList) {
                if (getFriendInfo(friendInfo.getId()) != null) {
                    db.update(TABLE_NAME, packContentValue(friendInfo, subgroup), whereSql,
                            new String[]{friendInfo.getId(), UserDao.user.getId()});
                } else {
                    db.insert(TABLE_NAME, null, packContentValue(friendInfo, subgroup));
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

    public static List<FriendInfo> getFriendList() {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where "
                + USER_ID + " = ?", new String[]{UserDao.user.getId()});
        List<FriendInfo> contactList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactList.add(packCursorData(cursor));
            }
            cursor.close();
        }
        DbManager.getInstance().closeDatabase();
        return contactList;
    }

    public static FriendInfo getFriendInfo(String friendId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
        String sql = "select * from " + TABLE_NAME + " where " + FRIEND_ID + " = ? and "
                + USER_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{friendId, UserDao.user.getId()});
        FriendInfo friendInfo =null;
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                friendInfo = packCursorData(cursor);
            }
            cursor.close();
        }
        DbManager.getInstance().closeDatabase();
        return friendInfo;
    }

    /**
     * 获取是否为好友状态 0非好友 1 好友
     *
     * @return
     */
    public static int findFriendshipMark(String friendId) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            Cursor cursor = db.rawQuery("select " + FRIENDSHIP_MARK + " from " + TABLE_NAME +
                            " where " + FRIEND_ID + "=?", new String[]{friendId});
            int friendship = 0;
            if (cursor != null) {
                if (cursor.getCount() > 0 && cursor.moveToNext()) {
                    friendship = cursor.getInt(cursor.getColumnIndex(FRIENDSHIP_MARK));
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
            return friendship;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateFriendInfo(FriendInfo friendInfo) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        String sql = "update " + TABLE_NAME + " set " + USERNAME + " =? and " + AVATAR + " =? where " +
                FRIEND_ID + " =?";
        db.execSQL(sql, new String[]{friendInfo.getUsername(), friendInfo.getAvatar(), friendInfo.getId()});
        DbManager.getInstance().closeDatabase();
    }

    public static void updateGender(String friendId, int gender) {
        update(friendId, GENDER, String.valueOf(gender));
    }

    public static void updateSign(String friendId, String sign) {
        update(friendId, SIGN, sign);
    }

    public static void updateName(String friendId, String friendName) {
        update(friendId, USERNAME, friendName);
    }

    public static void updateNote(String friendId, String friendNote) {
        update(friendId, FRIEND_NOTE, friendNote);
    }

    public static void updateAvatar(String friendId, String avatarUrl) {
        update(friendId, AVATAR, avatarUrl);
    }

    public static void updateTopMark(String friendId, int topMark) {
        update(friendId, TOP_MARK, String.valueOf(topMark));
    }

    public static void updateShieldMark(String friendId, int shieldMark) {
        update(friendId, SHIELD_MARK, String.valueOf(shieldMark));
    }

    public static void updateSubgroup(String friendId, Subgroup subgroup) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        String sql = "update " + TABLE_NAME + " set " + SUBGROUP_ID + " =? and " + SUBGROUP_NAME +
                " =? and " + SUBGROUP_TYPE + " = ? where " + FRIEND_ID + " = ? and " + USER_ID + " = ?";
        db.execSQL(sql, new String[]{subgroup.getId(), subgroup.getGroupName(),
                String.valueOf(subgroup.getIsDefault()), friendId, UserDao.user.getId()});
        DbManager.getInstance().closeDatabase();
    }

    private static void update(String friendId, String columnName, String columnValue) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        String sql = "update " + TABLE_NAME + " set " + columnName + " = ? where " + FRIEND_ID +
                " = ? and " + USER_ID + " = ?";
        db.execSQL(sql, new String[]{columnValue, friendId, UserDao.user.getId()});
        DbManager.getInstance().closeDatabase();
    }

    public static void deleteFriend(String friendId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, FRIEND_ID + " = ? and " + USER_ID + " = ?",
                    new String[]{friendId, UserDao.user.getId()});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    private static ContentValues packContentValue(FriendInfo friend, Subgroup subgroup) {
        ContentValues values = new ContentValues();
        values.put(FRIEND_ID, friend.getId());
        values.put(USERNAME, friend.getUsername());
        values.put(AVATAR, friend.getAvatar());
        values.put(FRIEND_NOTE, friend.getFriendNote());
        values.put(PHONE, friend.getPhone());
        values.put(SIGN, friend.getSign());
        values.put(GENDER, friend.getGender());
        values.put(SUBGROUP_ID, subgroup.getId());
        values.put(SUBGROUP_NAME, subgroup.getGroupName());
        values.put(SUBGROUP_TYPE, subgroup.getIsDefault());
        values.put(TOP_MARK, friend.getTopMark());
        values.put(SHIELD_MARK, friend.getShieldMark());
        if (subgroup.getIsDefault() == Subgroup.TYPE_BLACK) {
            values.put(BLACK_MARK, "1");
        } else {
            values.put(BLACK_MARK, "0");
        }
        values.put(FRIENDSHIP_MARK, friend.getFriendship());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

    private static FriendInfo packCursorData(Cursor cursor) {
        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setId(cursor.getString(cursor.getColumnIndex(FRIEND_ID)));
        friendInfo.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
        friendInfo.setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)));
        friendInfo.setFriendNote(cursor.getString(cursor.getColumnIndex(FRIEND_NOTE)));
        friendInfo.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
        friendInfo.setSign(cursor.getString(cursor.getColumnIndex(SIGN)));
        friendInfo.setGender(cursor.getInt(cursor.getColumnIndex(GENDER)));
        friendInfo.setSubgroupId(cursor.getString(cursor.getColumnIndex(SUBGROUP_ID)));
        friendInfo.setSubgroupName(cursor.getString(cursor.getColumnIndex(SUBGROUP_NAME)));
        friendInfo.setSubgroupType(cursor.getInt(cursor.getColumnIndex(SUBGROUP_TYPE)));
        friendInfo.setTopMark(cursor.getInt(cursor.getColumnIndex(TOP_MARK)));
        friendInfo.setShieldMark(cursor.getInt(cursor.getColumnIndex(SHIELD_MARK)));
        friendInfo.setBlackMark(cursor.getInt(cursor.getColumnIndex(BLACK_MARK)));
        friendInfo.setFriendship(cursor.getInt(cursor.getColumnIndex(FRIENDSHIP_MARK)));
        return friendInfo;
    }

}
