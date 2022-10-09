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
import androidx.annotation.NonNull;
import com.microsoft.quickauth.signin.ClientCreatedListener;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.MSQASignInOptions;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.test.R;
import junit.framework.Assert;
import org.junit.Before;

public class MSQABaseTest {
  public MSQASignInClient mClient;
  public MSQATestSingleSignInClient mSignInClientInternal;
  public Activity mActivity;

  public Activity getActivity() {
    return mActivity;
  }

  @Before
  public void setup() {
    mActivity = getActivity();
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    MSQASignInClient.create(
        mActivity,
        new MSQASignInOptions.Builder()
            .setConfigResourceId(R.raw.msqa_test_config_single_account)
            .setEnableLogcatLog(true)
            .build()
            .setTestSingleClientProvider(
                signInClientApplication -> {
                  mSignInClientInternal =
                      new MSQATestSingleSignInClient(signInClientApplication, mActivity);
                  return mSignInClientInternal;
                }),
        new ClientCreatedListener() {
          @Override
          public void onCreated(@NonNull MSQASignInClient client) {
            mClient = client;
            latch.countDown();
          }

          @Override
          public void onError(@NonNull MSQAException error) {
            latch.countDown();
          }
        });
    latch.await();
    Assert.assertNotNull(mClient);
  }
}
