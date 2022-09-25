package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQANoAccountException;
import com.microsoft.quickauth.signin.error.MSQANoScopeException;

public class MSQAErrorToMessageMapper implements IMSQAErrorToMessageMapper {

  @Override
  public void map(
      @NonNull MSQAMetric.MetricEvent event, @Nullable Object tResult, @Nullable Exception error) {
    if (tResult != null) {
      event.setMessage(MSQAMetricMessage.SUCCESS);
    } else if (error instanceof MSQACancelException) {
      event.setMessage(MSQAMetricMessage.CANCELED).setComments(error.getMessage());
    } else if (error instanceof MSQANoScopeException) {
      event.setMessage(MSQAMetricMessage.NO_SCOPES).setComments(error.getMessage());
    } else if (error instanceof MSQANoAccountException) {
      event.setMessage(MSQAMetricMessage.NO_ACCOUNT_PRESENT).setComments(error.getMessage());
    } else {
      event
          .setMessage(MSQAMetricMessage.FAILURE)
          .setComments(error != null ? error.getMessage() : null);
    }
  }
}
