package com.microsoft.quick.auth.signin.internal.task;

public interface ThreadSwitcher {
  void schedule(Runnable runnable);
}
