package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.internal.util.MSQATaskExecutor;

public class MSQADirectThreadSwitcher implements MSQAThreadSwitcher {

  private final @NonNull MSQAThreadSwitcher mSwitcher;
  private final boolean mDirect;

  public MSQADirectThreadSwitcher(@NonNull MSQAThreadSwitcher switcher, boolean direct) {
    mSwitcher = switcher;
    mDirect = direct;
  }

  @Override
  public void schedule(Runnable runnable) {
    if (mDirect) {
      mSwitcher.schedule(runnable);
    } else {
      runnable.run();
    }
  }

  /** If create is main thread, will change to io thread to run */
  public static MSQAThreadSwitcher directToIOWhenCreateInMain() {
    return new MSQADirectThreadSwitcher(MSQASwitchers.io(), MSQATaskExecutor.isMainThread());
  }
}
