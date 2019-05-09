package com.wewin.hichat.component.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Darren on 2016/11/11.
 */
public class MainVpAdapter extends FragmentPagerAdapter{

    private List<Fragment> fragmentList;

    public MainVpAdapter(FragmentManager manager, List<Fragment> fragmentList){
        super(manager);
        this.fragmentList = fragmentList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
