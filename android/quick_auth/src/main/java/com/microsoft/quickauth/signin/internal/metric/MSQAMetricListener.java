package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.error.MSQANoScopeException;

public class MSQAMetricListener<TResult> implements OnCompleteListener<TResult> {
  protected final @NonNull MSQAMetricController mController;
  protected final @Nullable OnCompleteListener<TResult> mCompleteListener;

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
    } else if (error instanceof MSQANoScopeException) {
      mController
          .getEvent()
          .setMessage(MSQAMetricMessage.NO_SCOPES)
          .setComments(error.getMessage());
    } else if (error != null && error.getErrorCode().equals(MSQAErrorString.NO_CURRENT_ACCOUNT)) {
      mController
          .getEvent()
          .setMessage(MSQAMetricMessage.NO_ACCOUNT_PRESENT)
          .setComments(error.getMessage());
    } else {
      mController
          .getEvent()
          .setMessage(MSQAMetricMessage.FAILURE)
          .setComments(error != null ? error.getMessage() : null);
    }
    mController.postMetric();
  }
}
