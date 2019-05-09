package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2018/12/13.
 */
public class LoginUser implements Serializable {

    private String id;
    private String account;
    private String phone;
    private String username;
    private String avatar;
    private int gender;//0女;1男;2为保密
    private String sign;
    private String email;
    private String isVip;
    private String no;//会员账号
    private String countryCode;//国家码
    private String friendNote;
    private int audioCues;//消息声音提示 1提示 0不提示
    private int vibratesCues;//消息震动提示 1提示 0不提示

    public LoginUser() {
    }

    public LoginUser(String id, String account, String avatar, String email, int gender, String isVip, String no, String countryCode, String phone, String sign, String username, String friendNote, int audioCues, int vibratesCues) {
        this.id = id;
        this.account = account;
        this.avatar = avatar;
        this.email = email;
        this.gender = gender;
        this.isVip = isVip;
        this.no = no;
        this.countryCode = countryCode;
        this.phone = phone;
        this.sign = sign;
        this.username = username;
        this.friendNote = friendNote;
        this.audioCues = audioCues;
        this.vibratesCues = vibratesCues;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getAudioCues() {
        return audioCues;
    }

    public void setAudioCues(int audioCues) {
        this.audioCues = audioCues;
    }

    public int getVibratesCues() {
        return vibratesCues;
    }

    public void setVibratesCues(int vibratesCues) {
        this.vibratesCues = vibratesCues;
    }

    public String getFriendNote() {
        return friendNote;
    }

    public void setFriendNote(String friendNote) {
        this.friendNote = friendNote;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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

    @Override
    public String toString() {
        return "LoginUser{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", isVip='" + isVip + '\'' +
                ", no='" + no + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phone='" + phone + '\'' +
                ", sign='" + sign + '\'' +
                ", username='" + username + '\'' +
                ", friendNote='" + friendNote + '\'' +
                ", audioCues=" + audioCues +
                ", vibratesCues=" + vibratesCues +
                '}';
    }

}
