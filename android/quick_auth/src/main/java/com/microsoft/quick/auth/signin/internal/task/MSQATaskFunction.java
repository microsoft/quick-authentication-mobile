package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public interface MSQATaskFunction<T, R> {
  /**
   * Convert the input value to other value.
   *
   * @param t the input value
   * @return the output value
   * @throws Exception on error
   */
  R apply(@NonNull T t) throws Exception;
}
