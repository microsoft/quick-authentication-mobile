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
package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQAException;

public class MSQASignInMetricListener<TResult> extends MSQAMetricListener<TResult> {

  private boolean mIsSignInButton;

  public MSQASignInMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener,
      boolean isSignInButton) {
    super(controller, completeListener);
    mIsSignInButton = isSignInButton;
  }

  public MSQASignInMetricListener(
      @NonNull MSQAMetricController controller, boolean isSignInButton, boolean postMetric) {
    super(controller, null, postMetric);
    mIsSignInButton = isSignInButton;
  }

  @Override
  public void onComplete(@Nullable TResult tResult, @Nullable MSQAException error) {
    String eventName =
        tResult != null ? MSQAMetricEvent.SIGN_IN_SUCCESS : MSQAMetricEvent.SIGN_IN_FAILURE;
    mController.addExtEvent(
        new MSQAMetric.MetricEvent(eventName)
            .setMessage(
                mIsSignInButton
                    ? MSQAMetricMessage.SIGN_IN_BUTTON
                    : MSQAMetricMessage.START_SIGN_IN_API)
            .setComments(error != null ? error.getMessage() : null));
    super.onComplete(tResult, error);
  }
}
