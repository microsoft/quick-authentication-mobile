package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class MSQADSTScheduleOn<T> extends MSQATask<T> {

  private final @NonNull MSQATask<T> mTask;
  private final @NonNull MSQAThreadSwitcher mSwitcher;

  public MSQADSTScheduleOn(@NonNull MSQATask<T> task, @NonNull MSQAThreadSwitcher switcher) {
    this.mTask = task;
    this.mSwitcher = switcher;
  }

  @Override
  protected void subscribeActual(@NonNull MSQAConsumer<? super T> consumer) {
    mTask.subscribe(new MSQADSTScheduleOnConsumer<>(mSwitcher, consumer));
  }

  static final class MSQADSTScheduleOnConsumer<T> implements MSQAConsumer<T> {

    private final @NonNull MSQAThreadSwitcher mSwitcher;
    private final @NonNull MSQAConsumer<? super T> mDownStreamConsumer;

    public MSQADSTScheduleOnConsumer(
        @NonNull MSQAThreadSwitcher switcher, @NonNull MSQAConsumer<? super T> consumer) {
      this.mSwitcher = switcher;
      this.mDownStreamConsumer = consumer;
    }

    @Override
    public void onSuccess(final T t) {
      mSwitcher.schedule(
          new Runnable() {
            @Override
            public void run() {
              mDownStreamConsumer.onSuccess(t);
            }
          });
    }

    @Override
    public void onError(final Exception t) {
      mSwitcher.schedule(
          new Runnable() {
            @Override
            public void run() {
              mDownStreamConsumer.onError(t);
            }
          });
    }

    @Override
    public void onCancel() {
      mDownStreamConsumer.onCancel();
    }
  }
}
