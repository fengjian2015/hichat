package com.wewin.hichat.androidlib.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Darren on 2017/11/16.
 */

public class PullableRecyclerView extends RecyclerView implements Pullable{


    public PullableRecyclerView(Context context) {
        super(context);
    }

    public PullableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean canPullDown() {
        LinearLayoutManager layoutManager = (LinearLayoutManager)getLayoutManager();
        int firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        return firstPosition == 0;
    }

    @Override
    public boolean canPullUp() {
        return false;
    }

}
