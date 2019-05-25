package com.wewin.hichat.component.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.wewin.hichat.R;
import com.wewin.hichat.androidlib.event.EventMsg;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.utils.FileUtil;
import com.wewin.hichat.androidlib.utils.ToastUtil;
import com.wewin.hichat.androidlib.utils.VersionUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author:jason date:2019/5/2314:29
 */
public class DownApkService extends IntentService {
    /**
     * 是否正在下载中
     */
    public static boolean isDownload = false;
    public final static int ON_PREPARE=0;
    public final static int ON_FAIL=1;
    public final static int ON_PROGRESS=2;
    public final static int ON_COMPLETE=3;

    private static final int NOTIFY_ID = 0;
    private static final String CHANNEL = "update";

    final String CHANNEL_ID = "hi_chat";
    final String CHANNEL_NAME = "hi_chat";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String versionUrl;
    private String versionName;

    /**
     * 定义个更新速率，避免更新通知栏过于频繁导致卡顿
     */
    private float rate = .0f;

    public DownApkService() {
        super("DownApkService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            versionUrl =intent.getStringExtra("url");
            versionName = intent.getStringExtra("versionName");
            downApk();
        }
    }



    /**
     * 创建通知栏
     */
    private void setNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mBuilder = new NotificationCompat.Builder(this, CHANNEL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID)
                    .setContentTitle("正在连接服务器")
                    .setContentText("开始下载")
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_hi_chat));
        } else {
            mBuilder.setContentTitle("开始下载")
                    .setContentText("正在连接服务器")
                    .setSmallIcon(R.mipmap.icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_hi_chat))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis());
        }
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
        }
    }

    /**
     * 下载完成
     */
    private void complete(String msg) {
        if (mBuilder != null) {
            mBuilder.setContentTitle("新版本").setContentText(msg);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            if (mNotificationManager != null) {
                mNotificationManager.notify(NOTIFY_ID, notification);
            }
        }
        stopSelf();
    }

    /**
     * 开始下载apk
     */
    public void downApk() {
        if (isDownload) {
            ToastUtil.showShort(this, getString(R.string.download_now));
            return;
        }
        isDownload = true;
        if (TextUtils.isEmpty(versionUrl)) {
            complete("下载路径错误");
            return;
        }
        setNotification();
        handler.sendEmptyMessage(ON_PREPARE);
        Request request = new Request.Builder().url(versionUrl).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = ON_FAIL;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    Message message = Message.obtain();
                    message.what = ON_FAIL;
                    message.obj = "下载错误";
                    handler.sendMessage(message);
                    return;
                }
                InputStream is = null;
                byte[] buff = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = FileUtil.createAPKFile(DownApkService.this, versionName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (rate != progress) {
                            Message message = Message.obtain();
                            message.what = ON_PROGRESS;
                            message.obj = progress;
                            handler.sendMessage(message);
                            rate = progress;
                        }
                    }
                    fos.flush();
                    Message message = Message.obtain();
                    message.what = ON_COMPLETE;
                    message.obj = file;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = ON_FAIL;
                    message.obj = "下载错误";
                    handler.sendMessage(message);
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 把处理结果放回ui线程
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ON_PREPARE:
                    //准备
                    isDownload = true;
                    EventTrans.post(EventMsg.DOWN_APK, ON_PREPARE);
                    break;

                case ON_FAIL:
                    //失败
                    isDownload = false;
                    if (mNotificationManager != null) {
                        mNotificationManager.cancel(NOTIFY_ID);
                    }
                    EventTrans.post(EventMsg.DOWN_APK, ON_FAIL);
                    stopSelf();
                    break;

                case ON_PROGRESS:
                    //下载进度
                    isDownload = true;
                    EventTrans.post(EventMsg.DOWN_APK, ON_PROGRESS);
                    int progress = (int) msg.obj;
                    mBuilder.setContentTitle("正在下载：新版本...")
                            .setContentText(String.format(Locale.CHINESE, "%d%%", progress))
                            .setProgress(100, progress, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    if (mNotificationManager != null) {
                        mNotificationManager.notify(NOTIFY_ID, notification);
                    }
                    break;
                case ON_COMPLETE:
                    //下载完成
                    EventTrans.post(EventMsg.DOWN_APK, ON_COMPLETE);
                    //app运行在界面,直接安装
                    //否则运行在后台则通知形式告知完成
                    if (mNotificationManager != null) {
                        mNotificationManager.cancel(NOTIFY_ID);
                    }
                    VersionUtil.install(DownApkService.this, (File) msg.obj);
                    isDownload = false;
                    break;
                default:
                    break;
            }
            return false;
        }
    });

}
