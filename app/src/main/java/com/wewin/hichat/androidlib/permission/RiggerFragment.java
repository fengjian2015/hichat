package com.wewin.hichat.androidlib.permission;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * 作者：created by jason on 2019/4/5 13
 * fragment监听回调处
 */
public class RiggerFragment extends Fragment {
    RiggerPresenter riggerPresenter=new RiggerPresenter(this);
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        riggerPresenter.onAdded();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        riggerPresenter.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        riggerPresenter.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        riggerPresenter.onDestroy();
    }

    public RiggerPresenter getRiggerPresenter(){
        return riggerPresenter;
    }
}
