package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2018/12/26.
 */
public class Notify implements Serializable {

    public static final int TYPE_FRIEND = 1;
    public static final int TYPE_GROUP = 2;
    public static final int STATUS_REFUSE = -1;
    public static final int STATUS_WAIT = 0;
    public static final int STATUS_AGREE = 1;

    private String accountId;
    private String content;
    private String from;
    private String fromGroup;
    private String href;
    private String id;
    private int isSystem;
    private String read;
    private String remark;
    private int status;//-1已拒绝；0等待验证；1已同意
    private long time;
    private int type;//1好友消息 2加群消息
    private String uGroup;
    private String uid;
    private LoginUser fromUser;//接收者
    private GroupFrom groupFrom;
    private LoginUser user;//申请者

    public Notify() {
    }

    public Notify(String accountId, String content, String from, String fromGroup, String href, String id, int isSystem, String read, String remark, int status, long time, int type, String uGroup, String uid, LoginUser fromUser, GroupFrom groupFrom, LoginUser user) {
        this.accountId = accountId;
        this.content = content;
        this.from = from;
        this.fromGroup = fromGroup;
        this.href = href;
        this.id = id;
        this.isSystem = isSystem;
        this.read = read;
        this.remark = remark;
        this.status = status;
        this.time = time;
        this.type = type;
        this.uGroup = uGroup;
        this.uid = uid;
        this.fromUser = fromUser;
        this.groupFrom = groupFrom;
        this.user = user;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(int isSystem) {
        this.isSystem = isSystem;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getuGroup() {
        return uGroup;
    }

    public void setuGroup(String uGroup) {
        this.uGroup = uGroup;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public LoginUser getFromUser() {
        return fromUser;
    }

    public void setFromUser(LoginUser fromUser) {
        this.fromUser = fromUser;
    }

    public GroupFrom getGroupFrom() {
        return groupFrom;
    }

    public void setGroupFrom(GroupFrom groupFrom) {
        this.groupFrom = groupFrom;
    }

    public LoginUser getUser() {
        return user;
    }

    public void setUser(LoginUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Notify{" +
                "accountId='" + accountId + '\'' +
                ", content='" + content + '\'' +
                ", from='" + from + '\'' +
                ", fromGroup='" + fromGroup + '\'' +
                ", href='" + href + '\'' +
                ", id='" + id + '\'' +
                ", isSystem=" + isSystem +
                ", read='" + read + '\'' +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", uGroup='" + uGroup + '\'' +
                ", uid='" + uid + '\'' +
                ", fromUser=" + fromUser +
                ", groupFrom=" + groupFrom +
                ", user=" + user +
                '}';
    }

    public static class GroupFrom implements Serializable{
        private String avatar;
        private String description;
        private String groupName;
        private String groupNum;
        private String id;

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
            return "Group{" +
                    "avatar='" + avatar + '\'' +
                    ", description='" + description + '\'' +
                    ", groupName='" + groupName + '\'' +
                    ", groupNum='" + groupNum + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }



}
