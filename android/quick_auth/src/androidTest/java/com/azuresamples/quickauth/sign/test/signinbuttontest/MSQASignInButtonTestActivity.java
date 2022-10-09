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
package com.azuresamples.quickauth.sign.test.signinbuttontest;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.azuresamples.quickauth.sign.test.MSQABaseTestActivity;
import com.azuresamples.quickauth.sign.test.MSQATestSingleSignInClient;
import com.microsoft.quickauth.signin.AccountInfo;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQASignInMetricListener;
import com.microsoft.quickauth.signin.test.R;
import com.microsoft.quickauth.signin.view.MSQASignInButton;

public class MSQASignInButtonTestActivity extends MSQABaseTestActivity {

  private MSQASignInButton mSignInButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.msqa_sign_in_button_test_activity);
    mSignInButton = findViewById(R.id.signInButton);
  }

  @Override
  public void onClientLoaded(
      @Nullable MSQASignInClient client,
      @Nullable MSQATestSingleSignInClient signInClientInternal) {
    super.onClientLoaded(client, signInClientInternal);
    mSignInButton.setSignInCallback(
        this,
        client,
        new MSQASignInMetricListener<AccountInfo>(
            new MSQAMetricController(MSQAMetricEvent.BUTTON_SIGN_IN), true, false) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            if (mTestListener != null) mTestListener.onResult(getController(), accountInfo, error);
          }
        });
  }
}
