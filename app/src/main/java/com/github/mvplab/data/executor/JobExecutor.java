package com.github.mvplab.data.executor;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date: 08.02.2017
 * Time: 13:51
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class JobExecutor implements Executor {
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int SCALE_POOL_SIZE = 4;
    private static final int INITIAL_POOL_SIZE = PROCESSORS > 1 ? PROCESSORS : 2;
    private static final int MAX_POOL_SIZE = INITIAL_POOL_SIZE * SCALE_POOL_SIZE;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 15;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final ThreadPoolExecutor threadPoolExecutor;

    public JobExecutor() {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadFactory threadFactory = new JobThreadFactory();
        this.threadPoolExecutor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, workQueue, threadFactory);
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        this.threadPoolExecutor.execute(runnable);
    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "mvplab-thread-";
        private int counter = 0;

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, THREAD_NAME + counter++);
        }
    }
}
