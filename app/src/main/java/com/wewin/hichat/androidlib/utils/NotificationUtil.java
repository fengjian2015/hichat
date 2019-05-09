package com.wewin.hichat.androidlib.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.wewin.hichat.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/*
 *   author:jason
 *   date:2019/5/113:34
 */
public class NotificationUtil {
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    final String CHANNEL_ID = "hi_chat_message";
    final String CHANNEL_NAME = "hi_chat_name_message";

    private final String CHANNEL = "message";
    private Context context;

    /**
     * 创建通知栏
     */
    public NotificationUtil setNotification(Context c) {
        this.context = c;
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context, CHANNEL);
        return this;
    }

    public NotificationUtil setContent(CharSequence title, CharSequence content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_hi_chat));
        } else {
            mBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.icon_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_hi_chat))
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis());
        }
        return this;
    }

    /**
     * 添加跳转
     *
     * @param intent
     */
    public NotificationUtil setIntent(Intent intent) {
        if (mBuilder == null) return this;
        int s=(int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, s, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        return this;
    }

    /**
     * 显示通知
     *
     * @return
     */
    public NotificationUtil notifyNot() {
        if (mNotificationManager == null) return this;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        return this;
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
