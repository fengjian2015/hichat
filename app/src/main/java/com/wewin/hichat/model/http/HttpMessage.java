package com.wewin.hichat.model.http;

import com.wewin.hichat.androidlib.impl.HttpCallBack;
import com.wewin.hichat.androidlib.utils.HttpUtil;
import com.wewin.hichat.component.constant.HttpCons;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Darren on 2019/1/28
 */
public class HttpMessage {

    //设置聊天消息已读
    public static void setMessageRead(String fromId, String msgType, String msgId, int readType,
                                                HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("fromId", fromId);
        map.put("type", msgType);
        map.put("msgId", msgId);
        map.put("status", String.valueOf(readType));
        HttpUtil.post(HttpCons.PATH_MESSAGE_SET_READ, map, httpCallBack);
    }

    //清空会话记录列表
    public static void clearConversationList(HttpCallBack httpCallBack){
        HttpUtil.get(HttpCons.PATH_CONVERSATION_CLEAR_ALL, httpCallBack);
    }

    //删除单个会话记录
    public static void deleteSingleConversation(String chatId, String msgType, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("id", chatId);
        map.put("type", msgType);
        HttpUtil.post(HttpCons.PATH_CONVERSATION_DELETE_SINGLE, map, httpCallBack);
    }

    //删除多个会话记录
    public static void deleteMultiConversation(String idStr, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("idStr", idStr);
        HttpUtil.post(HttpCons.PATH_CONVERSATION_DELETE_LIST, map, httpCallBack);
    }

    //增量获取会话列表
    public static void getServerConversationList(String sessionStr, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("sessionStr", sessionStr);
        HttpUtil.post(HttpCons.PATH_CONVERSATION_GET_LIST, map, httpCallBack);
    }

    //查询会话消息记录列表
    public static void getServerMessageList(String conversationId, String conversationType,
                                            int firstMark, int limit, String msgId,
                                            HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>();
        map.put("conversationId", conversationId);
        map.put("conversationType", conversationType);
        map.put("firstMark", String.valueOf(firstMark));
        map.put("limit", String.valueOf(limit));
        map.put("msgId", msgId);
        HttpUtil.post(HttpCons.PATH_MESSAGE_GET_LIST, map, httpCallBack);
    }


    //删除单个消息
    public static void removeMessage(String msgId, String roomId,String roomType, HttpCallBack httpCallBack){
        Map<String, String> map = new HashMap<>(3);
        map.put("msgId", msgId);
        map.put("sessionId", roomId);
        map.put("sessionType", roomType);
        HttpUtil.post(HttpCons.PATH_MESSAGE_REMOVE_MESSAGE, map, httpCallBack);
    }
}
