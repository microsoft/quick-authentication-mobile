package com.microsoft.quick.auth.signin.callback;

import androidx.annotation.NonNull;

public interface OnSuccessListener<TResult> {
  /**
   * Called once succeed and pass the result object.
   *
   * @param result the success result.
   */
  void onSuccess(@NonNull TResult result);
}
