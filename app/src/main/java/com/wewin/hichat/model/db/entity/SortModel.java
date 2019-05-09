package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

public class SortModel implements Serializable {

    private String id;
    private String name;//显示的数据
    private String sortLetters;//拼音首字母
    private String avatar;
    private String code;
    private boolean checked;
    private int state;
    private int status;

    public SortModel() {
    }

    public SortModel(String name, String sortLetters, String avatar, String code, boolean checked, int state, int status) {
        this.name = name;
        this.sortLetters = sortLetters;
        this.avatar = avatar;
        this.code = code;
        this.checked = checked;
        this.state = state;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SortModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sortLetters='" + sortLetters + '\'' +
                ", avatar='" + avatar + '\'' +
                ", code='" + code + '\'' +
                ", checked=" + checked +
                ", state=" + state +
                ", status=" + status +
                '}';
    }
}
