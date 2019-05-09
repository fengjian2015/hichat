package com.wewin.hichat.component.constant;

/**
 * Created by Darren on 2018/12/22.
 */
public class ContactCons {

    public static final String EXTRA_CONTACT_GROUP_INFO = "EXTRA_CONTACT_GROUP_INFO";
    public static final String EXTRA_CONTACT_GROUP_ANNOUNCEMENT = "EXTRA_CONTACT_GROUP_ANNOUNCEMENT";
    public static final String EXTRA_CONTACT_GROUP_ID = "EXTRA_CONTACT_GROUP_ID";
    public static final String EXTRA_CONTACT_GROUP_MAX = "EXTRA_CONTACT_GROUP_MAX";
    public static final String EXTRA_CONTACT_FRIEND_SEARCH_TITLE = "EXTRA_CONTACT_FRIEND_SEARCH_TITLE";
    public static final String EXTRA_CONTACT_FRIEND_LIST = "EXTRA_CONTACT_FRIEND_LIST";
    public static final String EXTRA_CONTACT_FRIEND_INFO = "EXTRA_CONTACT_FRIEND_INFO";
    public static final String EXTRA_CONTACT_FRIEND_ID = "EXTRA_CONTACT_FRIEND_ID";
    public static final String EXTRA_CONTACT_FRIEND_SUBGROUP_ID = "EXTRA_CONTACT_FRIEND_SUBGROUP_ID";
    public static final String EXTRA_CONTACT_FRIEND_SUBGROUP_NAME = "EXTRA_CONTACT_FRIEND_SUBGROUP_NAME";
    public static final String EXTRA_MESSAGE_CHAT_VIDEO_PATH = "EXTRA_MESSAGE_CHAT_VIDEO_PATH";
    public static final String EXTRA_MESSAGE_CHAT_FILE = "EXTRA_MESSAGE_CHAT_FILE";
    public static final String EXTRA_MESSAGE_CHAT_WEB_PATH = "EXTRA_MESSAGE_CHAT_WEB_PATH";
    public static final String EXTRA_MESSAGE_CHAT_CALL_TYPE = "EXTRA_MESSAGE_CHAT_CALL_TYPE";
    public static final String EXTRA_MESSAGE_CHAT_CALL_CHANNEL = "EXTRA_MESSAGE_CHAT_CALL_CHANNEL";
    public static final String EXTRA_MESSAGE_CHAT_START_TIMESTAMP = "EXTRA_MESSAGE_CHAT_START_TIMESTAMP";
    public static final String EXTRA_MESSAGE_UNREAD_NUM = "EXTRA_MESSAGE_UNREAD_NUM";
    public static final String EXTRA_CONTACT_GROUP_IS_MANAGER = "EXTRA_CONTACT_GROUP_IS_MANAGER";
    public static final String EXTRA_CONTACT_GROUP_ALLOW_INVITE = "EXTRA_CONTACT_GROUP_ALLOW_INVITE";
    public static final String EXTRA_CONTACT_CHAT_ROOM = "EXTRA_CONTACT_CHAT_ROOM";
    public static final String EXTRA_CONTACT_CHAT_MSG = "EXTRA_CONTACT_CHAT_MSG";

    public static final int TYPE_GROUP_MEMBER_TOTAL = -1;
    public static final int TYPE_GROUP_MEMBER_OWNER = 2;

    public static final int REQ_CONTACT_FRIEND_GROUPING_SELECT = 200;
    public static final int REQ_CONTACT_FRIEND_LIST_SELECT = 201;
    public static final int REQ_CONVERSATION_LIST_CLICK = 202;

    /*ChatMsg*/
    public static final int TYPE_MSG_CONTENT_TEXT = 0;//文本类
    public static final int TYPE_MSG_CONTENT_VOICE = 1;//语音类
    public static final int TYPE_MSG_CONTENT_FILE = 2;//文件图片类
    public static final int TYPE_MSG_CONTENT_TAPE_RECORD = 3;//录音类

    /*Subgroup*/
    public static final int TYPE_SUBGROUP_NORMAL = 0;
    public static final int TYPE_SUBGROUP_FRIEND = 1;
    public static final int TYPE_SUBGROUP_BLACK = 2;
    public static final int TYPE_SUBGROUP_PHONE_CONTACT = 3;


}
