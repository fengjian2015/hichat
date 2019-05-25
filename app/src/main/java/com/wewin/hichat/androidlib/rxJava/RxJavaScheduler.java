package com.wewin.hichat.androidlib.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Darren
 * Created by Darren on 2019/4/13
 **/
public class RxJavaScheduler {

    private RxJavaScheduler() {}

    public static void execute(final com.wewin.hichat.androidlib.rxjava.OnRxJavaProcessListener processListener, com.wewin.hichat.androidlib.rxjava.RxJavaObserver<Object> rxJavaObserver) {
        Observable.create(
                new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        if (processListener != null){
                            processListener.process(emitter);
                        }
                        if (emitter != null){
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rxJavaObserver);
    }

}
