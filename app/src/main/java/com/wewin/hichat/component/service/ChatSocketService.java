package com.wewin.hichat.component.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.constant.HttpCons;
import com.wewin.hichat.component.constant.SpCons;
import com.wewin.hichat.model.socket.ChatSocket;

import io.agora.rtc.utils.ThreadUtils;

/**
 * 检测ChatSocket连接状态，断开则重连
 * Created by Darren on 2018/12/21.
 */
public class ChatSocketService extends IntentService {

    public static final String NAME_CHAT_SERVICE = "com.wewin.hichat.component.service.ChatSocketService";

    public ChatSocketService() {
        super("ChatSocketService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ChatSocket.getInstance().init(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            try {
                Thread.sleep(8 * 1000);
                checkSocketState();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkSocketState() {
        if (!ChatSocket.getInstance().isConnectState()
                && SpCons.getLoginState(getApplicationContext())) {
            ChatSocket.getInstance().reconnect();
        }
    }

}
