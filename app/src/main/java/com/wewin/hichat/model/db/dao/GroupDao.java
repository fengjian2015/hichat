package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.androidlib.utils.TransUtil;
import com.wewin.hichat.model.db.entity.GroupInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/1/19
 */
public class GroupDao {

    public static final String TABLE_NAME = "h_group";
    public static final String LOCAL_ID = "local_id";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_AVATAR = "group_avatar";
    public static final String GROUP_NUM = "group_num";
    public static final String DESCRIPTION = "description";
    public static final String SEARCH_MARK = "search_mark";
    public static final String VERIFY_MARK = "verify_mark";
    public static final String INVITE_MARK = "invite_mark";
    public static final String ADD_FRIEND_MARK = "add_friend_mark";
    public static final String GROUP_SPEAK = "group_speak";
    public static final String TOP_MARK = "top_mark";
    public static final String SHIELD_MARK = "shield_mark";
    public static final String MEMBER_GRADE = "member_grade";
    public static final String USER_ID = "user_id";

    public static void addGroup(GroupInfo groupInfo){
        List<GroupInfo> groupList = new ArrayList<>();
        groupList.add(groupInfo);
        addGroupList(groupList);
    }

    public static void addGroupList(List<GroupInfo> groupList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = GROUP_ID + " =? and " + USER_ID + " =?";
            for (GroupInfo groupInfo : groupList) {
                if (getGroup(groupInfo.getId()) != null) {
                    db.update(TABLE_NAME, packContentValue(groupInfo), whereSql,
                            new String[]{groupInfo.getId(), UserDao.user.getId()});
                } else {
                    db.insert(TABLE_NAME, null, packContentValue(groupInfo));
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

    public static List<GroupInfo> getGroupList() {
        List<GroupInfo> groupList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + USER_ID + " =? " +
                    " order by " + LOCAL_ID, new String[]{UserDao.user.getId()});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    groupList.add(parseCursorData(cursor));
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupList;
    }

    public static GroupInfo getGroup(String groupId) {
        GroupInfo groupInfo = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            String sql = "select * from " + TABLE_NAME + " where " + GROUP_ID + " =? and "
                    + USER_ID + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{groupId, UserDao.user.getId()});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    groupInfo = parseCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupInfo;
    }


    /**
     * 获取群内是否允许添加好友
     * @return
     */
    public static int getAddMark(String groupId) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            int addMark = 0;
            Cursor cursor = db.rawQuery("select "+ADD_FRIEND_MARK+" from "+TABLE_NAME+" where "+ GROUP_ID + " =? and "
                    + USER_ID + " =?",
                    new String[]{groupId, UserDao.user.getId()});
            while (cursor.moveToNext()) {
                addMark = cursor.getInt(cursor.getColumnIndex(ADD_FRIEND_MARK));
            }
            cursor.close();
            DbManager.getInstance().closeDatabase();
            return addMark;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateTopMark(String groupId, int topMark) {
        update(groupId, TOP_MARK, String.valueOf(topMark));
    }

    public static void updateShieldMark(String groupId, int shieldMark) {
        update(groupId, SHIELD_MARK, String.valueOf(shieldMark));
    }

    public static void updateAvatar(String groupId, String groupAvatarUrl) {
        update(groupId, GROUP_AVATAR, groupAvatarUrl);
    }

    public static void updateName(String groupId, String groupName) {
        update(groupId, GROUP_NAME, groupName);
    }

    public static void updateDesc(String groupId, String desc) {
        update(groupId, DESCRIPTION, desc);
    }

    public static void updateGroupGrade(String groupId, int groupGrade) {
        update(groupId, MEMBER_GRADE, String.valueOf(groupGrade));
    }

    public static void updatePermission(GroupInfo groupInfo) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            ContentValues values = new ContentValues();
            if (groupInfo.getSearchFlag() >= 0) {
                values.put(SEARCH_MARK, groupInfo.getSearchFlag());
            }
            if (groupInfo.getGroupValid() >= 0) {
                values.put(VERIFY_MARK, groupInfo.getGroupValid());
            }
            if (groupInfo.getInviteFlag() >= 0) {
                values.put(INVITE_MARK, groupInfo.getInviteFlag());
            }
            if (groupInfo.getAddFriendMark() >= 0) {
                values.put(ADD_FRIEND_MARK, groupInfo.getAddFriendMark());
            }
            if (groupInfo.getGroupSpeak() >= 0) {
                values.put(GROUP_SPEAK, groupInfo.getGroupSpeak());
            }
            String whereSql = GROUP_ID + " =? and " + USER_ID + " =?";
            db.update(TABLE_NAME, values, whereSql, new String[]{groupInfo.getId(), UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void update(String groupId, String columnName, String columnValue) {
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            String sql = "update " + TABLE_NAME + " set " + columnName + " =? where " + GROUP_ID
                    + " =? and " + USER_ID + " =?";
            db.execSQL(sql, new String[]{columnValue, groupId, UserDao.user.getId()});
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空某一个表
     */
    public static void deleteTable(){
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
            db.execSQL("delete from "+TABLE_NAME);
            DbManager.getInstance().closeDatabase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void deleteGroup(String groupId) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, GROUP_ID + " = ? and " + USER_ID + " = ?",
                    new String[]{groupId, UserDao.user.getId()});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DbManager.getInstance().closeDatabase();
        }
    }

    private static ContentValues packContentValue(GroupInfo groupInfo) {
        ContentValues values = new ContentValues();
        values.put(GROUP_ID, groupInfo.getId());
        values.put(GROUP_NAME, groupInfo.getGroupName());
        values.put(GROUP_AVATAR, groupInfo.getGroupAvatar());
        values.put(GROUP_NUM, groupInfo.getGroupNum());
        values.put(DESCRIPTION, groupInfo.getDescription());
        values.put(SEARCH_MARK, groupInfo.getSearchFlag());
        values.put(VERIFY_MARK, groupInfo.getGroupValid());
        values.put(INVITE_MARK, groupInfo.getInviteFlag());
        values.put(ADD_FRIEND_MARK, groupInfo.getAddFriendMark());
        values.put(GROUP_SPEAK, groupInfo.getGroupSpeak());
        values.put(TOP_MARK, groupInfo.getTopMark());
        values.put(SHIELD_MARK, groupInfo.getShieldMark());
        values.put(MEMBER_GRADE, groupInfo.getGrade());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

    private static GroupInfo parseCursorData(Cursor cursor) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setSelfId(cursor.getString(cursor.getColumnIndex(LOCAL_ID)));
        groupInfo.setId(cursor.getString(cursor.getColumnIndex(GROUP_ID)));
        groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(GROUP_NAME)));
        groupInfo.setGroupAvatar(cursor.getString(cursor.getColumnIndex(GROUP_AVATAR)));
        groupInfo.setGroupNum(cursor.getString(cursor.getColumnIndex(GROUP_NUM)));
        groupInfo.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        groupInfo.setSearchFlag(cursor.getInt(cursor.getColumnIndex(SEARCH_MARK)));
        groupInfo.setGroupValid(cursor.getInt(cursor.getColumnIndex(VERIFY_MARK)));
        groupInfo.setInviteFlag(cursor.getInt(cursor.getColumnIndex(INVITE_MARK)));
        groupInfo.setAddFriendMark(cursor.getInt(cursor.getColumnIndex(ADD_FRIEND_MARK)));
        groupInfo.setGroupSpeak(cursor.getInt(cursor.getColumnIndex(GROUP_SPEAK)));
        groupInfo.setTopMark(cursor.getInt(cursor.getColumnIndex(TOP_MARK)));
        groupInfo.setShieldMark(cursor.getInt(cursor.getColumnIndex(SHIELD_MARK)));
        groupInfo.setGrade(cursor.getInt(cursor.getColumnIndex(MEMBER_GRADE)));
        return groupInfo;
    }

}
