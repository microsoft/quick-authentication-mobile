package com.microsoft.quick.auth.signin.internal.task;

import com.microsoft.quick.auth.signin.internal.util.MSQATaskExecutor;
import java.util.concurrent.Executor;

public class MSQAMainThreadSwitcher implements MSQAThreadSwitcher {
  private final Executor mExecutor;

  public MSQAMainThreadSwitcher() {
    mExecutor = MSQATaskExecutor.main();
  }

  @Override
  public void schedule(Runnable runnable) {
    mExecutor.execute(runnable);
  }
}
