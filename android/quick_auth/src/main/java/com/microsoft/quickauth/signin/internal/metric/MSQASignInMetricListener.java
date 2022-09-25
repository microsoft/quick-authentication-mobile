package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQAException;

public class MSQASignInMetricListener<TResult> extends MSQAMetricListener<TResult> {

  private boolean mIsSignInButton;

  public MSQASignInMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener,
      boolean isSignInButton) {
    super(controller, completeListener);
    mIsSignInButton = isSignInButton;
  }

  @Override
  public void onComplete(@Nullable TResult tResult, @Nullable MSQAException error) {
    String eventName =
        tResult != null ? MSQAMetricEvent.SIGN_IN_SUCCESS : MSQAMetricEvent.SIGN_IN_FAILURE;
    mController.addExtEvent(
        new MSQAMetric.MetricEvent(eventName)
            .setMessage(
                mIsSignInButton
                    ? MSQAMetricMessage.SIGN_IN_BUTTON
                    : MSQAMetricMessage.START_SIGN_IN_API)
            .setComments(error != null ? error.getMessage() : null));
    super.onComplete(tResult, error);
  }
}
