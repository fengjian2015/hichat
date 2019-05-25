package com.wewin.hichat.model.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.model.db.entity.LoginUser;

/**
 * Created by Darren on 2017/7/11.
 */

public class UserDao {

    public static final String TABLE_NAME = "h_user";
    public static final String ID = "id";
    public static final String ACCOUNT = "account";
    public static final String AVATAR = "avatar";
    public static final String EMAIL = "email";
    public static final String GENDER = "gender";
    public static final String IS_VIP = "is_vip";
    public static final String USERNAME = "username";
    public static final String NO = "num";
    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String PHONE = "phone";
    public static final String SIGN = "sign";
    public static final String AUDIO_CUES = "audio_cues";
    public static final String VIBRATES_CUES = "vibrates_cues";

    public static LoginUser user = new LoginUser();

    public static void setUser(LoginUser user) {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.delete(TABLE_NAME, null, null);
        ContentValues values = new ContentValues();
        values.put(ID, user.getId());
        values.put(ACCOUNT, user.getAccount());
        values.put(AVATAR, user.getAvatar());
        values.put(EMAIL, user.getEmail());
        values.put(GENDER, user.getGender());
        values.put(IS_VIP, user.getIsVip());
        values.put(USERNAME, user.getUsername());
        values.put(NO, user.getNo());
        values.put(COUNTRY_CODE, user.getCountryCode());
        values.put(PHONE, user.getPhone());
        values.put(SIGN, user.getSign());
        values.put(AUDIO_CUES, user.getAudioCues());
        values.put(VIBRATES_CUES, user.getVibratesCues());
        db.insert(TABLE_NAME, null, values);
        DbManager.getInstance().closeDatabase();
    }

    public static void deleteUser() {
        SQLiteDatabase db = DbManager.getInstance().openDatabase(true);
        db.execSQL("delete from " + TABLE_NAME);
        DbManager.getInstance().closeDatabase();
    }

    public static LoginUser getUser() {
        LoginUser loginUser = new LoginUser();
        try {
            SQLiteDatabase db = DbManager.getInstance().openDatabase(false);
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    loginUser.setId(cursor.getString(cursor.getColumnIndex(ID)));
                    loginUser.setAccount(cursor.getString(cursor.getColumnIndex(ACCOUNT)));
                    loginUser.setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)));
                    loginUser.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                    loginUser.setGender(cursor.getInt(cursor.getColumnIndex(GENDER)));
                    loginUser.setIsVip(cursor.getString(cursor.getColumnIndex(IS_VIP)));
                    loginUser.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
                    loginUser.setNo(cursor.getString(cursor.getColumnIndex(NO)));
                    loginUser.setCountryCode(cursor.getString(cursor.getColumnIndex(COUNTRY_CODE)));
                    loginUser.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                    loginUser.setSign(cursor.getString(cursor.getColumnIndex(SIGN)));
                    loginUser.setAudioCues(cursor.getInt(cursor.getColumnIndex(AUDIO_CUES)));
                    loginUser.setVibratesCues(cursor.getInt(cursor.getColumnIndex(VIBRATES_CUES)));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbManager.getInstance().closeDatabase();
        }
        return loginUser;
    }

}
