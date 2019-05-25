package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

/**
 * 切换扬声器/耳机/听筒
 * @author Darren
 * Created by Darren on 2019/5/23
 **/
public class SpeakerUtil {

    /**
     * 切换扬声器
     */
    public static void setSpeakerphoneOn(Context context) {
        if (context == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    /**
     * 切换听筒
     */
    public static void setCallPhoneOn(Context context) {
        if (context == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(false);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        }
    }

    /**
     * 切换耳机
     */
    public static void setEarphoneOn(Context context) {
        if (context == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(false);
        }
    }

    /**
     * 扬声器是否开启
     */
    public static boolean isSpeakerphoneOn(Context context) {
        if (context == null) {
            return false;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return false;
        }
        return audioManager.isSpeakerphoneOn();
    }


}
