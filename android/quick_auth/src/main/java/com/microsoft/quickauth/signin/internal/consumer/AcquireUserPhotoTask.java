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

import androidx.annotation.NonNull;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class AcquireUserPhotoTask
    implements MSQATaskFunction<MSQAAccountInfoInternal, MSQATask<MSQAAccountInfoInternal>> {
  private static final String TAG = "AcquireUserPhotoTask";
  private @NonNull final MSQATracker mTracker;

  public AcquireUserPhotoTask(@NonNull MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final MSQAAccountInfoInternal accountInfo) {
    mTracker.track(TAG, LogLevel.VERBOSE, "start request graph api to get user photo", null);
    return new MSQATask<MSQAAccountInfoInternal>() {
      @Override
      protected void subscribeActual(
          @NonNull MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
        InputStream responseStream = null;
        try {
          HttpURLConnection conn =
              MSQAHttpConnectionClient.createHttpURLConnection(createRequest(accountInfo));
          if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            responseStream = conn.getInputStream();
            accountInfo.setUserPhoto(readAllBytes(responseStream));
            mTracker.track(
                TAG, LogLevel.VERBOSE, "request graph api to get user photo success", null);
          }
        } catch (Exception e) {
          mTracker.track(TAG, LogLevel.ERROR, "request photo api error", e);
        } finally {
          MSQAHttpConnectionClient.safeCloseStream(responseStream);
        }
        consumer.onSuccess(accountInfo);
      }
    }.upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain());
  }

  private static MSQAHttpRequest createRequest(MSQAAccountInfoInternal microsoftAccountInfo) {
    return new MSQAHttpRequest.Builder()
        .setUrl(MSQAAPIConstant.MS_GRAPH_USER_PHOTO_LARGEST)
        .setHttpMethod(MSQAHttpMethod.GET)
        .addHeader("Content-Type", "image/jpg")
        .addHeader(
            "Authorization",
            MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccountInfo.getAccessToken())
        .builder();
  }

  private byte[] readAllBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int numRead;
    int BUFFER_SIZE = 16384;
    byte[] data = new byte[BUFFER_SIZE];
    while ((numRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, numRead);
    }
    return buffer.toByteArray();
  }
}
