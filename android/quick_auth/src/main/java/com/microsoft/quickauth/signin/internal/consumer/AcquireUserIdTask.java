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

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQASignInException;
import com.microsoft.quickauth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quickauth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpMethod;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;
import com.microsoft.quickauth.signin.internal.task.MSQAConsumer;
import com.microsoft.quickauth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quickauth.signin.internal.task.MSQATask;
import com.microsoft.quickauth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quickauth.signin.internal.util.MSQATracker;
import org.json.JSONObject;

public class AcquireUserIdTask
    implements MSQATaskFunction<MSQAAccountInfoInternal, MSQATask<MSQAAccountInfoInternal>> {

  private static final String TAG = "AcquireUserIdTask";
  private @NonNull final MSQATracker mTracker;

  public AcquireUserIdTask(@NonNull MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final MSQAAccountInfoInternal accountInfo) {
    return new MSQATask<MSQAAccountInfoInternal>() {
      @Override
      protected void subscribeActual(
          @NonNull MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
        try {
          mTracker.track(
              TAG, LogLevel.VERBOSE, "start request graph api to get account info", null);
          MSQAHttpRequest httpRequest = getHttpRequest(accountInfo);
          String result = MSQAHttpConnectionClient.request(httpRequest);
          if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            accountInfo.setId(jsonObject.optString("id"));
            mTracker.track(
                TAG, LogLevel.VERBOSE, "request graph api to get account info success", null);
          } else {
            mTracker.track(
                TAG,
                LogLevel.VERBOSE,
                "request graph api to get account info error: return empty result error",
                null);
            throw new MSQASignInException(
                MSQAErrorString.HTTP_ACCOUNT_REQUEST_ERROR,
                MSQAErrorString.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
          }
          consumer.onSuccess(accountInfo);
        } catch (Exception e) {
          consumer.onError(e);
        }
      }
    }.upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain());
  }

  private MSQAHttpRequest getHttpRequest(MSQAAccountInfoInternal microsoftAccount) {
    return new MSQAHttpRequest.Builder()
        .setUrl(MSQAAPIConstant.MS_GRAPH_USER_INFO_PATH)
        .setHttpMethod(MSQAHttpMethod.GET)
        .addHeader("Content-Type", "application/json")
        .addHeader(
            "Authorization",
            MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
        .builder();
  }
}
