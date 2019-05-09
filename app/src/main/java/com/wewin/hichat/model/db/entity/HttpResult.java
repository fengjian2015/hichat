package com.wewin.hichat.model.db.entity;

/**
 * Created by Darren on 2018/12/19.
 */
public class HttpResult {

    //0接口调用成功
    private int code;
    private int count;
    private int pages;
    private String desc;
    private Object data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", count=" + count +
                ", pages=" + pages +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                '}';
    }
}
