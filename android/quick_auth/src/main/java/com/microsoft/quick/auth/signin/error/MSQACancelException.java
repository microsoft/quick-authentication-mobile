package com.microsoft.quick.auth.signin.error;

/** Exception for user cancelling the flow. */
public class MSQACancelException extends MSQASignInException {
  public MSQACancelException(String errorCode) {
    super(errorCode);
  }

  public MSQACancelException(String errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public MSQACancelException(String errorCode, String errorMessage, Throwable throwable) {
    super(errorCode, errorMessage, throwable);
  }

  public static MSQACancelException create() {
    return new MSQACancelException(
        MSQAErrorString.USER_CANCEL_ERROR, MSQAErrorString.USER_CANCEL_ERROR_MESSAGE);
  }
}
