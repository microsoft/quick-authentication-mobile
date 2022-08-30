package com.microsoft.quick.auth.signin.task;

import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

import java.util.concurrent.Executor;

public class MainThreadSwitcher implements ThreadSwitcher {
    private final Executor mExecutor;

    public MainThreadSwitcher() {
        mExecutor = TaskExecutorUtil.main();
    }

    @Override
    public void schedule(Runnable runnable) {
        mExecutor.execute(runnable);
    }
}
