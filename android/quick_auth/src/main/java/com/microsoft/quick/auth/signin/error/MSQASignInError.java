package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.internal.MsalUtils;

public class MSQASignInError extends Exception {

  private final String mErrorCode;
  private Exception mSuppressedException;

  public MSQASignInError() {
    this(null);
  }

  /**
   * Initiates the detailed error code.
   *
   * @param errorCode The error code contained in the exception.
   */
  public MSQASignInError(final String errorCode) {
    this(errorCode, null);
  }

  /**
   * Initiates the {@link MSQASignInError} with error code and error message.
   *
   * @param errorCode The error code contained in the exception.
   * @param errorMessage The error message contained in the exception.
   */
  public MSQASignInError(final String errorCode, final String errorMessage) {
    this(errorCode, errorMessage, null);
  }

  /**
   * Initiates the {@link MSQASignInError} with error code, error message and throwable.
   *
   * @param errorCode The error code contained in the exception.
   * @param errorMessage The error message contained in the exception.
   * @param throwable The {@link Throwable} contains the cause for the exception.
   */
  public MSQASignInError(
      final String errorCode, final String errorMessage, final Throwable throwable) {
    super(errorMessage, throwable);
    mErrorCode = errorCode;
  }

  public void setSuppressedException(Exception e) {
    mSuppressedException = e;
  }

  public Exception getSuppressedException() {
    return mSuppressedException;
  }

  /**
   * @return The error code for the exception, could be null. {@link MSQASignInError} is the top
   *     level base exception, for the constants value of all the error code.
   */
  public String getErrorCode() {
    return mErrorCode;
  }

  /**
   * {@inheritDoc} Return the detailed description explaining why the exception is returned back.
   */
  @Override
  public String getMessage() {
    if (!MsalUtils.isEmpty(super.getMessage())) {
      return super.getMessage();
    }
    return "";
  }

  public static MSQASignInError create(Exception exception) {
    if (exception instanceof MSQASignInError) return (MSQASignInError) exception;

    MSQASignInError signInException;
    if (exception instanceof MsalException) {
      signInException =
          new MSQASignInError(((MsalException) exception).getErrorCode(), exception.getMessage());
    } else if (exception instanceof InterruptedException) {
      signInException =
          new MSQASignInError(MSQAErrorString.INTERRUPTED_ERROR, exception.getMessage());
    } else {
      signInException = new MSQASignInError(MSQAErrorString.UNKNOWN_ERROR, exception.getMessage());
    }
    signInException.setSuppressedException(exception);
    return signInException;
  }
}
