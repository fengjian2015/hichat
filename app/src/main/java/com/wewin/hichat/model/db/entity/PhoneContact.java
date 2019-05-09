package com.wewin.hichat.model.db.entity;

/**
 * Created by Darren on 2019/2/14
 */
public class PhoneContact {

    public static final int NOT_INVITE = 0;
    public static final int INVITED = 1;
    public static final int ADDED = 2;

    private String phone;
    private String name;
    private String sortLetter;//拼音首字母
    private int avatar;//随机头像
    private int localState;//0：未邀请；1：已邀请；2：已添加；
    private long inviteTime;


    public PhoneContact() {
    }

    public PhoneContact(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    public int getLocalState() {
        return localState;
    }

    public void setLocalState(int localState) {
        this.localState = localState;
    }

    public long getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(long inviteTime) {
        this.inviteTime = inviteTime;
    }

    @Override
    public String toString() {
        return "PhoneContact{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", sortLetter='" + sortLetter + '\'' +
                ", localState=" + localState +
                ", inviteTime=" + inviteTime +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PhoneContact phoneContact = (PhoneContact) obj;
        return phone.equals(phoneContact.getPhone());
    }


}
