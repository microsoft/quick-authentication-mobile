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
package com.microsoft.quick.auth.signin.internal.signinclient;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.quick.auth.signin.AccountInfo;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import java.util.ArrayList;
import java.util.List;

public class SingleClientApplication implements IClientApplication {

  private final @NonNull ISingleAccountPublicClientApplication mSignClient;
  private final List<IAccount> mAccounts = new ArrayList<>();

  public SingleClientApplication(@NonNull ISingleAccountPublicClientApplication signClient) {
    mSignClient = signClient;
  }

  @Override
  public void signIn(
      @NonNull Activity activity,
      @Nullable String loginHint,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    mSignClient.signIn(activity, loginHint, scopes, callback);
  }

  @Override
  public boolean signOut() throws Exception {
    return mSignClient.signOut();
  }

  @Override
  public void signOut(@NonNull ISingleAccountPublicClientApplication.SignOutCallback callback) {
    mSignClient.signOut(callback);
  }

  @Override
  public IAuthenticationResult acquireTokenSilent(
      @NonNull IAccount account, @NonNull String[] scopes) throws Exception {
    return mSignClient.acquireTokenSilent(scopes, account.getAuthority());
  }

  @Override
  public void acquireTokenSilentAsync(
      @NonNull IAccount account,
      @NonNull String[] scopes,
      @NonNull SilentAuthenticationCallback callback) {
    mSignClient.acquireTokenSilentAsync(scopes, account.getAuthority(), callback);
  }

  @Override
  public void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    mSignClient.acquireToken(activity, scopes, callback);
  }

  @Nullable
  @Override
  public IAccount getCurrentAccount() throws Exception {
    ICurrentAccountResult currentAccount = mSignClient.getCurrentAccount();
    if (currentAccount != null && currentAccount.getCurrentAccount() != null) {
      return currentAccount.getCurrentAccount();
    }
    return null;
  }

  @Override
  public void getCurrentAccountAsync(
      @NonNull ISingleAccountPublicClientApplication.CurrentAccountCallback callback) {
    mSignClient.getCurrentAccountAsync(callback);
  }

  @Nullable
  @Override
  public IAccount getAccount(@NonNull AccountInfo accountInfo) throws Exception {
    if (accountInfo instanceof MSQAAccountInfoInternal
        && ((MSQAAccountInfoInternal) accountInfo).getIAccount() != null) {
      return ((MSQAAccountInfoInternal) accountInfo).getIAccount();
    }
    return null;
  }

  @Nullable
  @Override
  public List<IAccount> getAccounts() throws Exception {
    IAccount account = getCurrentAccount();
    mAccounts.clear();
    if (account != null) {
      mAccounts.add(account);
    }
    return mAccounts;
  }
}
