package com.wewin.hichat.androidlib.threadpool;

import android.os.Process;
import android.support.annotation.NonNull;
import java.util.concurrent.ThreadFactory;


/**
 * 优先级线程工厂
 * @author Darren
 * Created by Darren on 2019/5/22
 **/
public class PriorityThreadFactory implements ThreadFactory {

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
