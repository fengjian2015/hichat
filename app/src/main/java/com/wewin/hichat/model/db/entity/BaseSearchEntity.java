package com.wewin.hichat.model.db.entity;

import java.util.Comparator;

/**
 * Created by Darren on 2019/2/26
 */
public class BaseSearchEntity {

    String sortName;
    String sortLetter;


    public BaseSearchEntity() {
    }

    public BaseSearchEntity(String sortName, String sortLetter) {
        this.sortName = sortName;
        this.sortLetter = sortLetter;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    public static class SortComparator implements Comparator<BaseSearchEntity> {
        @Override
        public int compare(BaseSearchEntity o1, BaseSearchEntity o2) {
            if ("@".equals(o1.getSortLetter())
                    || "#".equals(o2.getSortLetter())) {
                return -1;
            } else if ("#".equals(o1.getSortLetter())
                    || "@".equals(o2.getSortLetter())) {
                return 1;
            } else {
                return o1.getSortLetter().compareTo(o2.getSortLetter());
            }
        }
    }

    @Override
    public String toString() {
        return "BaseSearchEntity{" +
                "sortName='" + sortName + '\'' +
                ", sortLetter='" + sortLetter + '\'' +
                '}';
    }
}
