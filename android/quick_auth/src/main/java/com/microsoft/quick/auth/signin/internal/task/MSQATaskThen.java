package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class MSQATaskThen<T, R> extends MSQATask<R> {

  private final @NonNull MSQATask<T> mSource;
  private final @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> mFunction;

  public MSQATaskThen(
      @NonNull MSQATask<T> source,
      @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> function) {
    this.mSource = source;
    this.mFunction = function;
  }

  @Override
  protected void subscribeActual(@NonNull MSQAConsumer<? super R> consumer) {
    TaskThenConsumer<T, R> parent = new TaskThenConsumer<>(consumer, mFunction);
    mSource.subscribe(parent);
  }

  static class TaskThenConsumer<T, R> implements MSQAConsumer<T> {
    private final @NonNull MSQAConsumer<? super R> mDownStream;
    private final @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> mFunction;
    private MSQADisposable mDisposable;

    public TaskThenConsumer(
        @NonNull MSQAConsumer<? super R> consumer,
        @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> function) {
      this.mDownStream = consumer;
      this.mFunction = function;
    }

    @Override
    public void onSuccess(T t) {
      try {
        MSQATask<? extends R> task = mFunction.apply(t);
        mDisposable = task.subscribe(mDownStream);
      } catch (Exception e) {
        onError(e);
      }
    }

    @Override
    public void onError(Exception t) {
      mDownStream.onError(t);
    }

    @Override
    public void onCancel() {
      if (mDisposable != null) {
        mDisposable.dispose();
      }
      mDownStream.onCancel();
    }
  }
}
