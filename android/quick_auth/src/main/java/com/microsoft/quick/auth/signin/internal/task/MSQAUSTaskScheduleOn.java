package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class MSQAUSTaskScheduleOn<T> extends MSQATask<T> {

  private final MSQATask<T> mSource;
  private final MSQAThreadSwitcher mSwitcher;

  public MSQAUSTaskScheduleOn(MSQATask<T> source, MSQAThreadSwitcher switcher) {
    this.mSource = source;
    this.mSwitcher = switcher;
  }

  @Override
  protected void subscribeActual(@NonNull MSQAConsumer<? super T> consumer) {
    final MSQAUSTaskScheduleOnConsumer<? super T> parent =
        new MSQAUSTaskScheduleOnConsumer<>(consumer);
    mSwitcher.schedule(
        new Runnable() {
          @Override
          public void run() {
            mSource.subscribe(parent);
          }
        });
  }

  static class MSQAUSTaskScheduleOnConsumer<T> implements MSQAConsumer<T> {

    private final @NonNull MSQAConsumer<? super T> mDownStreamConsumer;

    public MSQAUSTaskScheduleOnConsumer(@NonNull MSQAConsumer<? super T> consumer) {
      this.mDownStreamConsumer = consumer;
    }

    @Override
    public void onSuccess(T t) {
      mDownStreamConsumer.onSuccess(t);
    }

    @Override
    public void onError(Exception t) {
      mDownStreamConsumer.onError(t);
    }

    @Override
    public void onCancel() {
      mDownStreamConsumer.onCancel();
    }
  }
}
