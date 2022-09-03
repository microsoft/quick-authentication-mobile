package com.microsoft.quick.auth.signin.internal.task;

import com.microsoft.quick.auth.signin.internal.util.MSQATaskExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class MSQAIOThreadSwitcher implements MSQAThreadSwitcher {
  private final ThreadPoolExecutor mExecutor;

  public MSQAIOThreadSwitcher() {
    mExecutor = MSQATaskExecutor.io();
  }

  @Override
  public void schedule(Runnable runnable) {
    mExecutor.submit(runnable);
  }
}
