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
package com.microsoft.quickauth.signin.internal.signinclient;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.quickauth.signin.AccountInfo;
import java.util.List;

public interface IClientApplication {
  void signIn(
      @NonNull Activity activity,
      @Nullable String loginHint,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback);

  @WorkerThread
  boolean signOut() throws Exception;

  void signOut(@NonNull final ISingleAccountPublicClientApplication.SignOutCallback callback);

  @WorkerThread
  IAuthenticationResult acquireTokenSilent(@NonNull IAccount account, @NonNull String[] scopes)
      throws Exception;

  void acquireTokenSilentAsync(
      @NonNull IAccount account,
      @NonNull String[] scopes,
      @NonNull SilentAuthenticationCallback callback);

  void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback);

  @Nullable
  @WorkerThread
  IAccount getCurrentAccount() throws Exception;

  void getCurrentAccountAsync(
      @NonNull ISingleAccountPublicClientApplication.CurrentAccountCallback callback);

  @Nullable
  IAccount getAccount(@NonNull AccountInfo accountInfo) throws Exception;

  /** Returns a List of {@link IAccount} objects for which this application has RefreshTokens. */
  @Nullable
  @WorkerThread
  List<IAccount> getAccounts() throws Exception;
}
