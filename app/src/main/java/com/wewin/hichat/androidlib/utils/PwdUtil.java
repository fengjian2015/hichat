package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Darren
 * Created by Darren on 2019/2/7
 */
public class PwdUtil {

    private static Pattern numPattern = Pattern.compile("[0-9]*");
    private static Pattern letterPattern = Pattern.compile("[a-zA-Z]");
    private static Pattern characterPattern = Pattern.compile("[\u4e00-\u9fa5]");

    /**
     * 必须包含字母和数字
     */
    public static boolean isLetterAndDigit(String str) {
        boolean isLetter = false;
        boolean isDigit = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                isLetter = true;
            } else if (Character.isDigit(str.charAt(i))) {
                isDigit = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        return isLetter && isDigit && str.matches(regex);
    }

    /**
     * 至少包含大小写字母及数字中的一种
     */
    public static boolean isLetterOrDigit(String str) {
        //定义一个boolean值，用来表示是否包含字母或数字
        boolean isLetterOrDigit = false;
        for (int i = 0; i < str.length(); i++) {
            //用char包装类中的判断数字的方法判断每一个字符
            if (Character.isLetterOrDigit(str.charAt(i))) {
                isLetterOrDigit = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        return isLetterOrDigit && str.matches(regex);
    }

    /**
     * 至少包含大小写字母及数字中的两种
     */
    public static boolean isLetterDigit(String str) {
        //定义一个boolean值，用来表示是否包含数字
        boolean isDigit = false;
        //定义一个boolean值，用来表示是否包含字母
        boolean isLetter = false;
        for (int i = 0; i < str.length(); i++) {
            //用char包装类中的判断数字的方法判断每一个字符
            if (Character.isDigit(str.charAt(i))) {
                isDigit = true;
                //用char包装类中的判断字母的方法判断每一个字符
            } else if (Character.isLetter(str.charAt(i))) {
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        return isDigit && isLetter && str.matches(regex);
    }

    /**
     * 必须同时包含大小写字母及数字
     */
    public static boolean isContainAll(String str) {
        //定义一个boolean值，用来表示是否包含数字
        boolean isDigit = false;
        //定义一个boolean值，用来表示是否包含字母
        boolean isLowerCase = false;
        boolean isUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            //用char包装类中的判断数字的方法判断每一个字符
            if (Character.isDigit(str.charAt(i))) {
                isDigit = true;
            } else if (Character.isLowerCase(str.charAt(i))) {
                //用char包装类中的判断字母的方法判断每一个字符
                isLowerCase = true;
            } else if (Character.isUpperCase(str.charAt(i))) {
                isUpperCase = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        return isDigit && isLowerCase && isUpperCase && str.matches(regex);
    }

    /**
     * 判断EditText输入的数字、中文还是字母方法
     */
    public static void whatIsInput(Context context, EditText edInput) {
        String txt = edInput.getText().toString();

        Matcher matcher = numPattern.matcher(txt);
        if (matcher.matches()) {
            Toast.makeText(context, "输入的是数字", Toast.LENGTH_SHORT).show();
        }
        matcher = letterPattern.matcher(txt);
        if (matcher.matches()) {
            Toast.makeText(context, "输入的是字母", Toast.LENGTH_SHORT).show();
        }
        matcher = characterPattern.matcher(txt);
        if (matcher.matches()) {
            Toast.makeText(context, "输入的是汉字", Toast.LENGTH_SHORT).show();
        }
    }


}
