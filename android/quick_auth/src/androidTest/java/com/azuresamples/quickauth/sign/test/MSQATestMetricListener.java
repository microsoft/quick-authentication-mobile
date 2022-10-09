package com.azuresamples.quickauth.sign.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricListener;

// MSQA test metric listener, will not really send metric to server.
public class MSQATestMetricListener<TResult> extends MSQAMetricListener<TResult> {

  public MSQATestMetricListener(
      @NonNull MSQAMetricController controller, @Nullable OnCompleteListener completeListener) {
    super(controller, completeListener);
    setPostMetric(false);
  }

  public MSQATestMetricListener(@Nullable OnCompleteListener completeListener) {
    this(new MSQAMetricController(MSQAMetricEvent.TEST), completeListener);
  }
}
