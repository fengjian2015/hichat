package com.wewin.hichat.model.db.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * 好友分组
 * Created by Darren on 2019/1/4.
 */
public class Subgroup implements Serializable {

    private static final int TYPE_NORMAL = 0;
    public static final int TYPE_FRIEND = 1;
    public static final int TYPE_BLACK = 2;
    public static final int TYPE_PHONE_CONTACT = 3;
    public static final int TYPE_TEMPORARY = 4;//临时分组

    private String accountId;
    private long buildTime;
    private String groupName;
    private String id;
    private int isDefault;//0普通分组；1好友；2黑名单；3通讯录；4隐藏临时会话人员
    private int online;
    private boolean checked;
    private List<FriendInfo> list;

    public Subgroup() {
    }

    public Subgroup(String accountId, long buildTime, String groupName, String id, int isDefault) {
        this.accountId = accountId;
        this.buildTime = buildTime;
        this.groupName = groupName;
        this.id = id;
        this.isDefault = isDefault;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public List<FriendInfo> getList() {
        return list;
    }

    public void setList(List<FriendInfo> list) {
        this.list = list;
    }

    public static class SubgroupComparator implements Comparator<Subgroup> {

        @Override
        public int compare(Subgroup o1, Subgroup o2) {
            if (o1.getIsDefault() == TYPE_FRIEND && o2.getIsDefault() != TYPE_FRIEND) {
                return -1;
            } else if (o1.getIsDefault() != TYPE_FRIEND && o2.getIsDefault() == TYPE_FRIEND) {
                return 1;
            } else if (o1.getIsDefault() == TYPE_PHONE_CONTACT && o2.getIsDefault() != TYPE_PHONE_CONTACT) {
                return -1;
            } else if (o1.getIsDefault() != TYPE_PHONE_CONTACT && o2.getIsDefault() == TYPE_PHONE_CONTACT) {
                return 1;
            } else if (o1.getIsDefault() == TYPE_BLACK && o2.getIsDefault() != TYPE_BLACK) {
                return 1;
            } else if (o1.getIsDefault() != TYPE_BLACK && o2.getIsDefault() == TYPE_BLACK) {
                return -1;
            } else if (o1.getBuildTime() < o2.getBuildTime()) {
                return -1;
            } else if (o1.getBuildTime() > o2.getBuildTime()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return "Subgroup{" +
                "accountId='" + accountId + '\'' +
                ", buildTime=" + buildTime +
                ", groupName='" + groupName + '\'' +
                ", id='" + id + '\'' +
                ", isDefault=" + isDefault +
                ", online=" + online +
                ", checked=" + checked +
                ", list=" + list +
                '}';
    }

}
