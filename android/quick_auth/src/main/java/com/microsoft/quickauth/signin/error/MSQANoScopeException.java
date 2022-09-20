package com.microsoft.quickauth.signin.error;

import com.microsoft.identity.client.exception.MsalArgumentException;

/**
 * This error class is a wrapper msal no scope error, created when error operationName is {@link
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
}
