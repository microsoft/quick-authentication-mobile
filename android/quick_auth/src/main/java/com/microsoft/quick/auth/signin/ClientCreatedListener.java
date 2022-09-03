package com.microsoft.quick.auth.signin;

import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.error.MSQASignInException;

public interface ClientCreatedListener {
  /**
   * Called once when the client object is successfully created.
   *
   * @param client Successfully created client object.
   */
  void onCreated(@NonNull MSQASignInClient client);

  /**
   * Called once failure and pass the error.
   *
   * @param error Error for creating a client
   */
  void onError(@NonNull MSQASignInException error);
}
