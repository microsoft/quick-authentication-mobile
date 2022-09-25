package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IMSQAErrorToMessageMapper {
  void map(
      @NonNull MSQAMetric.MetricEvent event,
      @Nullable Object result,
      @Nullable Exception exception);
}
