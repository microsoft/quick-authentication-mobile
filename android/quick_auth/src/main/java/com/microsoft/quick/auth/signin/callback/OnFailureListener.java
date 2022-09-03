package com.microsoft.quick.auth.signin.callback;

import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.error.MSQASignInError;

public interface OnFailureListener {
  /**
   * Called once failure and pass the error object.
   *
   * @param error the error result.
   */
  void onFailure(@NonNull MSQASignInError error);
}
