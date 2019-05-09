package com.wewin.hichat.component.constant;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Darren on 2018/12/13.
 */
public class SpCons {

    private static final String FILE_NAME = "HiChat";
    public static final String SP_KEY_FRIEND_SUBGROUP = "SP_KEY_FRIEND_SUBGROUP";

    public static void setLoginState(Context context, boolean state){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loginState", state);
        editor.apply();
    }

    public static boolean getLoginState(Context context){
        if (context == null){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("loginState", false);
    }

    public static void setCuid (Context context, String cuid){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cuid", cuid);
        editor.apply();
    }

    public static String getCuid(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString("cuid", "");
    }

    public static void setDomain(Context context, String domain){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("domain", domain);
        editor.apply();
    }

    public static String getDomain(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString("domain", "");
    }

    //在线状态
    public static void setOnlineType(Context context, int type){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("onlineType", type);
        editor.apply();
    }

    public static int getOnlineType(Context context){
        if (context == null){
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("onlineType", 0);
    }

    //是否开启声音提醒
    public static void setVoiceNotifyState(Context context, boolean state){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("voiceNotify", state);
        editor.apply();
    }

    public static boolean getVoiceNotifyState(Context context){
        if (context == null){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("voiceNotify", false);
    }

    public static void setTimeChanged(Context context, boolean state){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("timeChanged", state);
        editor.apply();
    }

    public static boolean getTimeChanged(Context context){
        if (context == null){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("timeChanged", false);
    }

    public static void setSystemTimestampRight(Context context, boolean state){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("systemTimestampRight", state);
        editor.apply();
    }

    public static boolean getSystemTimestampRight(Context context){
        if (context == null){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("systemTimestampRight", false);
    }

    //保存键盘高度
    public static void setKeyboardHeight(Context context, int height){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("keyboardHeight", height);
        editor.apply();
    }

    public static int getKeyboardHeight(Context context){
        if (context == null){
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("keyboardHeight", 0);
    }

    //最新的群公告title
    public static void setGroupAnnouncement(Context context, String title){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("groupAnnouncementTitle", title);
        editor.apply();
    }

    public static String getGroupAnnoucement(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("groupAnnouncementTitle", "");
    }

    public static void setCountryName(Context context, String countryName){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("countryName", countryName);
        editor.apply();
    }

    public static String getCountryName(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("countryName", "");
    }

    public static void setCountryCode(Context context, String countryCode){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("countryCode", countryCode);
        editor.apply();
    }

    public static String getCountryCode(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("countryCode", "");
    }

    public static void setCurrentRoomId(Context context, String chatId){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("currentChatId", chatId);
        editor.apply();
    }

    public static String getCurrentRoomId(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("currentChatId", "");
    }

    public static void setCurrentRoomType(Context context, String msgType){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("currentChatType", msgType);
        editor.apply();
    }

    public static String getCurrentRoomType(Context context){
        if (context == null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("currentChatType", "");
    }

    public static void setNotifyRedPointVisible(Context context, boolean state){
        if (context == null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("mainNotifyPoint", state);
        editor.apply();
    }

    public static boolean getNotifyRedPointVisible(Context context){
        if (context == null){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getBoolean("mainNotifyPoint", false);
    }

}
