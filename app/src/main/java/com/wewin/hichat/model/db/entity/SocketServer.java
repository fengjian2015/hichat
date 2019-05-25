package com.wewin.hichat.model.db.entity;

import java.util.List;

/**
 * Created by Darren on 2019/4/23
 **/
public class SocketServer {

    //syncAccountInfo; syncFriendCommand; syncMessageCommand; syncGroupInfo; syncGroupCommand;
    private String action;
    private ServerData data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ServerData getData() {
        return data;
    }

    public void setData(ServerData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SocketServer{" +
                "action='" + action + '\'' +
                ", data=" + data +
                '}';
    }

    public static class ServerData{
        //updAvatar; updGender; updSign; updUsername; updPassword; delFriend; updFriendNote; delSubgroup;
        //shieldFriend; blackFriend; topFriendMsg; pushTyping; addSubgroup; moveSubgroup; renameSubgroup;
        //agreeFriend; refuseFriend; removeFriendSession; removeGroupSession; clearSessionList;
        //chatMsgRead; runMsgBox; closeMsgBox; createGroup; updAvatar; updDesc; updGroupName;
        //updGroupPermission; agreeGroup; inviteFriendJoin; addGroupPost; delGroupPost; modifyGroupPost;
        //giveAdmin; cancelAdmin; expelMember; quitGroup;
        private String cmd;
        private String fromUid;
        private String fromGroupId;
        private String executorId;
        private String recipientIdStr;
        private String groupId;
        private String localMsgId;
        private String msgId;
        private String receiverId;
        private String roomType;
        private LoginUser userInfo;
        private FriendData friendData;
        private FriendInfo friendInfo;
        private MessageInfo messageInfo;
        private GroupData groupInfo;
        private FriendInfo executorInfo;
        private List<FriendInfo> recipientInfo;
        private GroupPostInfo groupPostInfo;
        private Notify notifyInfo;

        public Notify getNotifyInfo() {
            return notifyInfo;
        }

        public void setNotifyInfo(Notify notifyInfo) {
            this.notifyInfo = notifyInfo;
        }

        public String getFromUid() {
            return fromUid;
        }

        public void setFromUid(String fromUid) {
            this.fromUid = fromUid;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getFromGroupId() {
            return fromGroupId;
        }

        public void setFromGroupId(String fromGroupId) {
            this.fromGroupId = fromGroupId;
        }

        public String getExecutorId() {
            return executorId;
        }

        public void setExecutorId(String executorId) {
            this.executorId = executorId;
        }

        public String getRecipientIdStr() {
            return recipientIdStr;
        }

        public void setRecipientIdStr(String recipientIdStr) {
            this.recipientIdStr = recipientIdStr;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getLocalMsgId() {
            return localMsgId;
        }

        public void setLocalMsgId(String localMsgId) {
            this.localMsgId = localMsgId;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public LoginUser getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(LoginUser userInfo) {
            this.userInfo = userInfo;
        }

        public FriendData getFriendData() {
            return friendData;
        }

        public void setFriendData(FriendData friendData) {
            this.friendData = friendData;
        }

        public FriendInfo getFriendInfo() {
            return friendInfo;
        }

        public void setFriendInfo(FriendInfo friendInfo) {
            this.friendInfo = friendInfo;
        }

        public MessageInfo getMessageInfo() {
            return messageInfo;
        }

        public void setMessageInfo(MessageInfo messageInfo) {
            this.messageInfo = messageInfo;
        }

        public GroupData getGroupInfo() {
            return groupInfo;
        }

        public void setGroupInfo(GroupData groupInfo) {
            this.groupInfo = groupInfo;
        }

        public FriendInfo getExecutorInfo() {
            return executorInfo;
        }

        public void setExecutorInfo(FriendInfo executorInfo) {
            this.executorInfo = executorInfo;
        }

        public List<FriendInfo> getRecipientInfo() {
            return recipientInfo;
        }

        public void setRecipientInfo(List<FriendInfo> recipientInfo) {
            this.recipientInfo = recipientInfo;
        }

        public GroupPostInfo getGroupPostInfo() {
            return groupPostInfo;
        }

        public void setGroupPostInfo(GroupPostInfo groupPostInfo) {
            this.groupPostInfo = groupPostInfo;
        }

        @Override
        public String toString() {
            return "ServerData{" +
                    "cmd='" + cmd + '\'' +
                    ", fromUid='" + fromUid + '\'' +
                    ", fromGroupId='" + fromGroupId + '\'' +
                    ", executorId='" + executorId + '\'' +
                    ", recipientIdStr='" + recipientIdStr + '\'' +
                    ", groupId='" + groupId + '\'' +
                    ", localMsgId='" + localMsgId + '\'' +
                    ", msgId='" + msgId + '\'' +
                    ", receiverId='" + receiverId + '\'' +
                    ", roomType='" + roomType + '\'' +
                    ", userInfo=" + userInfo +
                    ", friendData=" + friendData +
                    ", friendInfo=" + friendInfo +
                    ", messageInfo=" + messageInfo +
                    ", groupInfo=" + groupInfo +
                    ", executorInfo=" + executorInfo +
                    ", recipientInfo=" + recipientInfo +
                    ", groupPostInfo=" + groupPostInfo +
                    ", notifyInfo=" + notifyInfo +
                    '}';
        }
    }

    public static class FriendData{
        private String friendId;
        private String friendNote;
        private String subgroupId;
        private String subgroupName;

        public String getFriendId() {
            return friendId;
        }

        public void setFriendId(String friendId) {
            this.friendId = friendId;
        }

        public String getFriendNote() {
            return friendNote;
        }

        public void setFriendNote(String friendNote) {
            this.friendNote = friendNote;
        }

        public String getSubgroupId() {
            return subgroupId;
        }

        public void setSubgroupId(String subgroupId) {
            this.subgroupId = subgroupId;
        }

        public String getSubgroupName() {
            return subgroupName;
        }

        public void setSubgroupName(String subgroupName) {
            this.subgroupName = subgroupName;
        }
    }

    public static class GroupData{
        private int addFriendMark;
        private String avatar;
        private String description;
        private String groupName;
        private int inviteMark;
        private int searchMark;
        private int speechMark;
        private int verifyMark;

        public int getAddFriendMark() {
            return addFriendMark;
        }

        public void setAddFriendMark(int addFriendMark) {
            this.addFriendMark = addFriendMark;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getInviteMark() {
            return inviteMark;
        }

        public void setInviteMark(int inviteMark) {
            this.inviteMark = inviteMark;
        }

        public int getSearchMark() {
            return searchMark;
        }

        public void setSearchMark(int searchMark) {
            this.searchMark = searchMark;
        }

        public int getSpeechMark() {
            return speechMark;
        }

        public void setSpeechMark(int speechMark) {
            this.speechMark = speechMark;
        }

        public int getVerifyMark() {
            return verifyMark;
        }

        public void setVerifyMark(int verifyMark) {
            this.verifyMark = verifyMark;
        }
    }

    public static class MessageInfo{
        private String conversationId;
        private String conversationType;
        private String expireMessage;
        private String msgId;
        private int msgRecordMark;
        private int shieldMark;
        private int topMark;

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

        public String getConversationType() {
            return conversationType;
        }

        public void setConversationType(String conversationType) {
            this.conversationType = conversationType;
        }

        public String getExpireMessage() {
            return expireMessage;
        }

        public void setExpireMessage(String expireMessage) {
            this.expireMessage = expireMessage;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public int getMsgRecordMark() {
            return msgRecordMark;
        }

        public void setMsgRecordMark(int msgRecordMark) {
            this.msgRecordMark = msgRecordMark;
        }

        public int getShieldMark() {
            return shieldMark;
        }

        public void setShieldMark(int shieldMark) {
            this.shieldMark = shieldMark;
        }

        public int getTopMark() {
            return topMark;
        }

        public void setTopMark(int topMark) {
            this.topMark = topMark;
        }
    }

    public static class ExecutorInfo{
        private String avatar;
        private int gender;
        private String sign;
        private String username;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class RecipientInfo{
        private String avatar;
        private int gender;
        private String id;
        private String sign;
        private String username;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class GroupPostInfo{
        private String postContent;
        private String postId;
        private String postTitle;

        public String getPostContent() {
            return postContent;
        }

        public void setPostContent(String postContent) {
            this.postContent = postContent;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getPostTitle() {
            return postTitle;
        }

        public void setPostTitle(String postTitle) {
            this.postTitle = postTitle;
        }
    }


}
