package com.microsoft.quick.auth.signin.task;

public interface ThreadSwitcher {
  void schedule(Runnable runnable);
}
