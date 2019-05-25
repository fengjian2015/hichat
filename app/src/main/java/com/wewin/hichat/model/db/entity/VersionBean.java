package com.wewin.hichat.model.db.entity;

/**
 * @author:jason date:2019/5/2312:25
 */
public class VersionBean {

    private String appDownloadUrl;
    private String appName;
    private float appSize;
    private String introduction;
    private String md5;
    private String packageName;
    /**
     * 是否强制更新 0 否 1是
     */
    private int status;
    private String terminal;
    private String version;
    private int versionCode;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppDownloadUrl() {
        return appDownloadUrl;
    }

    public void setAppDownloadUrl(String appDownloadUrl) {
        this.appDownloadUrl = appDownloadUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public float getAppSize() {
        return appSize;
    }

    public void setAppSize(float appSize) {
        this.appSize = appSize;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "appDownloadUrl='" + appDownloadUrl + '\'' +
                ", appName='" + appName + '\'' +
                ", appSize=" + appSize +
                ", introduction='" + introduction + '\'' +
                ", md5='" + md5 + '\'' +
                ", packageName='" + packageName + '\'' +
                ", status=" + status +
                ", terminal='" + terminal + '\'' +
                ", version='" + version + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
