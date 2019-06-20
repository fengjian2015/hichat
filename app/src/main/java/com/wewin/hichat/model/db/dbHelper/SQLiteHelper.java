package com.wewin.hichat.model.db.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wewin.hichat.model.db.dao.ChatRoomDao;
import com.wewin.hichat.model.db.dao.ContactUserDao;
import com.wewin.hichat.model.db.dao.FriendDao;
import com.wewin.hichat.model.db.dao.GroupDao;
import com.wewin.hichat.model.db.dao.GroupMemberDao;
import com.wewin.hichat.model.db.dao.MessageDao;
import com.wewin.hichat.model.db.dao.MessageSendingDao;
import com.wewin.hichat.model.db.dao.PhoneContactDao;
import com.wewin.hichat.model.db.dao.UserDao;

/**
 * Created by Darren on 2018/12/13.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HiChat.db"; //数据库名称
    private static final int VERSION = 3; //版本

    public SQLiteHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    private final String TABLE_USER = "create table "
            + UserDao.TABLE_NAME + "("
            + UserDao.ID + " varchar, "
            + UserDao.ACCOUNT + " varchar, "
            + UserDao.AVATAR + " varchar, "
            + UserDao.EMAIL + " varchar, "
            + UserDao.GENDER + " varchar, "
            + UserDao.IS_VIP + " varchar, "
            + UserDao.USERNAME + " varchar, "
            + UserDao.NO + " varchar, "
            + UserDao.COUNTRY_CODE + " varchar, "
            + UserDao.PHONE + " varchar, "
            + UserDao.AUDIO_CUES + " varchar, "
            + UserDao.VIBRATES_CUES + " varchar, "
            + UserDao.SIGN + " varchar);";

    private final String TABLE_MESSAGE = "create table "
            + MessageDao.TABLE_NAME + "("
            + MessageDao.LOCAL_ID + " integer primary key autoincrement, "
            + MessageDao.MSG_ID + " bigint, "
            + MessageDao.LOCAL_MSG_ID + " varchar, "
            + MessageDao.ROOM_ID + " varchar, "
            + MessageDao.ROOM_TYPE + " varchar, "
            + MessageDao.SENDER_ID + " varchar, "
            + MessageDao.RECEIVER_ID + " varchar, "
            + MessageDao.GROUP_ID + " varchar, "
            + MessageDao.CONTENT_TYPE + " varchar, "
            + MessageDao.CONTENT + " varchar, "
            + MessageDao.CREATE_TIMESTAMP + " bigint, "
            + MessageDao.CREATE_TIME + " varchar, "
            + MessageDao.REPLY_MSG_ID + " bigint, "
            + MessageDao.UN_SYNC_MSG_ID + " bigint, "
            + MessageDao.AT_FRIEND_MAP + " varchar, "
            + MessageDao.FILE_ID + " varchar, "
            + MessageDao.FILE_NAME + " varchar, "
            + MessageDao.FILE_TYPE + " varchar, "
            + MessageDao.FILE_LENGTH + " varchar, "
            + MessageDao.ORIGIN_PATH + " varchar, "
            + MessageDao.DOWNLOAD_PATH + " varchar, "
            + MessageDao.DOWNLOAD_STATE + " varchar, "
            + MessageDao.SAVE_PATH + " varchar, "
            + MessageDao.DURATION + " varchar, "
            + MessageDao.VOICE_INVITE_ID + " varchar, "
            + MessageDao.VOICE_CHANNEL + " varchar, "
            + MessageDao.VOICE_CONNECT_STATE + " varchar, "
            + MessageDao.SERVER_ACTION_TYPE + " varchar, "
            + MessageDao.SENDER_NAME + " varchar, "
            + MessageDao.SENDER_AVATAR + " varchar, "
            + MessageDao.RECEIVER_NAME + " varchar, "
            + MessageDao.RECEIVER_AVATAR + " varchar, "
            + MessageDao.TAPE_UNREAD_MARK + " varchar, "
            + MessageDao.SEND_STATE + " integer, "
            + MessageDao.EMO_MARK + " integer, "
            + MessageDao.PHONE_MARK + " integer, "
            + MessageDao.URL_MARK + " integer, "
            + MessageDao.SHOW_MARK + " integer, "
            + MessageDao.FRIENDSHIP_MARK + " integer, "
            + MessageDao.DELETE_MARK + " integer, "
            + MessageDao.REPLY_CONTENT + " varchar, "
            + MessageDao.REPLY_CONTENT_TYPE + " integer, "
            + MessageDao.REPLY_DOWNLOAD_PATH + " varchar, "
            + MessageDao.REPLY_DURATION + " integer, "
            + MessageDao.REPLY_FILE_LENGTH + " integer, "
            + MessageDao.REPLY_FILE_NAME + " varchar, "
            + MessageDao.REPLY_FILE_TYPE + " integer, "
            + MessageDao.REPLY_FILE_ID + " varchar, "
            + MessageDao.REPLY_SENDER_ID + " varchar, "
            + MessageDao.REPLY_DOWNLOAD_STATE + " integer, "
            + MessageDao.REPLY_SAVE_PATH + " varchar, "
            + MessageDao.REPLY_TAPE_UNREAD_MARK + " integer, "
            + MessageDao.USER_ID + " varchar);";

    private final String TABLE_MESSAGE_SENDING = "create table "
            + MessageSendingDao.TABLE_NAME + "("
            + MessageSendingDao.LOCAL_ID + " integer primary key autoincrement, "
            + MessageSendingDao.MSG_ID + " varchar, "
            + MessageSendingDao.LOCAL_MSG_ID + " varchar, "
            + MessageSendingDao.ROOM_ID + " varchar, "
            + MessageSendingDao.ROOM_TYPE + " varchar, "
            + MessageSendingDao.SENDER_ID + " varchar, "
            + MessageSendingDao.RECEIVER_ID + " varchar, "
            + MessageSendingDao.GROUP_ID + " varchar, "
            + MessageSendingDao.CONTENT_TYPE + " varchar, "
            + MessageSendingDao.CONTENT + " varchar, "
            + MessageSendingDao.CREATE_TIMESTAMP + " float, "
            + MessageSendingDao.CREATE_TIME + " varchar, "
            + MessageSendingDao.REPLY_MSG_ID + " varchar, "
            + MessageSendingDao.AT_FRIEND_MAP + " varchar, "
            + MessageSendingDao.FILE_ID + " varchar, "
            + MessageSendingDao.FILE_NAME + " varchar, "
            + MessageSendingDao.FILE_TYPE + " varchar, "
            + MessageSendingDao.FILE_LENGTH + " varchar, "
            + MessageSendingDao.ORIGIN_PATH + " varchar, "
            + MessageSendingDao.DOWNLOAD_PATH + " varchar, "
            + MessageSendingDao.DOWNLOAD_STATE + " varchar, "
            + MessageSendingDao.SAVE_PATH + " varchar, "
            + MessageSendingDao.DURATION + " varchar, "
            + MessageSendingDao.VOICE_INVITE_ID + " varchar, "
            + MessageSendingDao.VOICE_CHANNEL + " varchar, "
            + MessageSendingDao.VOICE_CONNECT_STATE + " varchar, "
            + MessageSendingDao.SERVER_ACTION_TYPE + " varchar, "
            + MessageSendingDao.SENDER_NAME + " varchar, "
            + MessageSendingDao.SENDER_AVATAR + " varchar, "
            + MessageSendingDao.TAPE_UNREAD_MARK + " varchar, "
            + MessageSendingDao.SEND_STATE + " integer, "
            + MessageSendingDao.EMO_MARK + " integer, "
            + MessageSendingDao.PHONE_MARK + " integer, "
            + MessageSendingDao.URL_MARK + " integer, "
            + MessageSendingDao.COMPRESS_PATH + " varchar, "
            + MessageSendingDao.USER_ID + " varchar);";

    private final String TABLE_CHAT_ROOM = "create table "
            + ChatRoomDao.TABLE_NAME + "("
            + ChatRoomDao.LOCAL_ID + " integer primary key autoincrement, "
            + ChatRoomDao.ROOM_ID + " varchar, "
            + ChatRoomDao.ROOM_TYPE + " varchar, "
            + ChatRoomDao.LOCAL_MSG_ID + " varchar, "
            + ChatRoomDao.LAST_MSG_ID + " varchar, "
            + ChatRoomDao.LAST_MSG_TIME + " bigint, "
            + ChatRoomDao.TOP_MARK + " varchar, "
            + ChatRoomDao.SHIELD_MARK + " varchar, "
            + ChatRoomDao.UNREAD_NUM + " varchar, "
            + ChatRoomDao.AT_MARK + " varchar, "
            + ChatRoomDao.AT_TYPE + " varchar, "
            + ChatRoomDao.FRIENDSHIP_MARK + " varchar, "
            + ChatRoomDao.USER_ID + " varchar);";

    private final String TABLE_FRIEND = "create table "
            + FriendDao.TABLE_NAME + "("
            + FriendDao.LOCAL_ID + " integer primary key autoincrement, "
            + FriendDao.FRIEND_ID + " varchar , "
            + FriendDao.USERNAME + " varchar, "
            + FriendDao.AVATAR + " varchar, "
            + FriendDao.FRIEND_NOTE + " varchar, "
            + FriendDao.PHONE + " varchar, "
            + FriendDao.SIGN + " varchar, "
            + FriendDao.GENDER + " varchar, "
            + FriendDao.SUBGROUP_ID + " varchar, "
            + FriendDao.SUBGROUP_NAME + " varchar, "
            + FriendDao.SUBGROUP_TYPE + " varchar, "
            + FriendDao.TOP_MARK + " varchar, "
            + FriendDao.SHIELD_MARK + " varchar, "
            + FriendDao.BLACK_MARK + " varchar, "
            + FriendDao.FRIENDSHIP_MARK + " varchar, "
            + FriendDao.USER_ID + " varchar);";

    private final String TABLE_GROUP = "create table "
            + GroupDao.TABLE_NAME + "("
            + GroupDao.LOCAL_ID + " integer primary key autoincrement, "
            + GroupDao.GROUP_ID + " varchar, "
            + GroupDao.GROUP_NAME + " varchar, "
            + GroupDao.GROUP_AVATAR + " varchar, "
            + GroupDao.GROUP_NUM + " varchar, "
            + GroupDao.DESCRIPTION + " varchar, "
            + GroupDao.SEARCH_MARK + " varchar, "
            + GroupDao.VERIFY_MARK + " varchar, "
            + GroupDao.INVITE_MARK + " varchar, "
            + GroupDao.ADD_FRIEND_MARK + " varchar, "
            + GroupDao.GROUP_SPEAK + " varchar, "
            + GroupDao.TOP_MARK + " varchar, "
            + GroupDao.SHIELD_MARK + " varchar, "
            + GroupDao.MEMBER_GRADE + " varchar, "
            + GroupDao.USER_ID + " varchar);";

    private final String TABLE_PHONE_CONTACT = "create table "
            + PhoneContactDao.TABLE_NAME + "("
            + PhoneContactDao.LOCAL_ID + " integer primary key autoincrement, "
            + PhoneContactDao.PHONE + " varchar , "
            + PhoneContactDao.NAME + " varchar, "
            + PhoneContactDao.LOCAL_STATE + " varchar, "
            + PhoneContactDao.INVITE_TIME + " varchar, "
            + PhoneContactDao.USER_ID + " varchar);";

    private final String TABLE_GROUP_MEMBER = "create table "
            + GroupMemberDao.TABLE_NAME + "("
            + GroupMemberDao.LOCAL_ID + " integer primary key autoincrement, "
            + GroupMemberDao.FRIEND_ID + " varchar , "
            + GroupMemberDao.GROUP_ID + " varchar , "
            + GroupMemberDao.USERNAME + " varchar, "
            + GroupMemberDao.AVATAR + " varchar, "
            + GroupMemberDao.FRIEND_NOTE + " varchar, "
            + GroupMemberDao.SIGN + " varchar, "
            + GroupMemberDao.GENDER + " varchar, "
            + GroupMemberDao.GRADE_IN_GROUP + " varchar, "
            + GroupMemberDao.FRIENDSHIP_MARK + " varchar, "
            + GroupMemberDao.USER_ID + " varchar);";

    private final String TABLE_CONTACT_USER = "create table "
            + ContactUserDao.TABLE_NAME + "("
            + ContactUserDao.LOCAL_ID + " integer primary key autoincrement, "
            + ContactUserDao.CONTACT_ID + " varchar , "
            + ContactUserDao.CONTACT_NAME + " varchar, "
            + ContactUserDao.CONTACT_NOTE + " varchar, "
            + ContactUserDao.CONTACT_AVATAR + " varchar, "
            + ContactUserDao.TOP_MARK + " integer, "
            + ContactUserDao.SHIELD_MARK + " integer, "
            + ContactUserDao.USER_ID + " varchar);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USER);
        db.execSQL(TABLE_MESSAGE);
        db.execSQL(TABLE_MESSAGE_SENDING);
        db.execSQL(TABLE_CHAT_ROOM);
        db.execSQL(TABLE_FRIEND);
        db.execSQL(TABLE_GROUP);
        db.execSQL(TABLE_PHONE_CONTACT);
        db.execSQL(TABLE_GROUP_MEMBER);
        db.execSQL(TABLE_CONTACT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.DELETE_MARK+" INTEGER DEFAULT 0");
            case 2:
                //2019-06-18新增回复字段
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_CONTENT+" TEXT");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_CONTENT_TYPE+" INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_DOWNLOAD_PATH+" TEXT");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_DURATION+" INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_FILE_LENGTH+" INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_FILE_NAME+" TEXT");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_FILE_TYPE+" INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_FILE_ID+" TEXT");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_SENDER_ID+" TEXT");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_DOWNLOAD_STATE+" INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_SAVE_PATH+" TEXT");
                db.execSQL("ALTER TABLE "+MessageDao.TABLE_NAME +" ADD "+ MessageDao.REPLY_TAPE_UNREAD_MARK+" INTEGER DEFAULT 0");
                //2019-06-20新增裁剪地址，用于重连后发送
                db.execSQL("ALTER TABLE "+MessageSendingDao.TABLE_NAME +" ADD "+ MessageSendingDao.COMPRESS_PATH+" TEXT");

                break;
            default:
                break;
        }
    }

}
