package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class DefaultToScheduler implements Scheduler {
    private final @NonNull
    Scheduler mScheduler;

    public DefaultToScheduler(@NonNull Scheduler scheduler) {
        mScheduler = scheduler;
    }

    @Override
    public void schedule(Runnable runnable) {
        mScheduler.schedule(runnable);
    }
}
