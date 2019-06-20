package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * @author:jason date:2019/6/1812:24
 */
public class ReplyMsgInfo implements Serializable {
    private String content;
    private int contentType;//0文本; 1语音通话; 2文件图片; 3@; 4回复消息
    private FileInfo fileInfo;//文件信息
    private FriendInfo senderInfo;//临时会话发送者信息

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public FriendInfo getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(FriendInfo senderInfo) {
        this.senderInfo = senderInfo;
    }
}
