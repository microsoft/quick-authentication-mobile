package com.microsoft.quick.auth.signin.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.task.DefaultToScheduler;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskExecutorUtil {
    private static final Executor MAIN_THREAD = new TaskExecutorUtil.Main();
    private static final ThreadPoolExecutor BACKGROUND_THREAD =
            (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private TaskExecutorUtil() {
    }

    public static Executor main() {
        return MAIN_THREAD;
    }

    public static ThreadPoolExecutor io() {
        return BACKGROUND_THREAD;
    }

    private static final class Main implements Executor {
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        public Main() {
        }

        public final void execute(@NonNull Runnable var1) {
            this.mHandler.post(var1);
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static Scheduler IO() {
        return new DefaultToScheduler(Schedulers.io());
    }

    public static Scheduler MainThread() {
        return new DefaultToScheduler(Schedulers.mainThread());
    }
}
