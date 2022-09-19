package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQAException;

public class MSQAMetricListener<TResult> implements OnCompleteListener<TResult> {
  private final @NonNull MSQAMetricController mController;
  private final @Nullable OnCompleteListener<TResult> mCompleteListener;

  public MSQAMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener) {
    mController = controller;
    mCompleteListener = completeListener;
  }

  @Override
  public void onComplete(@Nullable TResult tResult, @Nullable MSQAException error) {
    if (mCompleteListener != null) mCompleteListener.onComplete(tResult, error);
    if (tResult != null) {
      mController.getEvent().setMessage(MSQAMetricMessage.SUCCESS);
    } else if (error instanceof MSQACancelException) {
      mController.getEvent().setMessage(MSQAMetricMessage.CANCELED).setComments(error.getMessage());
    } else {
      mController
          .getEvent()
          .setMessage(MSQAMetricMessage.FAILURE)
          .setComments(error != null ? error.getMessage() : null);
    }
    mController.postMetric();
  }
}
