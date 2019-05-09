package com.wewin.hichat.model.db.entity;

/**
 * Created by Darren on 2019/1/22
 */
public class Emoticon {

    private String id;
    private String name;
    private String filePath;
    private String tag;

    public Emoticon() {
    }

    public Emoticon(String name, String id, String tag) {
        this.name = name;
        this.id = id;
        this.tag = tag;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Emoticon{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
