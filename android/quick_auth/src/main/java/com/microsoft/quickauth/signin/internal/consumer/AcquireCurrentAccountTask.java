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
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;
import com.microsoft.quickauth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quickauth.signin.internal.task.MSQAConsumer;
import com.microsoft.quickauth.signin.internal.task.MSQATask;
import com.microsoft.quickauth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quickauth.signin.internal.util.MSQATracker;

public class AcquireCurrentAccountTask
    implements MSQATaskFunction<IClientApplication, MSQATask<Pair<IClientApplication, IAccount>>> {

  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireCurrentAccountTask";

  public AcquireCurrentAccountTask(@NonNull final MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public MSQATask<Pair<IClientApplication, IAccount>> apply(
      @NonNull final IClientApplication iClientApplication) throws Exception {
    return MSQATask.create(
        new MSQATask.ConsumerHolder<Pair<IClientApplication, IAccount>>() {
          @Override
          public void start(
              @NonNull final MSQAConsumer<? super Pair<IClientApplication, IAccount>> consumer) {
            mTracker.track(TAG, LogLevel.VERBOSE, "start request MSAL api getCurrentAccount", null);
            iClientApplication.getCurrentAccountAsync(
                new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
                  @Override
                  public void onAccountLoaded(@Nullable IAccount activeAccount) {
                    consumer.onSuccess(new Pair<>(iClientApplication, activeAccount));
                  }

                  @Override
                  public void onAccountChanged(
                      @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                    consumer.onSuccess(new Pair<>(iClientApplication, currentAccount));
                  }

                  @Override
                  public void onError(@NonNull MsalException exception) {
                    consumer.onSuccess(new Pair<>(iClientApplication, (IAccount) null));
                  }
                });
          }
        });
  }
}
