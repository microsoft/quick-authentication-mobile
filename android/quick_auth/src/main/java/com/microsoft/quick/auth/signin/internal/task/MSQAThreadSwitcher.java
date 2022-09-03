package com.microsoft.quick.auth.signin.internal.task;

public interface MSQAThreadSwitcher {
  void schedule(Runnable runnable);
}
