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

public class MSQAMetricListener<TResult> implements OnCompleteListener<TResult> {
  protected final @NonNull MSQAMetricController mController;
  protected final @Nullable OnCompleteListener<TResult> mCompleteListener;
  protected final @NonNull MSQAErrorToMessageMapper mMessageMapper;
  protected boolean mPostMetric;

  public MSQAMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener) {
    this(controller, completeListener, new MSQAErrorToMessageMapper());
  }

  public MSQAMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener,
      @NonNull MSQAErrorToMessageMapper messageMapper) {
    mController = controller;
    mCompleteListener = completeListener;
    mMessageMapper = messageMapper;
    mPostMetric = true;
  }

  public void setPostMetric(boolean postMetric) {
    mPostMetric = postMetric;
  }

  @Override
  public void onComplete(@Nullable TResult tResult, @Nullable MSQAException error) {
    if (mCompleteListener != null) mCompleteListener.onComplete(tResult, error);
    mMessageMapper.map(mController.getEvent(), tResult, error);
    if (mPostMetric) {
      mController.postMetric();
    }
  }
}
