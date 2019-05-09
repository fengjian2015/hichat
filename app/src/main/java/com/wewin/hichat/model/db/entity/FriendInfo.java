package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2018/12/20.
 */
public class FriendInfo extends BaseSearchEntity implements Serializable {

    private String id;
    private String username;
    private String avatar;
    private String friendNote;
    private String phone;
    private String sign;
    private int gender;
    private String groupId;//分组
    private String groupName;
    private String groupAvatar;
    private String groupNum;
    private String status;
    private String onlineStatus;//online在线；offline离线；hide隐身
    private int grade;
    private String type;
    private String subgroupId;//分组
    private String subgroupName;
    private int subgroupType;////0普通分组；1好友；2黑名单；3通讯录
    private int topMark;//0 消息未置顶 1消息已置顶
    private int blackMark;//0取消拉黑；1拉黑
    private int shieldMark;//0取消屏蔽；1屏蔽
    private int friendship;//0非好友；1好友
    private boolean invited;//0未邀请；1已邀请
    private int groupSpeak;//0正常发言 1已禁言
    private boolean checked;


    public FriendInfo() {
    }

    public FriendInfo(String avatar, String friendNote, String id, String phone, String sign, String status, String username, int gender) {
        this.avatar = avatar;
        this.friendNote = friendNote;
        this.id = id;
        this.phone = phone;
        this.sign = sign;
        this.status = status;
        this.username = username;
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public int getSubgroupType() {
        return subgroupType;
    }

    public void setSubgroupType(int subgroupType) {
        this.subgroupType = subgroupType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupSpeak() {
        return groupSpeak;
    }

    public void setGroupSpeak(int groupSpeak) {
        this.groupSpeak = groupSpeak;
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    public int getFriendship() {
        return friendship;
    }

    public void setFriendship(int friendship) {
        this.friendship = friendship;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getTopMark() {
        return topMark;
    }

    public void setTopMark(int topMark) {
        this.topMark = topMark;
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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFriendNote() {
        return friendNote;
    }

    public void setFriendNote(String friendNote) {
        this.friendNote = friendNote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        setSortName(username);
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", friendNote='" + friendNote + '\'' +
                ", phone='" + phone + '\'' +
                ", sign='" + sign + '\'' +
                ", gender=" + gender +
                ", groupName='" + groupName + '\'' +
                ", groupAvatar='" + groupAvatar + '\'' +
                ", groupNum='" + groupNum + '\'' +
                ", status='" + status + '\'' +
                ", onlineStatus='" + onlineStatus + '\'' +
                ", grade=" + grade +
                ", subgroupId='" + subgroupId + '\'' +
                ", subgroupName='" + subgroupName + '\'' +
                ", subgroupType=" + subgroupType +
                ", groupId='" + groupId + '\'' +
                ", topMark=" + topMark +
                ", blackMark=" + blackMark +
                ", shieldMark=" + shieldMark +
                ", checked=" + checked +
                ", friendship=" + friendship +
                ", invited=" + invited +
                ", groupSpeak=" + groupSpeak +
                '}';
    }

}
