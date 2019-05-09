package com.wewin.hichat.androidlib.umeng;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.wewin.hichat.androidlib.utils.LogUtil;
import com.wewin.hichat.androidlib.utils.NotificationUtil;
import com.wewin.hichat.androidlib.utils.ServiceUtil;
import com.wewin.hichat.component.constant.LibCons;
import com.wewin.hichat.component.constant.LoginCons;
import com.wewin.hichat.component.service.ChatSocketService;
import com.wewin.hichat.model.db.dao.UserDao;

import java.util.Map;

import static com.wewin.hichat.component.service.ChatSocketService.NAME_CHAT_SERVICE;

/*
 *   author:jason
 *   date:2019/5/112:35
 *   友盟推送
 */
public class UMMessage {

    private static UMMessage instance = null;
    private PushAgent mPushAgent;
    private NotificationUtil notificationUtil;

    //单例
    public static UMMessage getInstance() {
        if (instance == null) {
            instance = new UMMessage();
        }
        return instance;
    }

    /**
     * 初始化推送
     * @param context
     */
    public void init(final Context context){
        notificationUtil=new NotificationUtil();
        notificationUtil.setNotification(context);
        UMConfigure.init(context, LibCons.UM_APP_KEY, LibCons.UM_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, LibCons.UMENG_MESSAGE_SECRET);
        UMConfigure.setLogEnabled(false);
        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);

        mPushAgent= PushAgent.getInstance(context);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                startChatSocketService(context);
                LogUtil.i(s);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.i(s+"   "+s1);
            }
        });
        setMessageHandler();
    }

    /**
     * 初始化透传消息
     */
    private void setMessageHandler(){
        if(mPushAgent==null)return;
        mPushAgent.setMessageHandler(umengMessageHandler);
    }

    /**
     * 添加别名绑定
     * 登录时需要绑定
     */
    public void setAlias(){
        mPushAgent.setAlias(UserDao.user.getId(), "别名", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.i("别名绑定："+isSuccess+"  "+message);
            }
        });
    }

    /**
     * 移除别名
     * 退出登录时调用
     */
    public void deleteAlias(){
        //移除别名ID
        mPushAgent.deleteAlias(UserDao.user.getId(), "别名", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.i("别名绑定："+isSuccess+"  "+message);
            }
        });
    }

    /**
     * 消息透传
     */
    UmengMessageHandler umengMessageHandler=new UmengMessageHandler(){
        @Override
        public void dealWithCustomMessage(Context context, UMessage uMessage) {
            startChatSocketService(context);
            LogUtil.i(uMessage.custom);
            //启动app
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                Map map=JSONObject.parseObject(uMessage.custom,Map.class);
                if("1".equals(map.get("type"))){
                    //唤起服务
                    return;
                }
                notificationUtil.setContent(map.get("title")+"",map.get("content")+"")
                        .setIntent(intent)
                        .notifyNot();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    //启动ChatSocket服务
    private void startChatSocketService(Context context) {
        if (ServiceUtil.isServiceRunning(context, NAME_CHAT_SERVICE)) {
            return;
        }
        Intent intent = new Intent(context, ChatSocketService.class);
        intent.putExtra(LoginCons.EXTRA_LOGIN_USER_ID, UserDao.user.getId());
        context.startService(intent);
    }
}
