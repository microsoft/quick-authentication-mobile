package com.microsoft.quick.auth.signin.internal.task;

import com.microsoft.quick.auth.signin.internal.util.TaskExecutorUtil;
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
