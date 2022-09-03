package com.microsoft.quick.auth.signin.internal.util;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MSQATaskExecutor {
  private static final Executor MAIN_THREAD = new MSQATaskExecutor.Main();
  private static final ThreadPoolExecutor BACKGROUND_THREAD =
      (ThreadPoolExecutor) Executors.newCachedThreadPool();

  private MSQATaskExecutor() {}

  public static Executor main() {
    return MAIN_THREAD;
  }

  public static ThreadPoolExecutor io() {
    return BACKGROUND_THREAD;
  }

  private static final class Main implements Executor {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public Main() {}

    public final void execute(@NonNull Runnable var1) {
      this.mHandler.post(var1);
    }
  }

  public static boolean isMainThread() {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }
}
