package com.wewin.hichat.model.db.entity;

import android.text.TextUtils;
import com.wewin.hichat.androidlib.utils.UUIDUtil;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

/**
 * Created by Darren on 2018/12/17.
 */
public class ChatMsg implements Serializable {

    public static final int TYPE_CONTENT_TEXT = 0;//文字内容
    public static final int TYPE_CONTENT_VOICE_CALL = 1;//语音通话
    public static final int TYPE_CONTENT_FILE = 2;//文件图片
    public static final int TYPE_CONTENT_AT = 3;//@
    public static final int TYPE_CONTENT_SERVER_ACTION = 4;//服务器消息

    public static final int TYPE_SEND_FAIL = -1;
    public static final int TYPE_SENDING = 0;
    public static final int TYPE_SEND_SUCCESS = 1;

    public static final int TYPE_AT_NORMAL = 0;//非@
    public static final int TYPE_AT_SINGLE = 1;//@我
    public static final int TYPE_AT_ALL = 2;//@全体成员

    public static final int TYPE_READ_TAPE = 1;//设置录音已读
    public static final int TYPE_READ_NORMAL = 2;//设置消息已读

    private String localId;//本地自增id
    private String msgId;//消息id
    private String localMsgId;//本地唯一id

    private String roomId;//房间id
    private String roomType;//friend/group/temporary
    private String senderId;//发送者id
    private String receiverId;//接收者id
    private String groupId;//群id
    private int contentType;//0文本; 1语音通话; 2文件图片; 3@; 4系统消息
    private String content;//消息内容
    private String contentDesc;//版本不支持时的提示内容
    private long createTimestamp;//消息生成时间戳
    private String replyMsgId;//被回复的消息id
    private String unSyncMsgFirstId;//未同步的最早一条消息id
    private Map<String, String> atFriendMap;//被@人<friendId, friendName>
    private FileInfo fileInfo;//文件信息
    private VoiceCall voiceCall;//语音通话
    private FriendInfo senderInfo;//临时会话发送者信息
    private FriendInfo receiverInfo;//临时会话接收者信息
    private int friendshipMark;//好友标识 0 非好友 1 好友
    private int deleteMark;//记录是否本地删除 0未删除 1删除

    private int sendState;//发送状态 -1发送失败； 0发送中；1发送成功；
    private int emoMark;//是否包含表情
    private int phoneMark;//是否包含手机号
    private int urlMark;//是否包含网址
    private int showMark;//是否显示
    private ServerAction serverAction;//系统消息操作
    private boolean checked;//编辑状态是否选中


    private static class ServerAction implements Serializable{
        private int actionType;//系统消息操作类型 0：邀请；1踢人；
        private String actionDesc;//版本不支持时的提示内容

        public int getActionType() {
            return actionType;
        }

        public void setActionType(int actionType) {
            this.actionType = actionType;
        }

        public String getActionDesc() {
            return actionDesc;
        }

        public void setActionDesc(String actionDesc) {
            this.actionDesc = actionDesc;
        }
    }

    public ChatMsg() {
    }

    public ChatMsg(String roomId, String roomType, String senderId, int contentType, String content, long createTimestamp) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.senderId = senderId;
        this.contentType = contentType;
        this.content = content;
        this.createTimestamp = createTimestamp;
    }

    //按createTimestamp正序排列
    public static class TimeRiseComparator implements Comparator<ChatMsg> {
        @Override
        public int compare(ChatMsg o1, ChatMsg o2) {
            if (o1.getCreateTimestamp() - o2.getCreateTimestamp() < 0) {
                return -1;
            } else if (o1.getCreateTimestamp() - o2.getCreateTimestamp() > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof ChatMsg)){
            return false;
        }
        return msgId.equals(((ChatMsg) obj).msgId);
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (!TextUtils.isEmpty(msgId)){
            result = result * 31 + msgId.hashCode();
            return result;
        }else {
            return new Random().nextInt(1000) * 31 + UUIDUtil.get32UUID().hashCode();
        }
    }

    public int getDeleteMark() {
        return deleteMark;
    }

    public void setDeleteMark(int deleteMark) {
        this.deleteMark = deleteMark;
    }

    public FriendInfo getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(FriendInfo receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    public int getEmoMark() {
        return emoMark;
    }

    public void setEmoMark(int emoMark) {
        this.emoMark = emoMark;
    }

    public int getPhoneMark() {
        return phoneMark;
    }

    public void setPhoneMark(int phoneMark) {
        this.phoneMark = phoneMark;
    }

    public int getUrlMark() {
        return urlMark;
    }

    public void setUrlMark(int urlMark) {
        this.urlMark = urlMark;
    }

    public String getUnSyncMsgFirstId() {
        return unSyncMsgFirstId;
    }

    public void setUnSyncMsgFirstId(String unSyncMsgFirstId) {
        this.unSyncMsgFirstId = unSyncMsgFirstId;
    }

    public int getFriendshipMark() {
        return friendshipMark;
    }

    public void setFriendshipMark(int friendshipMark) {
        this.friendshipMark = friendshipMark;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getLocalMsgId() {
        return localMsgId;
    }

    public void setLocalMsgId(String localMsgId) {
        this.localMsgId = localMsgId;
    }

    public FriendInfo getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(FriendInfo senderInfo) {
        this.senderInfo = senderInfo;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public Map<String, String> getAtFriendMap() {
        return atFriendMap;
    }

    public void setAtFriendMap(Map<String, String> atFriendMap) {
        this.atFriendMap = atFriendMap;
    }

    public String getReplyMsgId() {
        return replyMsgId;
    }

    public void setReplyMsgId(String replyMsgId) {
        this.replyMsgId = replyMsgId;
    }

    public ServerAction getServerAction() {
        return serverAction;
    }

    public void setServerAction(ServerAction serverAction) {
        this.serverAction = serverAction;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public VoiceCall getVoiceCall() {
        return voiceCall;
    }

    public void setVoiceCall(VoiceCall voiceCall) {
        this.voiceCall = voiceCall;
    }

    public int getShowMark() {
        return showMark;
    }

    public void setShowMark(int showMark) {
        this.showMark = showMark;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    @Override
    public String toString() {
        return "ChatMsg{" +
                "localId='" + localId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", localMsgId='" + localMsgId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomType='" + roomType + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                ", contentDesc='" + contentDesc + '\'' +
                ", createTimestamp=" + createTimestamp +
                ", replyMsgId='" + replyMsgId + '\'' +
                ", unSyncMsgFirstId='" + unSyncMsgFirstId + '\'' +
                ", atFriendMap=" + atFriendMap +
                ", fileInfo=" + fileInfo +
                ", voiceCall=" + voiceCall +
                ", senderInfo=" + senderInfo +
                ", receiverInfo=" + receiverInfo +
                ", friendshipMark=" + friendshipMark +
                ", deleteMark=" + deleteMark +
                ", sendState=" + sendState +
                ", emoMark=" + emoMark +
                ", phoneMark=" + phoneMark +
                ", urlMark=" + urlMark +
                ", showMark=" + showMark +
                ", serverAction=" + serverAction +
                ", checked=" + checked +
                '}';
    }
}
