package com.wewin.hichat.model.db.entity;

import java.util.List;

/**
 * Created by Darren on 2019/3/4
 */
public class UnreadMsg {

    private List<Unread> offList;
    private int sumNum;

    public UnreadMsg() {
    }

    public UnreadMsg(List<Unread> offList, int sumNum) {
        this.offList = offList;
        this.sumNum = sumNum;
    }

    public List<Unread> getOffList() {
        return offList;
    }

    public void setOffList(List<Unread> offList) {
        this.offList = offList;
    }

    public int getSumNum() {
        return sumNum;
    }

    public void setSumNum(int sumNum) {
        this.sumNum = sumNum;
    }

    @Override
    public String toString() {
        return "UnreadMsg{" +
                "offList=" + offList +
                ", sumNum=" + sumNum +
                '}';
    }

    public static class Unread{
        private String content;
        private int contentType;
        private int num;
        private long timestamp;
        private String type;
        private FriendInfo friend;
        private GroupInfo group;

        public Unread() {
        }

        public Unread(String content, int contentType, int num, long timestamp, String type, FriendInfo friend, GroupInfo group) {
            this.content = content;
            this.contentType = contentType;
            this.num = num;
            this.timestamp = timestamp;
            this.type = type;
            this.friend = friend;
            this.group = group;
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

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public FriendInfo getFriend() {
            return friend;
        }

        public void setFriend(FriendInfo friend) {
            this.friend = friend;
        }

        public GroupInfo getGroup() {
            return group;
        }

        public void setGroup(GroupInfo group) {
            this.group = group;
        }

        @Override
        public String toString() {
            return "Unread{" +
                    "content='" + content + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", num=" + num +
                    ", timestamp=" + timestamp +
                    ", type='" + type + '\'' +
                    ", friend=" + friend +
                    ", group=" + group +
                    '}';
        }
    }


}
