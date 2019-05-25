package com.wewin.hichat.androidlib.threadpool;

import android.os.Process;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author Darren
 * Created by Darren on 2019/4/13
 **/
public class CustomThreadPool {

    private static CustomThreadPool instance;
    // 核心线程的数量
    private final int CORE_POOL_SIZE = 3;
    // 最大线程数量
    private int MAX_POOL_SIZE = 8;
    // 存活时间
    private long KEEP_ALIVE_TIME = 3;
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private ThreadPoolExecutor poolExecutor;


    private CustomThreadPool(){}

    public static CustomThreadPool getInstance(){
        if (instance == null){
            synchronized (CustomThreadPool.class){
                if (instance == null){
                    instance = new CustomThreadPool();
                }
            }
        }
        return instance;
    }

    public void init(){
        ThreadFactory threadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        poolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                timeUnit,
                new LinkedBlockingDeque<Runnable>(),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        poolExecutor.allowCoreThreadTimeOut(true);
    }

    public void execute(Runnable runnable){
        if (poolExecutor == null){
            return;
        }
        poolExecutor.execute(runnable);
    }

    public Future submit(Runnable runnable){
        if (poolExecutor == null){
            return null;
        }
        return poolExecutor.submit(runnable);
    }

    public Future submit(Callable callable){
        if (poolExecutor == null){
            return null;
        }
        return poolExecutor.submit(callable);
    }

    public Future submit(FutureTask futureTask){
        if (poolExecutor == null){
            return null;
        }
        return poolExecutor.submit(futureTask);
    }

    public void remove(Runnable runnable){
        if (poolExecutor == null){
            return;
        }
        poolExecutor.remove(runnable);
    }

}
