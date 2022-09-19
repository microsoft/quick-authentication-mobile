package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quickauth.signin.internal.util.MSQATaskExecutor;
import java.net.HttpURLConnection;

public class MSQAMetricController {
  private final String TAG = "MSQAMetricController";
  private long mStartTime;
  private String mSessionId;
  private String mLibVersion;
  private final @NonNull MSQAMetric.MetricEvent mEvent;

  public MSQAMetricController() {
    mStartTime = System.currentTimeMillis();
    mEvent = new MSQAMetric.MetricEvent();
  }

  public MSQAMetricController start() {
    mStartTime = System.currentTimeMillis();
    return this;
  }

  public MSQAMetricController setSessionId(String sessionId) {
    mSessionId = sessionId;
    return this;
  }

  public MSQAMetricController setLibVersion(String libVersion) {
    mLibVersion = libVersion;
    return this;
  }

  public @NonNull MSQAMetric.MetricEvent getEvent() {
    return mEvent;
  }

  public void postMetric() {
    MSQATaskExecutor.background()
        .execute(
            () -> {
              try {
                // update event duration
                mEvent.setDuration(System.currentTimeMillis() - mStartTime);
                MSQAMetric msqaMetric =
                    new MSQAMetric()
                        .setSessionId(mSessionId)
                        .setLibVersion(mLibVersion)
                        .addEvent(mEvent);
                MSQAHttpRequest request =
                    new MSQAHttpRequest.Builder()
                        .setUrl(
                            MSQAAPIConstant.MS_QUICK_AUTH_ROOT_ENDPOINT
                                + "api/"
                                + msqaMetric.getLibVersion()
                                + "/metric")
                        .addHeader("Content-Type", "application/json")
                        .setParams(msqaMetric.getMetricParams())
                        .builder();
                HttpURLConnection conn = MSQAHttpConnectionClient.post(request);
                conn.getResponseCode();
              } catch (Exception e) {
                MSQALogger.getInstance().error(TAG, "post metric error", e);
              }
            });
  }
}
