package com.microsoft.quick.auth.signin.internal.consumer;

import android.util.Pair;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quick.auth.signin.TokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.error.MSQAUiRequiredException;
import com.microsoft.quick.auth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class AcquireTokenSilentTask
    implements MSQATaskFunction<Pair<IClientApplication, IAccount>, MSQATask<TokenResult>> {

  private @NonNull final String[] mScopes;
  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireTokenSilentTask";

  public AcquireTokenSilentTask(
      @NonNull final String[] scopes, @NonNull final MSQATracker tracker) {
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<TokenResult> apply(@NonNull final Pair<IClientApplication, IAccount> pair) {
    return MSQATask.create(
        new MSQATask.ConsumerHolder<TokenResult>() {
          @Override
          public void start(@NonNull final MSQAConsumer<? super TokenResult> consumer) {
            mTracker.track(
                TAG, LogLevel.VERBOSE, "start request MSAL api acquireTokenSilent", null);
            IClientApplication clientApplication = pair.first;
            IAccount iAccount = pair.second;
            // If no signed account, return error.
            if (iAccount == null) {
              consumer.onError(
                  new MSQASignInException(
                      MSQAErrorString.NO_CURRENT_ACCOUNT,
                      MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
              return;
            }
            clientApplication.acquireTokenSilentAsync(
                iAccount,
                mScopes,
                new SilentAuthenticationCallback() {
                  @Override
                  public void onSuccess(IAuthenticationResult authenticationResult) {
                    consumer.onSuccess(new MSQATokenResultInternal(authenticationResult));
                  }

                  @Override
                  public void onError(MsalException exception) {
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
                              exception.getErrorCode(), exception.getMessage());
                    }
                    consumer.onError(silentException);
                  }
                });
          }
        });
  }
}
