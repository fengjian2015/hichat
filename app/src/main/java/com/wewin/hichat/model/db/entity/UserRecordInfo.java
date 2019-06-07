package com.wewin.hichat.model.db.entity;

/**
 * @author:jason date:2019/5/2813:31
 */
public class UserRecordInfo {
    private String phoneNum;
    private String code;
    private String country;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
