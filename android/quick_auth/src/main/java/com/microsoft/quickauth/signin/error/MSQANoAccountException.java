package com.microsoft.quickauth.signin.error;

/**
 * This error class is a wrapper msal no account error, created when error code is {@link
 * MSQAErrorString#NO_CURRENT_ACCOUNT}.
 */
public class MSQANoAccountException extends MSQAException {
  public MSQANoAccountException(String errorCode) {
    super(errorCode);
  }

  public MSQANoAccountException(String errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public MSQANoAccountException(String errorCode, String errorMessage, Throwable throwable) {
    super(errorCode, errorMessage, throwable);
  }

  public static MSQANoAccountException create() {
    return new MSQANoAccountException(
        MSQAErrorString.NO_CURRENT_ACCOUNT, MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
  }
}
