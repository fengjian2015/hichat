package com.wewin.hichat.model.db.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * @author:jason date:2019/5/2414:31
 */
public class SelectSubgroup implements Serializable {

    private static final int TYPE_NORMAL = 0;
    public static final int TYPE_FRIEND = 1;
    public static final int TYPE_BLACK = 2;
    public static final int TYPE_PHONE_CONTACT = 3;
    /**
     * 临时分组
     */
    public static final int TYPE_TEMPORARY = 4;
    /**
     * 0普通分组；1好友；2黑名单；3通讯录；4隐藏临时会话人员
     */
    private int isDefault;
    private boolean checked;
    private String groupName;
    private String id;
    private long buildTime;

    private List<DataBean> data;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public static class DataBean extends BaseSearchEntity implements Serializable{
        private String roomType;
        private String roomId;
        private String avatar;
        private String username;

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }


    public static class SubgroupComparator implements Comparator<SelectSubgroup> {

        @Override
        public int compare(SelectSubgroup o1, SelectSubgroup o2) {
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
}
