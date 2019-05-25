package com.wewin.hichat.model.db.entity;

import android.text.TextUtils;
import com.wewin.hichat.androidlib.utils.UUIDUtil;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Darren on 2018/12/26.
 */
public class Notify implements Serializable {

    public static final int STATUS_REFUSE = -1;
    public static final int STATUS_WAIT = 0;
    public static final int STATUS_AGREE = 1;

    private String avatar;
    private long createTime;
    private String name;
    private String noticeId;
    private String noticeType;
    private String remark;
    private String senderId;
    private int status;//-1已拒绝；0等待验证；1已同意
    private int systemMark;
    private String systemMessage;
    private String systemNote;

    public Notify() {
    }

    public Notify(String avatar, long createTime, String name, String noticeId, String noticeType, String remark, String senderId, int status, int systemMark, String systemMessage, String systemNote) {
        this.avatar = avatar;
        this.createTime = createTime;
        this.name = name;
        this.noticeId = noticeId;
        this.noticeType = noticeType;
        this.remark = remark;
        this.senderId = senderId;
        this.status = status;
        this.systemMark = systemMark;
        this.systemMessage = systemMessage;
        this.systemNote = systemNote;
    }

    //按createTime倒序排列
    public static class TimeRiseComparator implements Comparator<Notify> {
        @Override
        public int compare(Notify o1, Notify o2) {
            if (o1.getCreateTime() - o2.getCreateTime() < 0) {
                return 1;
            } else if (o1.getCreateTime() - o2.getCreateTime() > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Notify)){
            return false;
        }
        return noticeId.equals(((Notify) obj).noticeId);
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (!TextUtils.isEmpty(noticeId)){
            result = result * 31 + noticeId.hashCode();
            return result;
        }else {
            return new Random().nextInt(1000) * 31 + UUIDUtil.get32UUID().hashCode();
        }

    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSystemMark() {
        return systemMark;
    }

    public void setSystemMark(int systemMark) {
        this.systemMark = systemMark;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    public String getSystemNote() {
        return systemNote;
    }

    public void setSystemNote(String systemNote) {
        this.systemNote = systemNote;
    }

    @Override
    public String toString() {
        return "Notify{" +
                "avatar='" + avatar + '\'' +
                ", createTime=" + createTime +
                ", name='" + name + '\'' +
                ", noticeId='" + noticeId + '\'' +
                ", noticeType='" + noticeType + '\'' +
                ", remark='" + remark + '\'' +
                ", senderId='" + senderId + '\'' +
                ", status=" + status +
                ", systemMark=" + systemMark +
                ", systemMessage='" + systemMessage + '\'' +
                ", systemNote='" + systemNote + '\'' +
                '}';
    }

}
