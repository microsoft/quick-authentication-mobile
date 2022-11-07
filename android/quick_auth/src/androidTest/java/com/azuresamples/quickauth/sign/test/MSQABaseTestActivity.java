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
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.ClientCreatedListener;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.MSQASignInOptions;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.signinclient.MSQASignInClientFactory;
import com.microsoft.quickauth.signin.test.R;
import junit.framework.Assert;

public class MSQABaseTestActivity extends Activity {

  private MSQASignInClient mClient;
  private MSQATestSingleSignInClient mSignInClientInternal;
  private final MSQATestSafeCountDownLatch mCountDownLatch = new MSQATestSafeCountDownLatch(1);
  protected MSQATestResultListener mTestListener;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MSQASignInClientFactory.setTestSingleClientProvider(
        signInClientApplication -> {
          mSignInClientInternal = new MSQATestSingleSignInClient(signInClientApplication, this);
          return mSignInClientInternal;
        });

    MSQASignInClient.create(
        this,
        new MSQASignInOptions.Builder()
            .setConfigResourceId(R.raw.msqa_test_config_single_account)
            .setEnableLogcatLog(true)
            .build(),
        new ClientCreatedListener() {
          @Override
          public void onCreated(@NonNull MSQASignInClient client) {
            mClient = client;
            onClientLoaded(mClient, mSignInClientInternal);
            mCountDownLatch.countDown();
          }

          @Override
          public void onError(@NonNull MSQAException error) {
            onClientLoaded(mClient, mSignInClientInternal);
            mCountDownLatch.countDown();
          }
        });
  }

  public MSQASignInClient getClient() {
    mCountDownLatch.await();
    return mClient;
  }

  public MSQATestSingleSignInClient getSignInClientInternal() {
    mCountDownLatch.await();
    return mSignInClientInternal;
  }

  public void onClientLoaded(
      @Nullable MSQASignInClient client,
      @Nullable MSQATestSingleSignInClient signInClientInternal) {
    Assert.assertNotNull(client);
    Assert.assertNotNull(signInClientInternal);
  }

  public void setTestResultListener(MSQATestResultListener listener) {
    mTestListener = listener;
  }
}
