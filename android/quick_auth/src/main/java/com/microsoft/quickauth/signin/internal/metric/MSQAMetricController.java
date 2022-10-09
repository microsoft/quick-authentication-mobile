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

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.BuildConfig;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quickauth.signin.internal.util.MSQATaskExecutor;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MSQAMetricController implements IMSQAMetricController {
  private final String TAG = "MSQAMetricController";
  private long mStartTime;
  private String mTimeStamp;
  private final @NonNull MSQAMetric.MetricEvent mEvent;
  private List<MSQAMetric.MetricEvent> mExtensionEvent;
  private static final String mSessionId = UUID.randomUUID().toString();
  private IMSQAErrorToMessageMapper mMessageMapper;

  public MSQAMetricController(@MSQAMetricEvent String eventName) {
    this(eventName, new MSQAErrorToMessageMapper());
  }

  public MSQAMetricController(
      @MSQAMetricEvent String eventName, @NonNull IMSQAErrorToMessageMapper mapper) {
    mTimeStamp = safeFormatTimeStamp(new Date());
    mStartTime = System.currentTimeMillis();
    mEvent = new MSQAMetric.MetricEvent(eventName).setTimestamp(mTimeStamp);
    mMessageMapper = mapper;

    MSQALogger.getInstance().verbose(TAG, "start time update");
  }

  @Override
  public @NonNull MSQAMetric.MetricEvent getEvent() {
    return mEvent;
  }

  @Override
  public MSQAMetricController addExtEvent(@NonNull MSQAMetric.MetricEvent event) {
    if (mExtensionEvent == null) mExtensionEvent = new ArrayList<>();
    if (TextUtils.isEmpty(event.getTimestamp())) event.setTimestamp(mTimeStamp);
    mExtensionEvent.add(event);
    return this;
  }

  @Override
  public @Nullable List<MSQAMetric.MetricEvent> getExtEvent() {
    return mExtensionEvent;
  }

  @NonNull
  @Override
  public IMSQAErrorToMessageMapper getMessageMapper() {
    return mMessageMapper;
  }

  @Override
  public void postMetric() {
    MSQATaskExecutor.background()
        .execute(
            () -> {
              MSQALogger.getInstance().verbose(TAG, "start post metric");
              try {
                // update event duration
                mEvent.setDuration(System.currentTimeMillis() - mStartTime);
                MSQAMetric msqaMetric =
                    new MSQAMetric()
                        .setSessionId(mSessionId)
                        .setLibVersion(BuildConfig.LIB_VERSION)
                        .addEvent(mEvent);
                if (mExtensionEvent != null && !mExtensionEvent.isEmpty()) {
                  for (MSQAMetric.MetricEvent event : mExtensionEvent) {
                    msqaMetric.addEvent(event);
                  }
                }
                MSQAHttpRequest request =
                    new MSQAHttpRequest.Builder(MSQAAPIConstant.MS_QUICK_AUTH_METRIC_PATH)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Origin", MSQAAPIConstant.MS_QUICK_AUTH_ROOT_ENDPOINT)
                        .setParams(msqaMetric.getMetricParams())
                        .builder();
                HttpURLConnection conn = MSQAHttpConnectionClient.post(request);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                  MSQALogger.getInstance().verbose(TAG, "post metric data success");
                } else {
                  MSQALogger.getInstance()
                      .verbose(TAG, "post metric data failed, responseCode=" + responseCode);
                }
              } catch (Exception e) {
                MSQALogger.getInstance().error(TAG, "post metric error", e);
              }
            });
  }

  private String safeFormatTimeStamp(Date date) {
    try {
      SimpleDateFormat mSimpleDateFormat =
          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
      return mSimpleDateFormat.format(date);
    } catch (Exception e) {
      MSQALogger.getInstance().error(TAG, "simple format error", e);
    }
    return null;
  }
}
