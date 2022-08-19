package com.microsoft.quick.auth.signin.task;

import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

import java.util.concurrent.ThreadPoolExecutor;

public class IOScheduler implements Scheduler {
    private final ThreadPoolExecutor mExecutor;

    public IOScheduler() {
        mExecutor = TaskExecutorUtil.io();
    }

    @Override
    public void schedule(Runnable runnable) {
        mExecutor.submit(runnable);
    }
}
