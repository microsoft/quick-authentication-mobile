package com.microsoft.quick.auth.signin.internal.task;

public interface MSQADisposable {
  /** Cancel this request and release resource. */
  void dispose();

  /**
   * @return true if this resource has been disposed.
   */
  boolean isDisposed();
}
