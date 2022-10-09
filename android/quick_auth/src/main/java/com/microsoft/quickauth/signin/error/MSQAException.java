//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.error;

import com.microsoft.identity.client.exception.MsalArgumentException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.identity.client.exception.MsalUserCancelException;
import com.microsoft.identity.client.internal.MsalUtils;

public class MSQAException extends Exception {

  private final String mErrorCode;
  private Exception mSuppressedException;

  public MSQAException() {
    this(null);
  }

  /**
   * Initiates the detailed error code.
   *
   * @param errorCode The error code contained in the exception.
   */
  public MSQAException(final String errorCode) {
    this(errorCode, null);
  }

  /**
   * Initiates the {@link MSQAException} with error code and error message.
   *
   * @param errorCode The error code contained in the exception.
   * @param errorMessage The error message contained in the exception.
   */
  public MSQAException(final String errorCode, final String errorMessage) {
    this(errorCode, errorMessage, null);
  }

  /**
   * Initiates the {@link MSQAException} with error code, error message and throwable.
   *
   * @param errorCode The error code contained in the exception.
   * @param errorMessage The error message contained in the exception.
   * @param throwable The {@link Throwable} contains the cause for the exception.
   */
  public MSQAException(
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
   * @return The error code for the exception, could be null. {@link MSQAException} is the top level
   *     base exception, for the constants value of all the error code.
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

  public static MSQAException mapToMSQAException(Exception exception) {
    if (exception instanceof MSQAException) return (MSQAException) exception;

    MSQAException msqaException;
    if (exception instanceof MsalException) {
      if (exception instanceof MsalUserCancelException
          || MsalServiceException.ACCESS_DENIED.equals(
              ((MsalException) exception).getErrorCode())) {
        msqaException = MSQACancelException.create(exception);
      } else if (exception instanceof MsalArgumentException
          && MsalArgumentException.SCOPE_ARGUMENT_NAME.equals(
              ((MsalArgumentException) exception).getOperationName())) {
        msqaException = MSQANoScopeException.create(exception);
      } else if (exception instanceof MsalUiRequiredException) {
        msqaException =
            new MSQAUiRequiredException(
                ((MsalUiRequiredException) exception).getErrorCode(), exception.getMessage());
      } else if (MSQAErrorString.NO_CURRENT_ACCOUNT.equals(
          ((MsalException) exception).getErrorCode())) {
        msqaException = MSQANoAccountException.create(exception);
      } else {
        msqaException =
            new MSQAException(((MsalException) exception).getErrorCode(), exception.getMessage());
      }
    } else if (exception instanceof InterruptedException) {
      msqaException = new MSQAException(MSQAErrorString.INTERRUPTED_ERROR, exception.getMessage());
    } else {
      msqaException = new MSQAException(MSQAErrorString.UNKNOWN_ERROR, exception.getMessage());
    }
    msqaException.setSuppressedException(exception);
    return msqaException;
  }
}
