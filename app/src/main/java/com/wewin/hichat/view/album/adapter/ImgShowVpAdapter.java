package com.wewin.hichat.view.album.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Darren on 2017/8/1.
 */

public class ImgShowVpAdapter extends PagerAdapter {

    private List<View> viewList;

    public ImgShowVpAdapter(List<View> viewList) {
        this.viewList = viewList;

    }

    @Override
    public int getCount() {
        return viewList.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡
        container.addView(viewList.get(position));//添加页卡
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));//删除页卡

    }
}
