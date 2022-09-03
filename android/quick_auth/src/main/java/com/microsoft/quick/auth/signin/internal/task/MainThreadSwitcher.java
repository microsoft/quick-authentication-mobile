package com.microsoft.quick.auth.signin.internal.task;

import com.microsoft.quick.auth.signin.internal.util.TaskExecutorUtil;
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
