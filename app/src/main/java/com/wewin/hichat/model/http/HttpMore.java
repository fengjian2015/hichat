package com.wewin.hichat.model.http;

import android.content.Context;

import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.component.constant.HttpCons;
import com.wewin.hichat.component.constant.SpCons;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Darren on 2018/12/28.
 */
public class HttpMore {

    //修改账号信息
    public static void modifyAccountInfo(int audioCues, int gender, String id, String sign,
                                         String username, int vibratesCues, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("audioCues", String.valueOf(audioCues));
        map.put("gender", String.valueOf(gender));
        map.put("id", id);
        map.put("sign", sign);
        map.put("username", username);
        map.put("vibratesCues", String.valueOf(vibratesCues));
        HttpUtil.post(HttpCons.PATH_MORE_MODIFY_ACCOUNT, map, httpCallBack);
    }

    //上传个人头像
    public static void uploadPersonalAvatar(File file, String id, String pass, String suffix, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("pass", pass);
        map.put("suffix", suffix);
        HttpUtil.postFileFormData(HttpCons.PATH_MORE_PERSONAL_AVATAR_LOAD, "file", file, map, httpCallBack);
    }

    //获取服务器时间戳
    public static void getServiceTimestamp(HttpCallBack httpCallBack) {
        HttpUtil.get(HttpCons.PATH_MORE_GET_SERVICE_TIMESTAMP, httpCallBack);
    }

    //获取个人信息
    public static void getPersonalInfo(Context context, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("deviceToken", SpCons.getDeviceTokens(context));
        HttpUtil.post(HttpCons.PATH_MORE_GET_PERSONAL_INFO, map, httpCallBack);
    }

    //获取登录记录
    public static void getLoginRecord(HttpCallBack httpCallBack) {
        HttpUtil.get(HttpCons.PATH_MORE_GET_LOGIN_RECORD, httpCallBack);
    }

    //获取通知列表
    public static void getNotifyList(int limit, int page, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("limit", String.valueOf(limit));
        map.put("page", String.valueOf(page));
        HttpUtil.post(HttpCons.PATH_NOTIFY_LIST, map, httpCallBack);
    }

    //同意好友添加
    public static void agreeFriendAdd(String subgroupId, String id, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("groupId", subgroupId);
        map.put("id", id);
        HttpUtil.post(HttpCons.PATH_NOTIFY_AGREE_ADD, map, httpCallBack);
    }

    //拒绝好友添加
    public static void refuseFriendAdd(String id, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        HttpUtil.post(HttpCons.PATH_NOTIFY_REFUSE_ADD, map, httpCallBack);
    }

    //同意加群
    public static void agreeGroupJoin(String msgId, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("id", msgId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_AGREE_JOIN, map, httpCallBack);
    }

    //拒绝入群
    public static void refuseGroupJoin(String msgId, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("id", msgId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_REFUSE_JOIN, map, httpCallBack);
    }

    //获取未读通知数量
    public static void getNotifyUnreadCount(HttpCallBack httpCallBack) {
        HttpUtil.get(HttpCons.PATH_NOTIFY_GET_UNREAD_COUNT, httpCallBack);
    }

    //设置通知已读
    public static void setNotifyRead(String noticeId, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("noticeId", noticeId);
        HttpUtil.post(HttpCons.PATH_NOTIFY_SET_READ, map, httpCallBack);
    }

    /**
     * 检查版本更新
     *
     * @param versionCode
     * @param httpCallBack
     */
    public static void checkVersion(int versionCode, HttpCallBack httpCallBack) {
        Map<String, String> map = new HashMap<>(2);
        map.put("terminal", "ANDROID");
        map.put("versionCode", versionCode + "");
        HttpUtil.post(HttpCons.PATH_MORE_GET_CHECK_VERSION, map, httpCallBack);
    }
}
