package com.wewin.hichat.component.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wewin.hichat.androidlib.event.EventTrans;
import com.wewin.hichat.androidlib.event.EventMsg;

/**
 * Created by Darren on 2018/12/13.
 */
public abstract class BaseFragment extends Fragment
        implements View.OnClickListener, EventTrans.OnEventTransListener {


    protected BaseActivity mActivity;
    /**
     * fragment生命周期标志位，表示fragment是否已经执行onViewCreated()方法
     */
    protected boolean isViewCreated = false;
    public View parentView;

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected void initArguments(Bundle savedInstanceState){}

    @Override
    public void onEventTrans(EventMsg msg){}

    protected void initViewsData(){}

    protected void setListener(){}

    /**
     * 获取宿主Activity
     */
    protected BaseActivity getHostActivity() {
        return mActivity;
    }

    @Override
    public void onClick(View v){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity){
            this.mActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mActivity = (BaseActivity) getActivity();
        super.onCreate(savedInstanceState);
        initArguments(savedInstanceState);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        parentView = inflater.inflate(getLayoutId(), container, false);
        initViews();
        return parentView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        initViewsData();
        setListener();
        EventTrans.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        EventTrans.getInstance().unRegister(this);
    }

}
