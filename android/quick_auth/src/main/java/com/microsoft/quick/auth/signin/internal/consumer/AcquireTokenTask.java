package com.microsoft.quick.auth.signin.internal.consumer;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.MSQATokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.MSQASwitchers;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class AcquireTokenTask
    implements MSQATaskFunction<IClientApplication, MSQATask<MSQATokenResult>> {
  private @NonNull final Activity mActivity;
  private @NonNull final String[] mScopes;
  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireTokenTask";

  public AcquireTokenTask(
      @NonNull final Activity activity,
      @NonNull final String[] scopes,
      @NonNull final MSQATracker tracker) {
    mActivity = activity;
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQATokenResult> apply(@NonNull final IClientApplication iClientApplication) {
    return MSQATask.create(
            new MSQATask.ConsumerHolder<MSQATokenResult>() {
              @Override
              public void start(@NonNull final MSQAConsumer<? super MSQATokenResult> consumer) {
                mTracker.track(TAG, LogLevel.VERBOSE, "start request MSAL acquireToken api", null);
                IAccount iAccount = null;
                try {
                  iAccount = iClientApplication.getCurrentAccount();
                } catch (Exception e) {
                  mTracker.track(TAG, LogLevel.ERROR, "current account get error", e);
                }
                // If no signed account, return error.
                if (iAccount == null) {
                  consumer.onError(
                      new MSQASignInException(
                          MSQAErrorString.NO_CURRENT_ACCOUNT,
                          MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
                  return;
                }
                iClientApplication.acquireToken(
                    mActivity,
                    mScopes,
                    new AuthenticationCallback() {
                      @Override
                      public void onCancel() {
                        mTracker.track(
                            TAG, LogLevel.VERBOSE, "request MSAL acquireToken api cancel", null);
                        consumer.onCancel();
                      }

                      @Override
                      public void onSuccess(final IAuthenticationResult authenticationResult) {
                        mTracker.track(
                            TAG, LogLevel.VERBOSE, "request MSAL acquireToken api success", null);
                        consumer.onSuccess(new MSQATokenResultInternal(authenticationResult));
                      }

                      @Override
                      public void onError(final MsalException exception) {
                        mTracker.track(
                            TAG, LogLevel.ERROR, "request MSAL acquireToken api error", exception);
                        consumer.onError(exception);
                      }
                    });
              }
            })
        .upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain())
        .downStreamSchedulerOn(MSQASwitchers.mainThread());
  }
}
