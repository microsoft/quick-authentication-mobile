package com.microsoft.quick.auth.signin.internal.consumer;

import androidx.annotation.NonNull;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quick.auth.signin.TokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.error.MSQAUiRequiredException;
import com.microsoft.quick.auth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.MSQASwitchers;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class AcquireTokenSilentTask
    implements MSQATaskFunction<IClientApplication, MSQATask<TokenResult>> {

  private @NonNull final String[] mScopes;
  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireTokenSilentTask";

  public AcquireTokenSilentTask(
      @NonNull final String[] scopes, @NonNull final MSQATracker tracker) {
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<TokenResult> apply(@NonNull final IClientApplication clientApplication) {
    return MSQATask.create(
            new MSQATask.ConsumerHolder<TokenResult>() {
              @Override
              public void start(@NonNull MSQAConsumer<? super TokenResult> consumer) {
                try {
                  mTracker.track(
                      TAG, LogLevel.VERBOSE, "start request MSAL api acquireTokenSilent", null);
                  IAccount iAccount = clientApplication.getCurrentAccount();
                  if (iAccount == null)
                    throw new MSQASignInException(
                        MSQAErrorString.NO_CURRENT_ACCOUNT,
                        MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                  IAuthenticationResult result =
                      clientApplication.acquireTokenSilent(iAccount, mScopes);
                  consumer.onSuccess(new MSQATokenResultInternal(result));
                } catch (Exception exception) {
                  Exception silentException = exception;
                  // wrapper silent MSAL UI thread and expose new thread for developers
                  if (silentException instanceof MsalUiRequiredException) {
                    mTracker.track(
                        TAG,
                        LogLevel.ERROR,
                        "token silent error instanceof MsalUiRequiredException will return wrap error",
                        null);
                    silentException =
                        new MSQAUiRequiredException(
                            ((MsalUiRequiredException) exception).getErrorCode(),
                            exception.getMessage());
                  }
                  consumer.onError(silentException);
                }
              }
            })
        .upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain())
        .downStreamSchedulerOn(MSQASwitchers.mainThread());
  }
}
