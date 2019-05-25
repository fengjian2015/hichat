package com.wewin.hichat.model.db.entity;

import java.util.List;

/**
 * Created by Darren on 2019/3/14
 */
public class ChatRecordSearch {

    private String roomId;
    private String roomType;
    private boolean childLvExpand;//子listView是否展开
    private List<ContentItem> contentItemList;

    public ChatRecordSearch() {
    }

    public ChatRecordSearch(String roomId, String roomType, List<ContentItem> contentItemList) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.contentItemList = contentItemList;
    }

    public boolean isChildLvExpand() {
        return childLvExpand;
    }

    public void setChildLvExpand(boolean childLvExpand) {
        this.childLvExpand = childLvExpand;
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

    public List<ContentItem> getContentItemList() {
        return contentItemList;
    }

    public void setContentItemList(List<ContentItem> contentItemList) {
        this.contentItemList = contentItemList;
    }

    @Override
    public String toString() {
        return "ChatRecordSearch{" +
                "roomId='" + roomId + '\'' +
                ", roomType='" + roomType + '\'' +
                ", childLvExpand=" + childLvExpand +
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
