package com.wewin.hichat.component.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.wewin.hichat.model.db.entity.BaseSearchEntity;
import java.util.List;

/**
 * Created by Darren on 2019/3/2
 */
public abstract class BaseSearchAdapter extends BaseAdapter implements SectionIndexer {

    public abstract List getList();

    public abstract View bindView(int position, View convertView, ViewGroup parent);

    @Override
    public int getCount() {
        if (getList() == null){
            return 0;
        }else {
            return getList().size();
        }
    }

    @Override
    public Object getItem(int position) {
        return getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return bindView(position, convertView, parent);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getList().size(); i++) {
            String sortStr = ((BaseSearchEntity)getList().get(i)).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return ((BaseSearchEntity)getList().get(position)).getSortLetter().charAt(0);
    }

    protected void setLetterVisible(TextView letterTv, int position){
        final BaseSearchEntity entity = ((BaseSearchEntity)getList().get(position));
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            letterTv.setVisibility(View.VISIBLE);
            letterTv.setText(entity.getSortLetter());
        } else {
            letterTv.setVisibility(View.GONE);
        }
    }

}
