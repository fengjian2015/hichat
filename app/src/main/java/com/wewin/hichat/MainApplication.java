package com.wewin.hichat;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.wewin.hichat.androidlib.manage.DbManager;
import com.wewin.hichat.androidlib.threadpool.CustomThreadPool;
import com.wewin.hichat.androidlib.umeng.UMMessage;
import com.wewin.hichat.androidlib.utils.EmoticonUtil;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.androidlib.datamanager.EnDecryptUtil;
import com.wewin.hichat.androidlib.utils.MyLifecycleHandler;
import com.wewin.hichat.component.dialog.CallSmallDialog;


/**
 * Created by Darren on 2018/12/13.
 */
public class MainApplication extends Application {

    //全局控制开关
    public static final boolean IS_DEBUG = true;


    @Override
    public void onCreate() {
        super.onCreate();
        //okHttp初始化
        HttpUtil.init(getApplicationContext());
        //加解密工具初始化
        EnDecryptUtil.init(getApplicationContext());
        //线程池初始化
//        CustomThreadPool.getInstance().init();
        //数据库管理初始化
        DbManager.init(getApplicationContext());
        //表情包管理初始化
        EmoticonUtil.init(getApplicationContext());
        //初始化友盟
        UMMessage.getInstance().init(this);
        //用于获取是否处于后台状态
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        //facebook数据库调式工具
        Stetho.initializeWithDefaults(this);
        CallSmallDialog.init(this);
        CustomThreadPool.getInstance().init();
    }

}
