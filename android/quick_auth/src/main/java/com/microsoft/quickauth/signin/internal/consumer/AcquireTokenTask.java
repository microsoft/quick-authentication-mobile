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

import android.app.Activity;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quickauth.signin.TokenResult;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQASignInException;
import com.microsoft.quickauth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;
import com.microsoft.quickauth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quickauth.signin.internal.task.MSQAConsumer;
import com.microsoft.quickauth.signin.internal.task.MSQATask;
import com.microsoft.quickauth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quickauth.signin.internal.util.MSQATaskTracker;

public class AcquireTokenTask
    implements MSQATaskFunction<Pair<IClientApplication, IAccount>, MSQATask<TokenResult>> {
  private @NonNull final Activity mActivity;
  private @NonNull final String[] mScopes;
  private @NonNull final MSQATaskTracker mTracker;
  private static final String TAG = "AcquireTokenTask";

  public AcquireTokenTask(
      @NonNull final Activity activity,
      @NonNull final String[] scopes,
      @NonNull final MSQATaskTracker tracker) {
    mActivity = activity;
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<TokenResult> apply(@NonNull final Pair<IClientApplication, IAccount> pair) {
    return MSQATask.create(
        new MSQATask.ConsumerHolder<TokenResult>() {
          @Override
          public void start(@NonNull final MSQAConsumer<? super TokenResult> consumer) {
            mTracker.track(TAG, LogLevel.VERBOSE, "start request MSAL acquireToken api", null);
            IClientApplication iClientApplication = pair.first;
            IAccount iAccount = pair.second;
            // If no signed account, return error.
            if (iAccount == null) {
              consumer.onError(
                  new MSQASignInException(
                      MSQAErrorString.NO_CURRENT_ACCOUNT,
                      MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
              return;
            }
            iClientApplication.acquireToken(
                mActivity,
                mScopes,
                new AuthenticationCallback() {
                  @Override
                  public void onCancel() {
                    mTracker.track(
                        TAG, LogLevel.VERBOSE, "request MSAL acquireToken api cancel", null);
                    consumer.onCancel();
                  }

                  @Override
                  public void onSuccess(final IAuthenticationResult authenticationResult) {
                    mTracker.track(
                        TAG, LogLevel.VERBOSE, "request MSAL acquireToken api success", null);
                    consumer.onSuccess(new MSQATokenResultInternal(authenticationResult));
                  }

                  @Override
                  public void onError(final MsalException exception) {
                    mTracker.track(
                        TAG, LogLevel.ERROR, "request MSAL acquireToken api error", exception);
                    consumer.onError(exception);
                  }
                });
          }
        });
  }
}
