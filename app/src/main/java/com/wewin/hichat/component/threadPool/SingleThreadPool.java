package com.wewin.hichat.component.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Darren on 2019/4/17
 **/
public class SingleThreadPool {

    private static SingleThreadPool instance;
    private static ExecutorService singleThreadExecutor;

    private SingleThreadPool(){
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static SingleThreadPool getInstance(){
        if (instance == null){
            synchronized (SingleThreadPool.class){
                if (instance == null){
                    instance = new SingleThreadPool();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable runnable){
        singleThreadExecutor.execute(runnable);
    }



}
