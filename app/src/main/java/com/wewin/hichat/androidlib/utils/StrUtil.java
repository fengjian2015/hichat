package com.wewin.hichat.androidlib.utils;

/**
 * Created by Darren on 2019/1/9.
 */
public class StrUtil {

    public static String formatFileLength(long originLength) {
        if (originLength < 1024) {
            return originLength + "B";
        } else if (originLength < 1024 * 1024) {
            return originLength / 1024 + "KB";
        } else if (originLength < 1024 * 1024 *1024){
            return originLength / 1024 / 1024 + "MB";
        }else {
            return originLength / 1024 / 1024 / 1024 + "G";
        }
    }

    /**
     * 从下载连接中解析出文件名
     */
    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 校验手机号
     */
    public static boolean isPhoneNumRight(String code, String phoneNum){
        /*String patternStrCN = "\\+861[34578]\\d{9}$";
        String patternStrHK = "\\+852[5689]\\d{7}$/";
        String patternStrMC = "\\+8536[68]\\d{5}$/";
        String patternStrTW = "\\+886[0][9]\\d{8}$/";
        String patternStrJP = "\\+810[789]0\\d{8}$/";
        String patternStrKR = "\\+8201[0179]\\d{8}$/";
        String patternStrVN = "\\+840[19]\\d{9}$/";
        String patternStrKH = "\\+855[1-9]\\d{7}$/";
        String patternStrPH = "\\+630[9]\\d{9}$/";
        String patternStrMY = "\\+6001[02346789]\\d{7}$/";
        String patternStrID = "\\+6208[1]\\d{7,9}$/";

        return Pattern.compile(patternStrCN).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrHK).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrMC).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrTW).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrJP).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrKR).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrVN).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrKH).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrPH).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrMY).matcher(code + phoneNum).matches()
                || Pattern.compile(patternStrID).matcher(code + phoneNum).matches();*/
        return true;

    }


}
