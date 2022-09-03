package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class ConsumerScheduleOn<T> extends Task<T> {

  private final @NonNull Task<T> mTask;
  private final @NonNull ThreadSwitcher mScheduler;

  public ConsumerScheduleOn(@NonNull Task<T> task, @NonNull ThreadSwitcher scheduler) {
    this.mTask = task;
    this.mScheduler = scheduler;
  }

  @Override
  protected void startActual(@NonNull Consumer<? super T> consumer) {
    mTask.start(new ScheduleOnConsumer<>(mScheduler, consumer));
  }

  static final class ScheduleOnConsumer<T> implements Consumer<T> {

    private final @NonNull ThreadSwitcher mSwitcher;
    private final @NonNull Consumer<? super T> mDownStreamConsumer;

    public ScheduleOnConsumer(
        @NonNull ThreadSwitcher switcher, @NonNull Consumer<? super T> consumer) {
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
