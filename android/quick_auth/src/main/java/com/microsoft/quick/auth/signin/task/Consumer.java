package com.microsoft.quick.auth.signin.task;

public interface Consumer<T> {

  void onSuccess(T t);

  void onError(Exception t);

  void onCancel();
}
