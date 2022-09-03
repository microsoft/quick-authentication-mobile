package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class MSQATaskCreate<T> extends MSQATask<T> {

  private final @NonNull ConsumerHolder<T> mSource;

  public MSQATaskCreate(@NonNull ConsumerHolder<T> source) {
    this.mSource = source;
  }

  @Override
  protected void subscribeActual(@NonNull MSQAConsumer<? super T> consumer) {
    MSQATaskCreateConsumer<T> parent = new MSQATaskCreateConsumer<>(consumer);
    mSource.start(parent);
  }

  static class MSQATaskCreateConsumer<T> implements MSQAConsumer<T> {

    private final @NonNull MSQAConsumer<? super T> mDownStream;

    public MSQATaskCreateConsumer(@NonNull MSQAConsumer<? super T> consumer) {
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
