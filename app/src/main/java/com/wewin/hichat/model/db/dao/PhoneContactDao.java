package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.model.db.entity.PhoneContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darren on 2019/2/15
 */
public class PhoneContactDao {

    public static final String TABLE_NAME = "h_phoneContact";
    public static final String LOCAL_ID = "local_id";
    public static final String PHONE = "phone";
    public static final String NAME = "name";
    public static final String LOCAL_STATE = "local_state";
    public static final String INVITE_TIME = "invite_time";
    public static final String USER_ID = "user_id";

    public static void addPhoneContactList(List<PhoneContact> contactList) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.beginTransaction();
        try {
            String whereSql = PHONE + " =? and " + USER_ID + " =?";
            for (PhoneContact contact : contactList) {
                if (getPhoneContact(contact.getPhone()) != null) {
                    db.update(TABLE_NAME, packContentValue(contact), whereSql,
                            new String[]{contact.getPhone(), UserDao.user.getId()});
                } else {
                    db.insert(TABLE_NAME, null, packContentValue(contact));
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

    public static List<PhoneContact> getPhoneContactList() {
        List<PhoneContact> contactList = new ArrayList<>();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + USER_ID + " = ?",
                    new String[]{UserDao.user.getId()});
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contactList.add(parseCursorData(cursor));
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactList;
    }

    public static void updatePhoneContact(PhoneContact contact) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.replace(TABLE_NAME, null, packContentValue(contact));
        DbManager.getInstance().closeDatabase();
    }

    private static PhoneContact getPhoneContact(String phone) {
        PhoneContact contact = null;
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + PHONE + " =? and " +
                    USER_ID + " = ?", new String[]{phone, UserDao.user.getId()});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    contact = parseCursorData(cursor);
                }
                cursor.close();
            }
            DbManager.getInstance().closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contact;
    }

    private static ContentValues packContentValue(PhoneContact contact) {
        ContentValues values = new ContentValues();
        values.put(PHONE, contact.getPhone());
        values.put(NAME, contact.getName());
        values.put(LOCAL_STATE, contact.getLocalState());
        values.put(INVITE_TIME, contact.getInviteTime());
        values.put(USER_ID, UserDao.user.getId());
        return values;
    }

    private static PhoneContact parseCursorData(Cursor cursor) {
        PhoneContact contact = new PhoneContact();
        contact.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
        contact.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        contact.setLocalState(cursor.getInt(cursor.getColumnIndex(LOCAL_STATE)));
        contact.setInviteTime(cursor.getLong(cursor.getColumnIndex(INVITE_TIME)));
        return contact;
    }

}
