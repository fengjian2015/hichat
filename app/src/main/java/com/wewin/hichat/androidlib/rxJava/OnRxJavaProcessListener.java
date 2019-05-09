package com.wewin.hichat.androidlib.rxJava;

import io.reactivex.ObservableEmitter;

/**
 * Created by Darren on 2019/4/13
 **/
public interface OnRxJavaProcessListener {

    void process(ObservableEmitter<Object> emitter);

}
