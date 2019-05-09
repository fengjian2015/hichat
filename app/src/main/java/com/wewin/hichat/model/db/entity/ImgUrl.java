package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2018/12/22.
 */
public class ImgUrl implements Serializable {

    private String id;
    private String url;
    private String fileName;
    private int resourceId;
    private String text;
    private boolean checked;

    public ImgUrl() {
    }

    public ImgUrl(String url) {
        this.url = url;
    }

    public ImgUrl(String id, String text, int resourceId){
        this.id = id;
        this.text = text;
        this.resourceId = resourceId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "ImgUrl{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", resourceId=" + resourceId +
                ", text='" + text + '\'' +
                ", checked=" + checked +
                '}';
    }
}
