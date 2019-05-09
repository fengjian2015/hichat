package com.wewin.hichat.component.threadPool;

import android.os.Process;
import android.support.annotation.NonNull;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by Darren on 2019/4/13
 **/
public class CustomThreadPool {

    private static CustomThreadPool instance;
    private int corePoolSize = 3; // 核心线程的数量
    private int maximumPoolSize = 8;// 最大线程数量
    private long keepAliveTime = 3;// 存活时间
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
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
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


    //优先级线程工厂
    private class PriorityThreadFactory implements ThreadFactory {

        private final int mThreadPriority;

        PriorityThreadFactory(int threadPriority) {
            mThreadPriority = threadPriority;
        }

        @Override
        public Thread newThread(@NonNull final Runnable runnable) {
            Runnable wrapperRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Process.setThreadPriority(mThreadPriority);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runnable.run();
                }
            };
            return new Thread(wrapperRunnable);
        }
    }


}
