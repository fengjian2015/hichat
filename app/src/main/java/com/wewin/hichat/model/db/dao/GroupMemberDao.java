package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.model.db.entity.FriendInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/4/16
 **/
public class GroupMemberDao {

    public static final String TABLE_NAME = "h_group_member";
    public static final String LOCAL_ID = "local_id";
    public static final String FRIEND_ID = "friend_id";
    public static final String GROUP_ID = "group_id";
    public static final String USERNAME = "username";
    public static final String AVATAR = "avatar";
    public static final String FRIEND_NOTE = "friend_note";
    public static final String SIGN = "sign";
    public static final String GENDER = "gender";
    public static final String GRADE_IN_GROUP = "grade_in_group";
    public static final String FRIENDSHIP_MARK = "friendship_mark";
    public static final String USER_ID = "user_id";


    public static void addGroupMember(String groupId, FriendInfo friendInfo) {
        List<FriendInfo> memberList = new ArrayList<>();
        memberList.add(friendInfo);
        addGroupMemberList(groupId, memberList);
    }

    public static void addGroupMemberList(String groupId, List<FriendInfo> memberList){
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = FRIEND_ID + " =? and " + GROUP_ID + " =? and " + USER_ID + " =?";
            for (FriendInfo friendInfo : memberList) {
                friendInfo.setGroupId(groupId);
                if (getGroupMember(friendInfo.getId(), friendInfo.getGroupId()) != null) {
                    db.update(TABLE_NAME, packContentValue(friendInfo), whereSql,
                            new String[]{friendInfo.getId(), friendInfo.getGroupId(),
                                    UserDao.user.getId()});

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

    public static List<FriendInfo> getGroupMemberList(String groupId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
        String sql = "select * from " + TABLE_NAME + " where " + GROUP_ID + " =? and " +
                USER_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{groupId, UserDao.user.getId()});
        List<FriendInfo> memberList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                memberList.add(parseCursorData(cursor));
            }
            cursor.close();
        }
        DbManager.getInstance().closeDatabase();
        return memberList;
    }

    /**
     * 获取群成员，除去自己的信息
     * @param groupId
     * @return
     */
    public static List<FriendInfo> getGroupMemberListNoMy(String groupId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
        String sql = "select * from " + TABLE_NAME + " where " + GROUP_ID + " =? and " +
                USER_ID + " =? and not "+FRIEND_ID+" =?";
        Cursor cursor = db.rawQuery(sql, new String[]{groupId, UserDao.user.getId(),UserDao.user.getId()});
        List<FriendInfo> memberList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                memberList.add(parseCursorData(cursor));
            }
            cursor.close();
        }
        DbManager.getInstance().closeDatabase();
        return memberList;
    }

    public static FriendInfo getGroupMember(String friendId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
        String sql = "select * from " + TABLE_NAME + " where " + FRIEND_ID + " =? and " +
                USER_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{friendId, UserDao.user.getId()});
        FriendInfo member = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                member = parseCursorData(cursor);
            }
            cursor.close();
        }
        db.close();
        DbManager.getInstance().closeDatabase();
        return member;
    }

    public static FriendInfo getGroupMember(String friendId, String groupId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
        String sql = "select * from " + TABLE_NAME + " where " + FRIEND_ID + " =? and " +
                GROUP_ID + " =? and " + USER_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{friendId, groupId, UserDao.user.getId()});
        FriendInfo member = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                member = parseCursorData(cursor);
            }
            cursor.close();
        }
        DbManager.getInstance().closeDatabase();
        return member;
    }

    private static ContentValues packContentValue(FriendInfo friend) {
        ContentValues values = new ContentValues();
        values.put(FRIEND_ID, friend.getId());
        values.put(GROUP_ID, friend.getGroupId());
        values.put(USERNAME, friend.getUsername());
        values.put(AVATAR, friend.getAvatar());
        values.put(FRIEND_NOTE, friend.getFriendNote());
        values.put(SIGN, friend.getSign());
        values.put(GENDER, friend.getGender());
        values.put(GRADE_IN_GROUP, friend.getGrade());
        values.put(FRIENDSHIP_MARK, friend.getFriendship());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

    private static FriendInfo parseCursorData(Cursor cursor) {
        FriendInfo member = new FriendInfo();
        member.setId(cursor.getString(cursor.getColumnIndex(FRIEND_ID)));
        member.setGroupId(cursor.getString(cursor.getColumnIndex(GROUP_ID)));
        member.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
        member.setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)));
        member.setFriendNote(cursor.getString(cursor.getColumnIndex(FRIEND_NOTE)));
        member.setSign(cursor.getString(cursor.getColumnIndex(SIGN)));
        member.setGender(cursor.getInt(cursor.getColumnIndex(GENDER)));
        member.setGrade(cursor.getInt(cursor.getColumnIndex(GRADE_IN_GROUP)));
        member.setFriendship(cursor.getInt(cursor.getColumnIndex(FRIENDSHIP_MARK)));
        return member;
    }


}
