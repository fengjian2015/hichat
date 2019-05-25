package com.wewin.hichat.model.db.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Darren on 2019/4/15
 **/
public class ChatRoom implements Serializable {

    public static final String TYPE_SINGLE = "private";//单聊
    public static final String TYPE_GROUP = "group";//群聊

    private String roomId;//房间id
    private String roomType;//房间类型 private/group
    private ChatMsg lastChatMsg;//最新一条消息
    private long lastMsgTime;//最新一条消息时间戳
    private int unreadNum;//未读消息数量
    private int topMark;//是否置顶
    private int shieldMark;//是否屏蔽
    private int blackMark;//是否拉黑
    private int groupSpeakMark;//是否可以发言
    private int inviteMark;//是否允许邀请
    private int groupGrade;//群成员等级
    private int addMark;//是否新增加
    private int atMark;//是否@
    private int atType;//@类型 1我；2全体成员
    private int deleteMark;//是否要删除
    private int friendshipMark;
    private int updateMark;//是否要更新
    private String unSyncMsgFirstId;//未同步的最早一条消息id
    private boolean checked;//编辑状态是否选中



    //按置顶和时间倒序配列
    public static class TopComparator implements Comparator<ChatRoom> {
        @Override
        public int compare(ChatRoom o1, ChatRoom o2) {
            if (o1.getTopMark() == 1 && o2.getTopMark() != 1) {
                return -1;
            } else if (o1.getTopMark() != 1 && o2.getTopMark() == 1) {
                return 1;
            } else if (o1.getLastMsgTime() - o2.getLastMsgTime() > 0) {
                return -1;
            } else if (o1.getLastMsgTime() - o2.getLastMsgTime() < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public ChatRoom() {
    }

    public ChatRoom(String roomId, String roomType) {
        this.roomId = roomId;
        this.roomType = roomType;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getUnSyncMsgFirstId() {
        return unSyncMsgFirstId;
    }

    public void setUnSyncMsgFirstId(String unSyncMsgFirstId) {
        this.unSyncMsgFirstId = unSyncMsgFirstId;
    }

    public int getAddMark() {
        return addMark;
    }

    public void setAddMark(int addMark) {
        this.addMark = addMark;
    }

    public int getAtMark() {
        return atMark;
    }

    public void setAtMark(int atMark) {
        this.atMark = atMark;
    }

    public int getAtType() {
        return atType;
    }

    public void setAtType(int atType) {
        this.atType = atType;
    }

    public int getDeleteMark() {
        return deleteMark;
    }

    public void setDeleteMark(int deleteMark) {
        this.deleteMark = deleteMark;
    }

    public int getFriendshipMark() {
        return friendshipMark;
    }

    public void setFriendshipMark(int friendshipMark) {
        this.friendshipMark = friendshipMark;
    }

    public int getUpdateMark() {
        return updateMark;
    }

    public void setUpdateMark(int updateMark) {
        this.updateMark = updateMark;
    }

    public int getGroupGrade() {
        return groupGrade;
    }

    public void setGroupGrade(int groupGrade) {
        this.groupGrade = groupGrade;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ChatMsg getLastChatMsg() {
        return lastChatMsg;
    }

    public void setLastChatMsg(ChatMsg lastChatMsg) {
        this.lastChatMsg = lastChatMsg;
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

    public int getTopMark() {
        return topMark;
    }

    public void setTopMark(int topMark) {
        this.topMark = topMark;
    }

    public int getShieldMark() {
        return shieldMark;
    }

    public void setShieldMark(int shieldMark) {
        this.shieldMark = shieldMark;
    }

    public int getBlackMark() {
        return blackMark;
    }

    public void setBlackMark(int blackMark) {
        this.blackMark = blackMark;
    }

    public int getGroupSpeakMark() {
        return groupSpeakMark;
    }

    public void setGroupSpeakMark(int groupSpeakMark) {
        this.groupSpeakMark = groupSpeakMark;
    }

    public int getInviteMark() {
        return inviteMark;
    }

    public void setInviteMark(int inviteMark) {
        this.inviteMark = inviteMark;
    }

    public int getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(int unreadNum) {
        this.unreadNum = unreadNum;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", roomType='" + roomType + '\'' +
                ", lastChatMsg=" + lastChatMsg +
                ", lastMsgTime=" + lastMsgTime +
                ", unreadNum=" + unreadNum +
                ", topMark=" + topMark +
                ", shieldMark=" + shieldMark +
                ", blackMark=" + blackMark +
                ", groupSpeakMark=" + groupSpeakMark +
                ", inviteMark=" + inviteMark +
                ", groupGrade=" + groupGrade +
                ", addMark=" + addMark +
                ", atMark=" + atMark +
                ", atType=" + atType +
                ", deleteMark=" + deleteMark +
                ", friendshipMark=" + friendshipMark +
                ", updateMark=" + updateMark +
                ", unSyncMsgFirstId='" + unSyncMsgFirstId + '\'' +
                ", checked=" + checked +
                '}';
    }
}
