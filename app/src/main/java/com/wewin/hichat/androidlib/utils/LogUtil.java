package com.wewin.hichat.androidlib.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wewin.hichat.MainApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Darren on 2019/2/21
 */
public class LogUtil {


    //日志写入文件开关
    private static boolean writeFileSwitch = false;
    // 输出日志类型，v:输出所有信息
    private static char LOG_TYPE = 'v';
    // 文件保存天数
    private static final int MAX_SAVE_DAYS = 1;
    // sdcard中的文件夹名
    private static final String DIR_NAME_MAIN = "HiChat/";
    private static final String LOG_FILE_NAME = "debugLog.txt";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
    private static final SimpleDateFormat DIR_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String TAG_LOG = "HiChat";

    public static void v(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'v');
    }

    public static void v(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'v');
    }

    public static void d(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'd');
    }

    public static void d(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'd');
    }

    public static void i(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'i');
    }

    public static void i(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'i');
    }

    public static void w(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'w');
    }

    public static void w(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'w');
    }

    public static void e(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'e');
    }

    public static void e(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'e');
    }

    public static void setWriteFileSwitch(boolean state) {
        writeFileSwitch = state;
    }

    /**
     * 删除超时的日志
     */
    public static void clearTimeout() {
        // 取得日志存放目录
        String path = getSDDebugLogPath();
        if (!TextUtils.isEmpty(path)) {
            String needDelFile = DIR_FORMAT.format(getDateBefore());
            File file = new File(path, needDelFile + LOG_FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 根据tag, msg和等级，输出日志
     */
    private static void log(String logStr, char level) {
        if (MainApplication.IS_DEBUG) {
            if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.e(TAG_LOG, logStr);
            } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.w(TAG_LOG, logStr);
            } else if ('i' == level && ('i' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.e(TAG_LOG, logStr);
            } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.d(TAG_LOG, logStr);
            } else {
                Log.v(TAG_LOG, logStr);
            }
            if (writeFileSwitch) {
                writeLogToFile(String.valueOf(level), logStr);
            }
        }
    }

    /**
     * 打开日志文件并写入日志
     */
    private static void writeLogToFile(String logType, String logStr) {
        Date nowTime = new Date();
        String filenameTime = DIR_FORMAT.format(nowTime);
        String logContent = "---" + TIME_FORMAT.format(nowTime) + "_" + TAG_LOG + "_" + logType + "_" + logStr + "\n";
        String path = getSDDebugLogPath();

        if (!TextUtils.isEmpty(path)) {
            try {
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File noMediaDir = new File(path + "/" + ".nomedia");
                if (!noMediaDir.exists()) {
                    noMediaDir.mkdir();
                }
                File file = new File(path + "/" + filenameTime + LOG_FILE_NAME);
                FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(logContent);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //需要删除的日志日期
    private static Date getDateBefore() {
        Date nowTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - MAX_SAVE_DAYS);
        return now.getTime();
    }

    //SD卡的根路径
    private static String getSDDebugLogPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!TextUtils.isEmpty(path)) {
                if (!path.endsWith(File.separator)) {
                    return path + File.separator + DIR_NAME_MAIN + "debugLog";
                }
                return path + DIR_NAME_MAIN + "debugLog";
            }
        }
        return "";
    }

    private static String getCurrentClassName() {
        int level = 2;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

}
