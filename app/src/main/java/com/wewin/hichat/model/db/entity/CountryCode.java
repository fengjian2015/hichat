package com.wewin.hichat.model.db.entity;

import java.io.Serializable;

/**
 * Created by Darren on 2018/12/18.
 */
public class CountryCode implements Serializable {

    private String country;
    private String code;

    public CountryCode() {
    }

    public CountryCode(String country, String code) {
        this.country = country;
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CountryCode{" +
                "country='" + country + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}