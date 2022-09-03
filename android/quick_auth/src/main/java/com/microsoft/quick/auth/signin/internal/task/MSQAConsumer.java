package com.microsoft.quick.auth.signin.internal.task;

public interface MSQAConsumer<T> {

  void onSuccess(T t);

  void onError(Exception t);

  void onCancel();
}
