package com.wewin.hichat.model.db.entity;

import java.util.List;

/**
 * Created by Darren on 2019/3/14
 */
public class ChatRecordSearch {

    private String chatId;
    private String chatName;
    private String chatAvatar;
    private String msgType;
    private String friendNote;
    private String groupName;
    private String groupAvatar;
    private String groupNum;
    private List<ContentItem> contentItemList;

    public ChatRecordSearch() {
    }

    public ChatRecordSearch(String chatId, String chatName, String chatAvatar, String msgType, List<ContentItem> contentItemList) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatAvatar = chatAvatar;
        this.msgType = msgType;
        this.contentItemList = contentItemList;
    }

    public String getFriendNote() {
        return friendNote;
    }

    public void setFriendNote(String friendNote) {
        this.friendNote = friendNote;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatAvatar() {
        return chatAvatar;
    }

    public void setChatAvatar(String chatAvatar) {
        this.chatAvatar = chatAvatar;
    }

    public List<ContentItem> getContentItemList() {
        return contentItemList;
    }

    public void setContentItemList(List<ContentItem> contentItemList) {
        this.contentItemList = contentItemList;
    }

    @Override
    public String toString() {
        return "ChatRecordSearch{" +
                "chatId='" + chatId + '\'' +
                ", chatName='" + chatName + '\'' +
                ", chatAvatar='" + chatAvatar + '\'' +
                ", msgType='" + msgType + '\'' +
                ", friendNote='" + friendNote + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupAvatar='" + groupAvatar + '\'' +
                ", groupNum='" + groupNum + '\'' +
                ", contentItemList=" + contentItemList +
                '}';
    }

    public static class ContentItem{

        private String content;
        private long createTimestamp;

        public ContentItem() {
        }

        public ContentItem(String content, long createTimestamp) {
            this.content = content;
            this.createTimestamp = createTimestamp;
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

        @Override
        public String toString() {
            return "ContentItem{" +
                    "content='" + content + '\'' +
                    ", createTimestamp=" + createTimestamp +
                    '}';
        }
    }

}
