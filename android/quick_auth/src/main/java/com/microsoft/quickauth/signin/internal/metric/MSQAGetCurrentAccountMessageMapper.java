package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MSQAGetCurrentAccountMessageMapper extends MSQAErrorToMessageMapper {

  @Override
  public void map(
      @NonNull MSQAMetric.MetricEvent event, @Nullable Object tResult, @Nullable Exception error) {
    // no error && no result is the case no account
    if (tResult == null && error == null) {
      event.setMessage(MSQAMetricMessage.NO_ACCOUNT_PRESENT);
    } else {
      super.map(event, tResult, error);
    }
  }
}
