package com.wewin.hichat.model.db.entity;

import java.util.List;

/**
 * Created by Darren on 2019/5/3
 **/
public class ServerConversation {

    private List<DeleteConversation> deleteConversations;
    private List<UpdateConversation> updateConversations;

    public List<DeleteConversation> getDeleteConversations() {
        return deleteConversations;
    }

    public void setDeleteConversations(List<DeleteConversation> deleteConversations) {
        this.deleteConversations = deleteConversations;
    }

    public List<UpdateConversation> getUpdateConversations() {
        return updateConversations;
    }

    public void setUpdateConversations(List<UpdateConversation> updateConversations) {
        this.updateConversations = updateConversations;
    }

    @Override
    public String toString() {
        return "ServerConversation{" +
                "deleteConversations=" + deleteConversations +
                ", updateConversations=" + updateConversations +
                '}';
    }

    public static class DeleteConversation{

        private String conversationId;
        private String conversationType;

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

        @Override
        public String toString() {
            return "DeleteConversation{" +
                    "conversationId='" + conversationId + '\'' +
                    ", conversationType='" + conversationType + '\'' +
                    '}';
        }
    }

    public static class UpdateConversation{

        private int aitMark;//是否@
        private int aitType;//@类型 1我；2全体成员
        private String avatar;
        private String conversationId;
        private String conversationType;
        private int friendshipMark;
        private int addMark;//是否为新增
        private int deleteMark;//是否为删除
        private int updateMark;//是否需要更新
        private ChatMsg latestMessage;//最新消息
        private String name;//会话名称
        private int shieldMark;
        private int topMark;
        private int unreadNum;
        private String unSyncMsgFirstId;//未同步的最早一条消息id

        public String getUnSyncMsgFirstId() {
            return unSyncMsgFirstId;
        }

        public void setUnSyncMsgFirstId(String unSyncMsgFirstId) {
            this.unSyncMsgFirstId = unSyncMsgFirstId;
        }

        public int getAitMark() {
            return aitMark;
        }

        public void setAitMark(int aitMark) {
            this.aitMark = aitMark;
        }

        public int getAitType() {
            return aitType;
        }

        public void setAitType(int aitType) {
            this.aitType = aitType;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

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

        public int getFriendshipMark() {
            return friendshipMark;
        }

        public void setFriendshipMark(int friendshipMark) {
            this.friendshipMark = friendshipMark;
        }

        public int getAddMark() {
            return addMark;
        }

        public void setAddMark(int addMark) {
            this.addMark = addMark;
        }

        public int getDeleteMark() {
            return deleteMark;
        }

        public void setDeleteMark(int deleteMark) {
            this.deleteMark = deleteMark;
        }

        public int getUpdateMark() {
            return updateMark;
        }

        public void setUpdateMark(int updateMark) {
            this.updateMark = updateMark;
        }

        public ChatMsg getLatestMessage() {
            return latestMessage;
        }

        public void setLatestMessage(ChatMsg latestMessage) {
            this.latestMessage = latestMessage;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public int getUnreadNum() {
            return unreadNum;
        }

        public void setUnreadNum(int unreadNum) {
            this.unreadNum = unreadNum;
        }

        @Override
        public String toString() {
            return "UpdateConversation{" +
                    "aitMark=" + aitMark +
                    ", aitType=" + aitType +
                    ", avatar='" + avatar + '\'' +
                    ", conversationId='" + conversationId + '\'' +
                    ", conversationType='" + conversationType + '\'' +
                    ", friendshipMark=" + friendshipMark +
                    ", addMark=" + addMark +
                    ", deleteMark=" + deleteMark +
                    ", updateMark=" + updateMark +
                    ", latestMessage=" + latestMessage +
                    ", name='" + name + '\'' +
                    ", shieldMark=" + shieldMark +
                    ", topMark=" + topMark +
                    ", unreadNum=" + unreadNum +
                    ", unSyncMsgFirstId='" + unSyncMsgFirstId + '\'' +
                    '}';
        }
    }


}
