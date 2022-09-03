package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class TaskCreate<T> extends Task<T> {

  private final @NonNull ConsumerHolder<T> mSource;

  public TaskCreate(@NonNull ConsumerHolder<T> source) {
    this.mSource = source;
  }

  @Override
  protected void startActual(@NonNull Consumer<? super T> consumer) {
    CreateConsumer<T> parent = new CreateConsumer<>(consumer);
    mSource.start(parent);
  }

  static class CreateConsumer<T> implements Consumer<T> {

    private final @NonNull Consumer<? super T> mDownStream;

    public CreateConsumer(@NonNull Consumer<? super T> consumer) {
      mDownStream = consumer;
    }

    @Override
    public void onSuccess(T t) {
      mDownStream.onSuccess(t);
    }

    @Override
    public void onError(Exception t) {
      mDownStream.onError(t);
    }

    @Override
    public void onCancel() {
      mDownStream.onCancel();
    }
  }
}
