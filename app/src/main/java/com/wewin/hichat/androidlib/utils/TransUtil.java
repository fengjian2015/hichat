package com.wewin.hichat.androidlib.utils;

/**
 * Created by Darren on 2019/3/6
 */
public class TransUtil {

    public static int booleanToInt(boolean state){
        if (state){
            return 1;
        }else {
            return 0;
        }
    }

    public static boolean intToBoolean(int num){
        return num == 1;
    }

}
