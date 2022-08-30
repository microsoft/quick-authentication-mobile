package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class DirectToThreadSwitcher implements ThreadSwitcher {

    private final @NonNull
    ThreadSwitcher mSwitcher;
    private final boolean mDirect;

    public DirectToThreadSwitcher(@NonNull ThreadSwitcher switcher, boolean direct) {
        mSwitcher = switcher;
        mDirect = direct;
    }

    @Override
    public void schedule(Runnable runnable) {
        if (mDirect) {
            mSwitcher.schedule(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * If create thread is main thread, finally will return to main thread
     */
    public static ThreadSwitcher directToMainWhenCreateInMain() {
        return new DirectToThreadSwitcher(Switchers.mainThread(), TaskExecutorUtil.isMainThread());
    }

    /**
     * If create is main thread, will change to io thread to run
     */
    public static ThreadSwitcher directToIOWhenCreateInMain() {
        return new DirectToThreadSwitcher(Switchers.io(), TaskExecutorUtil.isMainThread());
    }

    public static ThreadSwitcher directToIOWhenCreateInIO() {
        return new DirectToThreadSwitcher(Switchers.io(), !TaskExecutorUtil.isMainThread());
    }
}
