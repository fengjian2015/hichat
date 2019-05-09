package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2019/1/15.
 */
public class Announcement implements Serializable {

    private String accountId;
    private String content;
    private String groupId;
    private String id;
    private long postTime;
    private String title;
    private LoginUser account;

    public Announcement() {
    }

    public Announcement(String accountId, String content, String groupId, String id, long postTime, String title, LoginUser account) {
        this.accountId = accountId;
        this.content = content;
        this.groupId = groupId;
        this.id = id;
        this.postTime = postTime;
        this.title = title;
        this.account = account;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LoginUser getAccount() {
        return account;
    }

    public void setAccount(LoginUser account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "accountId='" + accountId + '\'' +
                ", content='" + content + '\'' +
                ", groupId='" + groupId + '\'' +
                ", id='" + id + '\'' +
                ", postTime=" + postTime +
                ", title='" + title + '\'' +
                ", account=" + account +
                '}';
    }
}
