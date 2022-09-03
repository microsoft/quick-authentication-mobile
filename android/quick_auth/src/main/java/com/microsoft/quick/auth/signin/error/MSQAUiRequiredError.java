package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalUiRequiredException;

/**
 * This error class is a wrapper error class for {@link MsalUiRequiredException}. This error will be
 * created when token silent error instanceof MsalUiRequiredException.
 */
public class MSQAUiRequiredError extends MSQASignInError {

  public MSQAUiRequiredError(String errorCode) {
    super(errorCode);
  }

  public MSQAUiRequiredError(String errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public MSQAUiRequiredError(String errorCode, String errorMessage, Throwable throwable) {
    super(errorCode, errorMessage, throwable);
  }
}
