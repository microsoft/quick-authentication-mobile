package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class CancelableConsumer<T> implements Consumer<T>, Disposable {
  private final Object mLock = new Object();
  private volatile boolean mIsCanceled;
  private final Consumer<? super T> mConsumer;

  public CancelableConsumer(@NonNull Consumer<? super T> consumer) {
    mConsumer = consumer;
  }

  @Override
  public void onSuccess(T t) {
    if (!isDisposed()) {
      mConsumer.onSuccess(t);
    }
  }

  @Override
  public void onError(Exception t) {
    if (!isDisposed()) {
      mConsumer.onError(t);
    }
  }

  @Override
  public void dispose() {
    synchronized (this.mLock) {
      mIsCanceled = true;
    }
  }

  @Override
  public void onCancel() {
    dispose();
  }

  @Override
  public boolean isDisposed() {
    return mIsCanceled;
  }
}
