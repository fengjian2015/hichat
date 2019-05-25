package com.wewin.hichat.androidlib.rxjava;

import io.reactivex.ObservableEmitter;

/**
 * @author Darren
 * Created by Darren on 2019/4/13
 **/
public interface OnRxJavaProcessListener {

    void process(ObservableEmitter<Object> emitter);

}
