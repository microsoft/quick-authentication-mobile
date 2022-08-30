package com.microsoft.quick.auth.signin.task;

import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

import java.util.concurrent.ThreadPoolExecutor;

public class IOThreadSwitcher implements ThreadSwitcher {
    private final ThreadPoolExecutor mExecutor;

    public IOThreadSwitcher() {
        mExecutor = TaskExecutorUtil.io();
    }

    @Override
    public void schedule(Runnable runnable) {
        mExecutor.submit(runnable);
    }
}
