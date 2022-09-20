package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQAException;

public class MSQASignInMetricListener<TResult> extends MSQAMetricListener<TResult> {

  private final @NonNull MSQAMetric.MetricEvent mSignInEvent;

  public MSQASignInMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener,
      boolean isSignInButton) {
    super(controller, completeListener);
    mSignInEvent =
        new MSQAMetric.MetricEvent()
            .setMessage(
                isSignInButton
                    ? MSQAMetricMessage.SIGN_IN_BUTTON
                    : MSQAMetricMessage.START_SIGN_IN_API);
  }

  @Override
  public void onComplete(@Nullable TResult tResult, @Nullable MSQAException error) {
    mSignInEvent
        .setEventName(
            tResult != null ? MSQAMetricEvent.SIGN_IN_SUCCESS : MSQAMetricEvent.SIGN_IN_FAILURE)
        .setComments(error != null ? error.getMessage() : null);
    mController.addExtEvent(mSignInEvent);
    super.onComplete(tResult, error);
  }
}
