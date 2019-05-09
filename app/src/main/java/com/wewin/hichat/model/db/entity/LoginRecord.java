package com.wewin.hichat.model.db.entity;

/**
 * Created by Darren on 2019/3/1
 */
public class LoginRecord {

    private String account;
    private String ip;
    private long loginDate;
    private String terminal;
    private String userId;

    public LoginRecord() {
    }

    public LoginRecord(String account, String ip, long loginDate, String terminal, String userId) {
        this.account = account;
        this.ip = ip;
        this.loginDate = loginDate;
        this.terminal = terminal;
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(long loginDate) {
        this.loginDate = loginDate;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginRecord{" +
                "account='" + account + '\'' +
                ", ip='" + ip + '\'' +
                ", loginDate=" + loginDate +
                ", terminal='" + terminal + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
