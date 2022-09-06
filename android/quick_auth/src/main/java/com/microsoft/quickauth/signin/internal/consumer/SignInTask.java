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
import com.microsoft.quickauth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;
import com.microsoft.quickauth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quickauth.signin.internal.task.MSQAConsumer;
import com.microsoft.quickauth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quickauth.signin.internal.task.MSQATask;
import com.microsoft.quickauth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quickauth.signin.internal.util.MSQATaskTracker;

public class SignInTask
    implements MSQATaskFunction<
        Pair<IClientApplication, IAccount>, MSQATask<MSQAAccountInfoInternal>> {

  private @NonNull final Activity mActivity;
  private @NonNull final String[] mScopes;
  private @NonNull final MSQATaskTracker mTracker;
  private static final String TAG = "SignInTask";

  public SignInTask(
      @NonNull final Activity activity,
      @NonNull String[] scopes,
      @NonNull final MSQATaskTracker tracker) {
    mActivity = activity;
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final Pair<IClientApplication, IAccount> pair) {
    final IClientApplication clientApplication = pair.first;
    IAccount iAccount = pair.second;
    if (iAccount == null) {
      return MSQATask.create(
          new MSQATask.ConsumerHolder<MSQAAccountInfoInternal>() {
            @Override
            public void start(
                @NonNull final MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
              mTracker.track(TAG, LogLevel.VERBOSE, "start request msal sign in api", null);
              clientApplication.signIn(
                  mActivity,
                  null,
                  mScopes,
                  new AuthenticationCallback() {

                    @Override
                    public void onSuccess(final IAuthenticationResult authenticationResult) {
                      mTracker.track(TAG, LogLevel.VERBOSE, "request msal sign in success", null);
                      MSQAAccountInfoInternal account =
                          MSQAAccountInfoInternal.getAccount(authenticationResult);
                      consumer.onSuccess(account);
                    }

                    @Override
                    public void onError(final MsalException exception) {
                      mTracker.track(
                          TAG, LogLevel.VERBOSE, "request msal sign in error", exception);

                      consumer.onError(exception);
                    }

                    @Override
                    public void onCancel() {
                      mTracker.track(TAG, LogLevel.VERBOSE, "request msal sign in cancel", null);
                      consumer.onCancel();
                    }
                  });
            }
          });
    } else {
      return MSQATask.with(pair)
          .then(new AcquireCurrentTokenTask(mActivity, true, mScopes, mTracker))
          .upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain());
    }
  }
}
