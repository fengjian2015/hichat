package com.wewin.hichat.androidlib.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Darren on 2019/1/14.
 */
public class CrashUtil implements Thread.UncaughtExceptionHandler {

    private static CrashUtil instance;
    private Context context;

    public static CrashUtil getInstance() {
        if (instance == null) {
            instance = new CrashUtil();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    //当程序crash 会回调此方法， Throwable中存放这错误日志
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        String logPath;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            logPath = FileUtil.getSDDirPath(context) + "ErrorLog";
            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                FileWriter fw = new FileWriter(logPath + File.separator
                        + "myErrorLog.txt", true);
                fw.write(TimeUtil.getCurrentTime() + "错误原因：\n");
                // 错误信息
                // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = arg1.getStackTrace();
                fw.write(arg1.getMessage() + "\n");
                for (int i = 0; i < stackTrace.length; i++) {
                    fw.write("file:" + stackTrace[i].getFileName() + " class:"
                            + stackTrace[i].getClassName() + " method:"
                            + stackTrace[i].getMethodName() + " line:"
                            + stackTrace[i].getLineNumber() + "\n");
                }
                fw.write("\n");
                fw.close();
                // 上传错误信息到服务器
                // uploadToServer();
            } catch (IOException e) {
                Log.e("crash util", "load file failed...", e.getCause());
            }
        }
        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
