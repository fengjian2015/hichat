package com.wewin.hichat.model.http;

import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.component.constant.HttpCons;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Darren on 2018/12/26.
 */
public class HttpNotify {

    //获取通知列表
    public static void getNotifyList(String userId, int page, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("page", String.valueOf(page));
        HttpUtil.post(HttpCons.PATH_NOTIFY_LIST, map, httpCallBack);
    }

    //同意好友添加
    public static void agreeFriendAdd(String subgroupId, String id, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("groupId", subgroupId);
        map.put("id", id);
        HttpUtil.post(HttpCons.PATH_NOTIFY_AGREE_ADD, map, httpCallBack);
    }

    //拒绝好友添加
    public static void refuseFriendAdd(String id, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        HttpUtil.post(HttpCons.PATH_NOTIFY_REFUSE_ADD, map, httpCallBack);
    }

    //同意加群
    public static void agreeGroupJoin(String msgId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("id", msgId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_AGREE_JOIN, map, httpCallBack);
    }

    //拒绝入群
    public static void refuseGroupJoin(String msgId, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("id", msgId);
        HttpUtil.post(HttpCons.PATH_CONTACT_GROUP_REFUSE_JOIN, map, httpCallBack);
    }


}
