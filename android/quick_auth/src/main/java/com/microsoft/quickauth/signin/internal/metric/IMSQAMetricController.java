package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;

public interface IMSQAMetricController {

  @NonNull
  MSQAMetric.MetricEvent getEvent();

  MSQAMetricController addExtEvent(@NonNull MSQAMetric.MetricEvent event);

  void postMetric();
}
