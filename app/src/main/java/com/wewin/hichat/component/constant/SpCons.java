package com.wewin.hichat.component.constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.wewin.hichat.androidlib.datamanager.EnDecryptUtil;
import com.wewin.hichat.androidlib.utils.ActivityUtil;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.model.db.entity.LoginUser;

/**
 * @author Darren
 * Created by Darren on 2018/12/13.
 */
public class SpCons {

    private static final String FILE_NAME = "HiChat";
    public static final String SP_KEY_FRIEND_SUBGROUP = "SP_KEY_FRIEND_SUBGROUP";

    public static final String VERSION_CONTENT="version_content";
    public static final String SUBGROUP_LIST="subgroup_list";
    public static final String USER_RECORD="user_record";
    public static final String INVITE_TEMPLATE="invite_template";
    /**
     * 存储字符串sp
     * @param key
     * @param value
     */
    public static void setString(Context context,String key, String value) {
        if(!ActivityUtil.isActivityOnTop(context)){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);

        SharedPreferences.Editor edit = sp.edit();

        edit.putString(key, value);

        edit.commit();

    }

    /**
     * 取出字符串sp
     * @param value
     * @return
     */
    public static String getString(Context context,String value) {
        if(!ActivityUtil.isActivityOnTop(context)){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);

        String s = sp.getString(value, "");

        return s;

    }

    /**
     * 登录状态
     */
    public static void setLoginState(Context context, boolean state) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loginState", state);
        editor.apply();
    }

    public static boolean getLoginState(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("loginState", false);
    }

    public static void setUser(Context context, LoginUser loginUser) {
        if (context == null || loginUser == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        byte[] byteArr = EnDecryptUtil.obj2byteArr(loginUser);
        if (byteArr != null) {
            editor.putString("loginUser", EnDecryptUtil.parseByte2HexStr(byteArr));
            editor.apply();
        } else {
            LogUtil.i("EnDecryptUtil.obj2byteArr(loginUser) == null");
        }
    }

    public static LoginUser getUser(Context context){
        if (context == null) {
            return new LoginUser();
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String result = sp.getString("loginUser", "");
        if (!TextUtils.isEmpty(result)) {
            return (LoginUser) EnDecryptUtil.byteArr2Obj(EnDecryptUtil.parseHexStr2Byte(result));
        }
        return new LoginUser();
    }

    public static void setCuid(Context context, String cuid) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cuid", cuid);
        editor.apply();
    }

    public static String getCuid(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString("cuid", "");
    }

    public static void setDomain(Context context, String domain) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("domain", domain);
        editor.apply();
    }

    public static String getDomain(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString("domain", "");
    }

    /**
     * 保存键盘高度
     */
    public static void setKeyboardHeight(Context context, int height) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("keyboardHeight", height);
        editor.apply();
    }

    public static int getKeyboardHeight(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("keyboardHeight", 0);
    }

    public static void setCountryName(Context context, String countryName) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("countryName", countryName);
        editor.apply();
    }

    public static String getCountryName(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("countryName", "");
    }

    public static void setCountryCode(Context context, String countryCode) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("countryCode", countryCode);
        editor.apply();
    }

    public static String getCountryCode(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("countryCode", "");
    }

    /**
     * 通知红点是否需要显示
     */
    public static void setNotifyRedPointVisible(Context context, boolean state) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("mainNotifyPoint", state);
        editor.apply();
    }

    public static boolean getNotifyRedPointVisible(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("mainNotifyPoint", false);
    }
    public static void setDeviceTokens(Context context, String deviceTokens){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("device_tokens", deviceTokens);
        editor.apply();
    }

    public static String getDeviceTokens(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("device_tokens", "");
    }

    /**
     * 保存小窗口X值
     */
    public static void setVMParamsX(Context context, int x) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("VMParamsX", x);
        editor.apply();
    }

    public static int getVMParamsX(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("VMParamsX", 0);
    }

    /**
     * 保存小窗口Y值
     */
    public static void setVMParamsY(Context context, int y) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("VMParamsY", y);
        editor.apply();
    }

    public static int getVMParamsY(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("VMParamsY", 0);
    }



}
