package com.wewin.hichat.androidlib.event;

/**
 * 消息实体
 * Created by Darren on 2018/12/17.
 */
public class EventMsg {

    public static final int LOGIN_FINISH = 0;
    public static final int SOCKET_ON_MESSAGE = 1;
    public static final int CONTACT_FRIEND_ADD_FINISH = 2;
    public static final int CONTACT_FRIEND_ADD_REFRESH = 3;
    public static final int MORE_PERSONAL_INFO_REFRESH = 4;
    public static final int MORE_LOGOUT_FINISH = 5;
    public static final int CONTACT_GROUP_CREATE_REFRESH = 6;
    public static final int CONTACT_GROUP_AGREE_JOIN = 7;
    public static final int CONTACT_GROUP_APPLY_JOIN_FINISH = 8;
    public static final int CONTACT_FRIEND_NOTE_REFRESH = 9;
    public static final int CONTACT_SEND_MSG_FINISH = 10;
    public static final int MORE_PERSONAL_AVATAR_REFRESH = 11;
    public static final int CONTACT_GROUP_ANNOUNCEMENT_REFRESH = 12;
    public static final int CONTACT_GROUP_NAME_REFRESH = 14;
    public static final int CONTACT_GROUP_DISBAND = 16;
    public static final int CONTACT_GROUP_AVATAR_REFRESH = 17;
    public static final int CONTACT_FRIEND_SUBGROUP_REFRESH = 18;
    public static final int MESSAGE_VOICE_CALL_REFRESH = 19;
    public static final int CONTACT_FRIEND_DELETE_REFRESH = 20;
    public static final int CONTACT_PHONE_CONTACT_REFRESH = 21;
    public static final int CONVERSATION_CLEAR_REFRESH = 22;
    public static final int CONTACT_FRIEND_BLACK_REFRESH = 24;
    public static final int CONTACT_NOTIFY_REFRESH = 26;
    public static final int CONTACT_GROUP_INVITE_MEMBER_REFRESH = 27;
    public static final int CONTACT_FRIEND_LIST_GET_REFRESH = 29;
    public static final int CONTACT_GROUP_LIST_GET_REFRESH = 30;
    public static final int CONTACT_FRIEND_MAKE_TOP_REFRESH = 31;
    public static final int CONTACT_GROUP_MAKE_TOP_REFRESH = 32;
    public static final int CONTACT_FRIEND_SHIELD_REFRESH = 33;
    public static final int CONTACT_GROUP_SHIELD_REFRESH = 34;
    public static final int CONVERSATION_CHAT_FINISH = 35;
    public static final int CONVERSATION_UNREAD_NUM_REFRESH = 37;
    public static final int CONTACT_FRIEND_AVATAR_REFRESH = 38;
    public static final int CONTACT_GROUP_INFO_REFRESH = 39;
    public static final int CONTACT_GROUP_REMOVE_MEMBER = 42;
    public static final int CONTACT_GROUP_QUIT = 43;
    public static final int CONTACT_DELETE_BY_OTHER = 44;
    public static final int CONTACT_LIST_GET_REFRESH = 45;//ContactUserDao新增或更新
    public static final int CONTACT_FRIEND_NAME_REFRESH = 46;
    public static final int CONTACT_FRIEND_INFO_REFRESH = 47;
    public static final int CONVERSATION_DELETE_ROOM = 50;
    public static final int CONVERSATION_DELETE_ALL_ROOM = 51;
    public static final int CONTACT_GROUP_PERMISSION_REFRESH = 52;
    public static final int CONVERSATION_CHAT_REFRESH = 55;
    public static final int CONTACT_GROUP_SET_MANAGER = 56;
    public static final int CONTACT_FRIEND_LIST_REFRESH = 57;
    public static final int CONTACT_FRIEND_AGREE_REFRESH = 58;//同意加为好友
    public static final int CONVERSATION_SYNC_SERVER_LIST = 59;//同步服务器会话列表
    public static final int DOWN_APK = 60;
    public static final int CONVERSATION_VOICE_CALL_FINISH = 61;
    public static final int CONVERSATION_DELETE_MSG=62;
    public static final int CONVERSATION_REFRESH_AT=63;
    public static final int CONVERSATION_REFRESH_TYPE_AT_NORMAL=64;
    //草稿变化刷新
    public static final int CONVERSATION_REFRESH_DRAFT=65;
    //群组公告新增
    public static final int CONVERSATION_ADD_GROUP_POST=66;
    //链接状态改变通知
    public static final int SOCKET_RECONNECT_STATE=69;

    private int key;
    private Object data;
    private Object secondData;
    private Object thirdData;

    public EventMsg() {
    }

    public EventMsg(int key) {
        this.key = key;
    }

    public EventMsg(int key, Object data) {
        this.key = key;
        this.data = data;
    }

    public EventMsg(int key, Object data, Object secondData) {
        this.key = key;
        this.data = data;
        this.secondData = secondData;
    }

    public EventMsg(int key, Object data, Object secondData, Object thirdData) {
        this.key = key;
        this.data = data;
        this.secondData = secondData;
        this.thirdData = thirdData;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getSecondData() {
        return secondData;
    }

    public void setSecondData(Object secondData) {
        this.secondData = secondData;
    }

    public Object getThirdData() {
        return thirdData;
    }

    public void setThirdData(Object thirdData) {
        this.thirdData = thirdData;
    }

    @Override
    public String toString() {
        return "EventMsg{" +
                "key=" + key +
                ", data=" + data +
                ", secondData=" + secondData +
                ", thirdData=" + thirdData +
                '}';
    }
}
