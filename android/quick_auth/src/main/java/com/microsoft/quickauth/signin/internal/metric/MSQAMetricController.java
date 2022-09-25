package com.microsoft.quickauth.signin.internal.metric;

import android.text.TextUtils;
import androidx.annotation.NonNull;
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

  public MSQAMetricController(@MSQAMetricEvent String eventName) {
    mTimeStamp = safeFormatTimeStamp(new Date());
    mStartTime = System.currentTimeMillis();
    mEvent = new MSQAMetric.MetricEvent(eventName).setTimestamp(mTimeStamp);

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
