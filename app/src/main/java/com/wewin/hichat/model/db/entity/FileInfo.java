package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2019/1/5.
 */
public class FileInfo implements Serializable {

    public static final int TYPE_IMG = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_DOC = 3;
    public static final int TYPE_MUSIC = 4;//音频，音乐
    public static final int TYPE_TAPE_RECORD = 5;
    public static final int TYPE_APK = 21;
    public static final int TYPE_ZIP = 22;

    public static final int TYPE_DOWNLOAD_FAIL = -1;//下载失败
    public static final int TYPE_DOWNLOAD_NOT= 0;//未下载
    public static final int TYPE_DOWNLOADING = 1;//下载中
    public static final int TYPE_DOWNLOAD_SUCCESS = 2;//下载成功

    private String id;
    private String fileName;
    private int fileType;
    private long fileLength;
    private String originPath;//文件原始路径
    private String compressPath;//文件压缩路径
    private String savePath;//文件下载后保存在本地路径
    private String downloadPath;//服务器下载链接
    private int downloadState;//下载状态 -1下载失败；0下载中；1下载成功；
    private int tapePlayingMark;//录音是否播放中
    private int tapeUnreadMark;//录音是否未读
    private long duration;
    private long createTime;
    private boolean checked;


    public FileInfo() {
    }

    public FileInfo(String id, String fileName, int fileType, long fileLength, String originPath, String savePath, String downloadPath, long duration, long createTime) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileLength = fileLength;
        this.originPath = originPath;
        this.savePath = savePath;
        this.downloadPath = downloadPath;
        this.duration = duration;
        this.createTime = createTime;
    }

    public int getTapePlayingMark() {
        return tapePlayingMark;
    }

    public void setTapePlayingMark(int tapePlayingMark) {
        this.tapePlayingMark = tapePlayingMark;
    }

    public int getTapeUnreadMark() {
        return tapeUnreadMark;
    }

    public void setTapeUnreadMark(int tapeUnreadMark) {
        this.tapeUnreadMark = tapeUnreadMark;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", fileLength=" + fileLength +
                ", originPath='" + originPath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", savePath='" + savePath + '\'' +
                ", downloadPath='" + downloadPath + '\'' +
                ", downloadState=" + downloadState +
                ", tapePlayingMark=" + tapePlayingMark +
                ", tapeUnreadMark=" + tapeUnreadMark +
                ", duration=" + duration +
                ", createTime=" + createTime +
                ", checked=" + checked +
                '}';
    }

}
