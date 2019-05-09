package com.wewin.hichat.androidlib.utils;

import java.io.File;

import top.zibin.luban.OnCompressListener;

/**
 * Created by Darren on 2019/1/8.
 */
public class LubanCallBack implements OnCompressListener {


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(File outputFile) {

    }

    @Override
    public void onError(Throwable e) {
        LogUtil.i(e.getMessage());
        e.printStackTrace();
    }
}
