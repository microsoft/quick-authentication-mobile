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
package com.azuresamples.quickauth.sign.test;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.azuresamples.quickauth.sign.test.mock.MSQAMockAuthentication;
import com.azuresamples.quickauth.sign.test.mock.MSQAMockIAccount;
import com.azuresamples.quickauth.sign.test.mock.MSQATestMockUtil;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalArgumentException;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalUserCancelException;
import com.microsoft.quickauth.signin.internal.signinclient.MSQASingleSignInClientInternal;

// MSAL client mock class
public class MSQATestSingleSignInClient extends MSQASingleSignInClientInternal {
  protected final @NonNull ISingleAccountPublicClientApplication mSignClient;

  private @MSQATestFlag String mFlag = MSQATestFlag.SUCCESS;
  private final Context mContext;

  public MSQATestSingleSignInClient(
      @NonNull ISingleAccountPublicClientApplication application, Context context) {
    super(application);
    mSignClient = application;
    mContext = context;
  }

  public void setFlag(@MSQATestFlag String flag) {
    mFlag = flag;
  }

  @Override
  public void signIn(
      @NonNull Activity activity,
      @Nullable String loginHint,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    if (MSQATestFlag.Failure.equals(mFlag)) {
      callback.onError(new MsalClientException("sign in test error"));
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      callback.onError(new MsalUserCancelException());
    } else {
      callback.onSuccess(new MSQAMockAuthentication(activity));
    }
  }

  @Override
  public boolean signOut() throws Exception {
    if (MSQATestFlag.Failure.equals(mFlag)) {
      throw new MsalClientException("sign out test error");
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      throw new MsalUserCancelException();
    } else if (TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      throw new MsalClientException(
          MsalClientException.NO_CURRENT_ACCOUNT,
          MsalClientException.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
    }
    return true;
  }

  @Override
  public void signOut(@NonNull ISingleAccountPublicClientApplication.SignOutCallback callback) {
    if (MSQATestFlag.Failure.equals(mFlag)) {
      callback.onError(new MsalClientException("sign out test error"));
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      callback.onError(new MsalUserCancelException());
    } else if (!TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      callback.onSignOut();
    } else {
      callback.onError(
          new MsalClientException(
              MsalClientException.NO_CURRENT_ACCOUNT,
              MsalClientException.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
    }
  }

  @Override
  public IAuthenticationResult acquireTokenSilent(
      @NonNull IAccount account, @NonNull String[] scopes) throws Exception {
    if (MSQATestFlag.Failure.equals(mFlag)) {
      throw new MsalClientException("acquire token silent test error");
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      throw new MsalUserCancelException();
    }
    return new MSQAMockAuthentication(mContext);
  }

  @Override
  public void acquireTokenSilentAsync(
      @NonNull IAccount account,
      @NonNull String[] scopes,
      @NonNull SilentAuthenticationCallback callback) {
    if (MSQATestFlag.Failure.equals(mFlag)) {
      callback.onError(new MsalClientException("acquire token silent test error"));
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      callback.onError(new MsalUserCancelException());
    } else {
      callback.onSuccess(new MSQAMockAuthentication(mContext));
    }
  }

  @Override
  public void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    if (MSQATestFlag.Failure.equals(mFlag)) {
      callback.onError(new MsalClientException("acquire token test error"));
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      callback.onError(new MsalUserCancelException());
    } else if (scopes.length <= 0) {
      callback.onError(
          new MsalArgumentException(
              MsalArgumentException.SCOPE_ARGUMENT_NAME, "acquireToken", "scope is empty"));
    } else {
      callback.onSuccess(new MSQAMockAuthentication(mContext));
    }
  }

  @Nullable
  @Override
  public IAccount getCurrentAccount() {
    if (!TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      return new MSQAMockIAccount(mContext);
    } else {
      return null;
    }
  }

  @Override
  public void getCurrentAccountAsync(
      @NonNull ISingleAccountPublicClientApplication.CurrentAccountCallback callback) {
    if (!TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      callback.onAccountLoaded(new MSQAMockIAccount(mContext));
    } else {
      callback.onAccountLoaded(null);
    }
  }
}
