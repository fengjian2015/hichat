package com.wewin.hichat.model.db.entity;

/**
 * Created by Darren on 2019/2/25
 */
public class Heart {

    private String value;//ping, pong
    private long serverTimestamp;//服务器时间戳

    public Heart() {
    }

    public Heart(String value) {
        this.value = value;
    }

    public Heart(String value, long serverTimestamp) {
        this.value = value;
        this.serverTimestamp = serverTimestamp;
    }

    public long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Heart{" +
                "value='" + value + '\'' +
                ", serverTimestamp='" + serverTimestamp + '\'' +
                '}';
    }

}
