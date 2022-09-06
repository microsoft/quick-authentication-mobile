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
package com.microsoft.quickauth.signin.internal.consumer;

import android.util.Pair;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quickauth.signin.TokenResult;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQASignInException;
import com.microsoft.quickauth.signin.error.MSQAUiRequiredException;
import com.microsoft.quickauth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;
import com.microsoft.quickauth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quickauth.signin.internal.task.MSQAConsumer;
import com.microsoft.quickauth.signin.internal.task.MSQATask;
import com.microsoft.quickauth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quickauth.signin.internal.util.MSQATracker;

public class AcquireTokenSilentTask
    implements MSQATaskFunction<Pair<IClientApplication, IAccount>, MSQATask<TokenResult>> {

  private @NonNull final String[] mScopes;
  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireTokenSilentTask";

  public AcquireTokenSilentTask(
      @NonNull final String[] scopes, @NonNull final MSQATracker tracker) {
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<TokenResult> apply(@NonNull final Pair<IClientApplication, IAccount> pair) {
    return MSQATask.create(
        new MSQATask.ConsumerHolder<TokenResult>() {
          @Override
          public void start(@NonNull final MSQAConsumer<? super TokenResult> consumer) {
            mTracker.track(
                TAG, LogLevel.VERBOSE, "start request MSAL api acquireTokenSilent", null);
            IClientApplication clientApplication = pair.first;
            IAccount iAccount = pair.second;
            // If no signed account, return error.
            if (iAccount == null) {
              consumer.onError(
                  new MSQASignInException(
                      MSQAErrorString.NO_CURRENT_ACCOUNT,
                      MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
              return;
            }
            clientApplication.acquireTokenSilentAsync(
                iAccount,
                mScopes,
                new SilentAuthenticationCallback() {
                  @Override
                  public void onSuccess(IAuthenticationResult authenticationResult) {
                    consumer.onSuccess(new MSQATokenResultInternal(authenticationResult));
                  }

                  @Override
                  public void onError(MsalException exception) {
                    Exception silentException = exception;
                    // wrapper silent MSAL UI thread and expose new thread for developers
                    if (silentException instanceof MsalUiRequiredException) {
                      mTracker.track(
                          TAG,
                          LogLevel.ERROR,
                          "token silent error instanceof MsalUiRequiredException will return wrap error",
                          null);
                      silentException =
                          new MSQAUiRequiredException(
                              exception.getErrorCode(), exception.getMessage());
                    }
                    consumer.onError(silentException);
                  }
                });
          }
        });
  }
}
