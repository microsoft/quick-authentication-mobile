package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQAException;

public class MSQAMetricListener<TResult> implements OnCompleteListener<TResult> {
  protected final @NonNull MSQAMetricController mController;
  protected final @Nullable OnCompleteListener<TResult> mCompleteListener;
  private final @NonNull MSQAErrorToMessageMapper mMessageMapper;

  public MSQAMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener) {
    this(controller, completeListener, new MSQAErrorToMessageMapper());
  }

  public MSQAMetricListener(
      @NonNull MSQAMetricController controller,
      @Nullable OnCompleteListener<TResult> completeListener,
      @NonNull MSQAErrorToMessageMapper messageMapper) {
    mController = controller;
    mCompleteListener = completeListener;
    mMessageMapper = messageMapper;
  }

  @Override
  public void onComplete(@Nullable TResult tResult, @Nullable MSQAException error) {
    if (mCompleteListener != null) mCompleteListener.onComplete(tResult, error);
    mMessageMapper.map(mController.getEvent(), tResult, error);
    mController.postMetric();
  }
}
