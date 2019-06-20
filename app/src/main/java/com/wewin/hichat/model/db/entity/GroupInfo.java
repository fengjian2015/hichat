package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2019/1/2.
 */
public class GroupInfo extends BaseSearchEntity implements Serializable {

    public static final int TYPE_GRADE_NOT = -1;//不在该群中
    public static final int TYPE_GRADE_NORMAL = 0;//普通会员
    public static final int TYPE_GRADE_MANAGER = 1;//管理员
    public static final int TYPE_GRADE_OWNER = 2;//群主

    private String selfId;
    private String id;
    private String groupName;
    private String groupAvatar;
    private String groupNum;
    private String description;
    private String groupMax;//群人数上限
    private String title;//最新群公告正文
    private int topMark;//是否置顶
    private int shieldMark;//是否屏蔽
    private int searchFlag;//是否允许被搜索
    private int groupValid;//是否需要验证
    private int inviteFlag;//是否允许群成员邀请加入
    private int addFriendMark;//是否允许群成员互加好友
    private int groupSpeak;//是否可以发言
    private int grade;//-1不在该群中 0 普通会员 1 管理员 2群主

    public GroupInfo() {
    }

    public GroupInfo(String description, String groupAvatar, String groupName, String groupNum, String id) {
        this.description = description;
        this.groupAvatar = groupAvatar;
        this.groupName = groupName;
        this.groupNum = groupNum;
        this.id = id;
    }

    public int getAddFriendMark() {
        return addFriendMark;
    }

    public void setAddFriendMark(int addFriendMark) {
        this.addFriendMark = addFriendMark;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupMax() {
        return groupMax;
    }

    public void setGroupMax(String groupMax) {
        this.groupMax = groupMax;
    }

    public int getShieldMark() {
        return shieldMark;
    }

    public void setShieldMark(int shieldMark) {
        this.shieldMark = shieldMark;
    }

    public int getGroupSpeak() {
        return groupSpeak;
    }

    public void setGroupSpeak(int groupSpeak) {
        this.groupSpeak = groupSpeak;
    }

    public int getSearchFlag() {
        return searchFlag;
    }

    public void setSearchFlag(int searchFlag) {
        this.searchFlag = searchFlag;
    }

    public int getGroupValid() {
        return groupValid;
    }

    public void setGroupValid(int groupValid) {
        this.groupValid = groupValid;
    }

    public int getInviteFlag() {
        return inviteFlag;
    }

    public void setInviteFlag(int inviteFlag) {
        this.inviteFlag = inviteFlag;
    }

    public int getTopMark() {
        return topMark;
    }

    public void setTopMark(int topMark) {
        this.topMark = topMark;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "selfId='" + selfId + '\'' +
                ", id='" + id + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupAvatar='" + groupAvatar + '\'' +
                ", groupNum='" + groupNum + '\'' +
                ", description='" + description + '\'' +
                ", groupMax='" + groupMax + '\'' +
                ", title='" + title + '\'' +
                ", topMark=" + topMark +
                ", shieldMark=" + shieldMark +
                ", searchFlag=" + searchFlag +
                ", groupValid=" + groupValid +
                ", inviteFlag=" + inviteFlag +
                ", addFriendMark=" + addFriendMark +
                ", groupSpeak=" + groupSpeak +
                ", grade=" + grade +
                '}';
    }
}
