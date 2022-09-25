package com.microsoft.quickauth.signin.error;

import androidx.annotation.Nullable;
import com.microsoft.identity.client.exception.MsalArgumentException;

/**
 * This error class is a wrapper for MSAL no scope error, created when error operationName is {@link
 * MsalArgumentException#SCOPE_ARGUMENT_NAME}.
 */
public class MSQANoScopeException extends MSQAException {
  public MSQANoScopeException(String errorCode) {
    super(errorCode);
  }

  public MSQANoScopeException(String errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public MSQANoScopeException(String errorCode, String errorMessage, Throwable throwable) {
    super(errorCode, errorMessage, throwable);
  }

  public static MSQANoScopeException create(@Nullable Exception exception) {
    return new MSQANoScopeException(
        MSQAErrorString.NO_SCOPE_ERROR,
        exception != null ? exception.getMessage() : MSQAErrorString.NO_SCOPE_ERROR_MESSAGE);
  }
}
