package com.microsoft.quick.auth.signin.internal.consumer;

import androidx.annotation.NonNull;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quick.auth.signin.internal.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.MSQATokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.error.MSQAUiRequiredError;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.Consumer;
import com.microsoft.quick.auth.signin.internal.task.Convert;
import com.microsoft.quick.auth.signin.internal.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.Switchers;
import com.microsoft.quick.auth.signin.internal.task.Task;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;

public class AcquireTokenSilentTask implements Convert<IClientApplication, Task<MSQATokenResult>> {

  private @NonNull final String[] mScopes;
  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireTokenSilentTask";

  public AcquireTokenSilentTask(
      @NonNull final String[] scopes, @NonNull final MSQATracker tracker) {
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public Task<MSQATokenResult> convert(@NonNull final IClientApplication clientApplication)
      throws Exception {
    return Task.create(
            new Task.ConsumerHolder<MSQATokenResult>() {
              @Override
              public void start(@NonNull Consumer<? super MSQATokenResult> consumer) {
                try {
                  mTracker.track(
                      TAG, LogLevel.VERBOSE, "start request MSAL api acquireTokenSilent", null);
                  IAccount iAccount = clientApplication.getCurrentAccount();
                  if (iAccount == null)
                    throw new MSQASignInError(
                        MSQAErrorString.NO_CURRENT_ACCOUNT,
                        MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                  IAuthenticationResult result =
                      clientApplication.acquireTokenSilent(iAccount, mScopes);
                  consumer.onSuccess(new MSQASignInTokenResult(result));
                } catch (Exception exception) {
                  Exception silentException = exception;
                  if (silentException instanceof MsalUiRequiredException) {
                    mTracker.track(
                        TAG,
                        LogLevel.ERROR,
                        "token silent error instanceof MsalUiRequiredException "
                            + "will return wrap error",
                        null);
                    silentException =
                        new MSQAUiRequiredError(
                            ((MsalUiRequiredException) exception).getErrorCode(),
                            exception.getMessage());
                  }
                  consumer.onError(silentException);
                }
              }
            })
        .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain())
        .nextTaskSchedulerOn(Switchers.mainThread());
  }
}
