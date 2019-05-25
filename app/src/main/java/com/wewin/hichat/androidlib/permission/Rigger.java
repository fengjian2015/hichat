package com.wewin.hichat.androidlib.permission;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 作者：created by jason on 2019/4/5 13
 * 权限请求封装,通过接口回调方式处理权限请求结果
 */
public class Rigger {

    private final String RIGGER_FRAGMENT_TAG = "Rigger";
    private RiggerPresenter mRiggerPresenter;

    public static Rigger on(Activity activity) {
        return new Rigger(activity);
    }

    private Rigger(Activity activity) {
        if (activity instanceof FragmentActivity) {
            mRiggerPresenter = getSupportRiggerPresenter(((FragmentActivity) activity).getSupportFragmentManager());
        } else {
            mRiggerPresenter = getRiggerPresenter(activity.getFragmentManager());
        }
        mRiggerPresenter.setActivity(activity);
    }

    /**
     * 是否显示弹窗
     *
     * @param isShow
     * @return
     */
    public Rigger isShowDialog(boolean isShow) {
        mRiggerPresenter.setIsShow(isShow);
        return this;
    }

    /**
     * 权限名字弹窗提示
     *
     * @param permissions
     * @return
     */
    public Rigger permissions(String... permissions) {
        mRiggerPresenter.setPermissions(permissions);
        return this;
    }

    /**
     * 启动
     *
     * @return
     */
    public Rigger start() {
        mRiggerPresenter.start();
        return this;
    }

    /**
     * 启动
     *
     * @param callback
     * @return
     */
    public Rigger start(PermissionCallback callback) {
        mRiggerPresenter.setPermissionCallback(callback);
        mRiggerPresenter.start();
        return this;
    }

    private RiggerPresenter getSupportRiggerPresenter(FragmentManager fm) {
        SupportRiggerFragment riggerFragment = (SupportRiggerFragment) fm.findFragmentByTag(RIGGER_FRAGMENT_TAG);
        if (riggerFragment == null) {
            riggerFragment = new SupportRiggerFragment();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(riggerFragment, RIGGER_FRAGMENT_TAG);
            transaction.commitNowAllowingStateLoss();
        }
        return riggerFragment.getRiggerPresenter();
    }

    private RiggerPresenter getRiggerPresenter(android.app.FragmentManager fm) {
        RiggerFragment riggerFragment = (RiggerFragment) fm.findFragmentByTag(RIGGER_FRAGMENT_TAG);
        if (riggerFragment == null) {
            riggerFragment = new RiggerFragment();
            android.app.FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(riggerFragment, RIGGER_FRAGMENT_TAG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                transaction.commitNowAllowingStateLoss();
            } else {
                transaction.commitAllowingStateLoss();
            }
        }
        return riggerFragment.getRiggerPresenter();
    }
}
