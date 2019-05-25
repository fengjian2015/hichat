package com.wewin.hichat.view.search;

import com.wewin.hichat.model.db.entity.SortModel;

import java.util.Comparator;

/**
 * @author xiaanming
 */
public class PinyinComparator implements Comparator<SortModel> {
    @Override
    public int compare(SortModel o1, SortModel o2) {
        if ("@".equals(o1.getSortLetters())
                || "#".equals(o2.getSortLetters())) {
            return -1;
        } else if ("#".equals(o1.getSortLetters())
                || "@".equals(o2.getSortLetters())) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
