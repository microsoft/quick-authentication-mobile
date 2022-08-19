package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class DirectToScheduler implements Scheduler {

    private final @NonNull
    Scheduler mScheduler;
    private final boolean mDirect;

    public DirectToScheduler(@NonNull Scheduler scheduler, boolean direct) {
        mScheduler = scheduler;
        mDirect = direct;
    }

    @Override
    public void schedule(Runnable runnable) {
        if (mDirect) {
            mScheduler.schedule(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * If create thread is main thread, finally will return to main thread
     */
    public static Scheduler directToMainWhenCreateInMain() {
        return new DirectToScheduler(Schedulers.mainThread(), TaskExecutorUtil.isMainThread());
    }

    /**
     * If create is main thread, will change to io thread to run
     */
    public static Scheduler directToIOWhenCreateInMain() {
        return new DirectToScheduler(Schedulers.io(), TaskExecutorUtil.isMainThread());
    }

    public static Scheduler directToIOWhenCreateInIO() {
        return new DirectToScheduler(Schedulers.io(), !TaskExecutorUtil.isMainThread());
    }
}
