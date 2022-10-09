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
package com.azuresamples.quickauth.sign.test.apitest;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import com.azuresamples.quickauth.sign.test.MSQABaseTestActivity;
import com.azuresamples.quickauth.sign.test.MSQATestFlag;
import com.microsoft.quickauth.signin.AccountInfo;
import com.microsoft.quickauth.signin.TokenResult;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.metric.MSQAGetCurrentAccountMessageMapper;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricListener;
import com.microsoft.quickauth.signin.internal.metric.MSQASignInMetricListener;
import com.microsoft.quickauth.signin.internal.metric.MSQASignOutMessageMapper;
import com.microsoft.quickauth.signin.test.R;

public class MSQAApiTestActivity extends MSQABaseTestActivity {
  private String[] mScopes = new String[] {"user.read"};

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.msqa_api_test_activity);
  }

  public void testSignInApi(@MSQATestFlag String flag) {
    getSignInClientInternal().setFlag(flag);
    getClient()
        .signIn(
            this,
            new MSQASignInMetricListener<AccountInfo>(
                new MSQAMetricController(MSQAMetricEvent.SIGN_IN), false, false) {
              @Override
              public void onComplete(
                  @Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
                super.onComplete(accountInfo, error);
                if (mTestListener != null)
                  mTestListener.onResult(getController(), accountInfo, error);
              }
            });
  }

  public void testSignInApiSuccess(View v) {
    testSignInApi(MSQATestFlag.SUCCESS);
  }

  public void testSignInApiFailure(View v) {
    testSignInApi(MSQATestFlag.Failure);
  }

  public void testSignInApiCancel(View v) {
    testSignInApi(MSQATestFlag.CANCEL);
  }

  public void testSignOutApi(View v) {
    getSignInClientInternal().setFlag(MSQATestFlag.SUCCESS);
    getClient()
        .signOut(
            new MSQAMetricListener<Boolean>(
                new MSQAMetricController(MSQAMetricEvent.SIGN_OUT, new MSQASignOutMessageMapper()),
                false) {
              @Override
              public void onComplete(@Nullable Boolean o, @Nullable MSQAException error) {
                super.onComplete(o, error);
                if (mTestListener != null) mTestListener.onResult(getController(), o, error);
              }
            });
  }

  public void testGetCurrentAccountApi(View v) {
    getSignInClientInternal().setFlag(MSQATestFlag.SUCCESS);
    getClient()
        .getCurrentAccount(
            new MSQAMetricListener<AccountInfo>(
                new MSQAMetricController(
                    MSQAMetricEvent.GET_CURRENT_ACCOUNT, new MSQAGetCurrentAccountMessageMapper()),
                false) {
              @Override
              public void onComplete(
                  @Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
                super.onComplete(accountInfo, error);
                if (mTestListener != null)
                  mTestListener.onResult(getController(), accountInfo, error);
              }
            });
  }

  public void testAcquireTokenApi(@MSQATestFlag String flag, String[] scopes) {
    getSignInClientInternal().setFlag(flag);
    getClient()
        .acquireToken(
            this,
            scopes,
            new MSQAMetricListener<TokenResult>(
                new MSQAMetricController(MSQAMetricEvent.ACQUIRE_TOKEN), false) {
              @Override
              public void onComplete(
                  @Nullable TokenResult tokenResult, @Nullable MSQAException error) {
                super.onComplete(tokenResult, error);
                if (mTestListener != null)
                  mTestListener.onResult(getController(), tokenResult, error);
              }
            });
  }

  public void testAcquireTokenApiSuccess(View v) {
    testAcquireTokenApi(MSQATestFlag.SUCCESS, mScopes);
  }

  public void testAcquireTokenApiFailure(View v) {
    testAcquireTokenApi(MSQATestFlag.Failure, mScopes);
  }

  public void testAcquireTokenApiCancel(View v) {
    testAcquireTokenApi(MSQATestFlag.CANCEL, mScopes);
  }

  public void testAcquireTokenApiNoScope(View v) {
    testAcquireTokenApi(MSQATestFlag.SUCCESS, new String[] {});
  }

  public void testAcquireTokenSilentApi(@MSQATestFlag String flag, String[] scopes) {
    getSignInClientInternal().setFlag(flag);
    getClient()
        .acquireTokenSilent(
            scopes,
            new MSQAMetricListener<TokenResult>(
                new MSQAMetricController(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT), false) {
              @Override
              public void onComplete(
                  @Nullable TokenResult tokenResult, @Nullable MSQAException error) {
                super.onComplete(tokenResult, error);
                if (mTestListener != null)
                  mTestListener.onResult(getController(), tokenResult, error);
              }
            });
  }

  public void testAcquireTokenSilentApiSuccess(View v) {
    testAcquireTokenSilentApi(MSQATestFlag.SUCCESS, mScopes);
  }

  public void testAcquireTokenSilentApiFailure(View v) {
    testAcquireTokenSilentApi(MSQATestFlag.Failure, mScopes);
  }

  public void testAcquireTokenSilentApiCancel(View v) {
    testAcquireTokenSilentApi(MSQATestFlag.CANCEL, mScopes);
  }
}
