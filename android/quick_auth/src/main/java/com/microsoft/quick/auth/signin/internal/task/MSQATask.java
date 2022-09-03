package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public abstract class MSQATask<T> {

  public static <T> MSQATask<T> create(@NonNull ConsumerHolder<T> consumerHolder) {
    return new MSQATaskCreate<>(consumerHolder);
  }

  public MSQADisposable subscribe(@NonNull MSQAConsumer<? super T> consumer) {
    MSQACancelableConsumer<T> cancelableConsumer = new MSQACancelableConsumer<>(consumer);
    subscribeActual(cancelableConsumer);
    return cancelableConsumer;
  }

  protected abstract void subscribeActual(@NonNull MSQAConsumer<? super T> consumer);

  public static <T> MSQATask<T> with(@NonNull final T value) {
    return MSQATask.create(
        new ConsumerHolder<T>() {
          @Override
          public void start(@NonNull MSQAConsumer<? super T> consumer) {
            consumer.onSuccess(value);
          }
        });
  }

  public <R> MSQATask<R> then(
      @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> mapper) {
    return new MSQATaskThen<>(this, mapper);
  }

  public MSQATask<T> upStreamScheduleOn(MSQAThreadSwitcher scheduler) {
    return new MSQAUSTaskScheduleOn<>(this, scheduler);
  }

  public MSQATask<T> downStreamSchedulerOn(MSQAThreadSwitcher scheduler) {
    return new MSQADSTScheduleOn<>(this, scheduler);
  }

  public interface ConsumerHolder<T> {
    void start(@NonNull MSQAConsumer<? super T> consumer);
  }
}
