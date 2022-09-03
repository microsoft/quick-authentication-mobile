package com.microsoft.quick.auth.signin.internal.consumer;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class AcquireCurrentTokenTask
    implements MSQATaskFunction<IClientApplication, MSQATask<MSQAAccountInfoInternal>> {

  private final @NonNull Activity mActivity;
  private static final String TAG = "AcquireCurrentTokenTask";
  private final boolean mErrorRetry;
  private @NonNull final MSQATracker mTracker;
  private @NonNull final String[] mScopes;

  public AcquireCurrentTokenTask(
      @NonNull final Activity activity,
      final boolean errorRetry,
      @NonNull final String[] scopes,
      @NonNull final MSQATracker tracker) {
    mTracker = tracker;
    mScopes = scopes;
    mActivity = activity;
    mErrorRetry = errorRetry;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final IClientApplication clientApplication) {
    return MSQATask.create(
            new MSQATask.ConsumerHolder<MSQAAccountInfoInternal>() {
              @Override
              public void start(
                  @NonNull final MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
                mTracker.track(TAG, LogLevel.VERBOSE, "start get current token task", null);
                // Get silent token first, if error will request token with acquireToken api
                try {
                  final IAccount iAccount = clientApplication.getCurrentAccount();
                  if (iAccount == null) {
                    mTracker.track(
                        TAG, LogLevel.ERROR, "get current account error no account signed", null);
                    throw new MSQASignInException(
                        MSQAErrorString.NO_CURRENT_ACCOUNT,
                        MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                  }
                  mTracker.track(
                      TAG, LogLevel.VERBOSE, "start request MSAL acquireTokenSilent api", null);
                  IAuthenticationResult authenticationResult =
                      clientApplication.acquireTokenSilent(iAccount, mScopes);
                  if (authenticationResult != null) {
                    mTracker.track(
                        TAG, LogLevel.VERBOSE, "request MSAL acquireTokenSilent api success", null);
                    consumer.onSuccess(MSQAAccountInfoInternal.getAccount(authenticationResult));
                    return;
                  }
                } catch (final Exception exception) {
                  mTracker.track(
                      TAG, LogLevel.ERROR, "request MSAL acquireTokenSilent api error", exception);

                  if (!mErrorRetry) {
                    consumer.onError(exception);
                    return;
                  }
                }
                mTracker.track(TAG, LogLevel.VERBOSE, "request MSAL acquireToken api", null);
                clientApplication.acquireToken(
                    mActivity,
                    mScopes,
                    new AuthenticationCallback() {
                      @Override
                      public void onCancel() {
                        mTracker.track(
                            TAG, LogLevel.VERBOSE, "request MSAL acquireToken cancel", null);
                        consumer.onCancel();
                      }

                      @Override
                      public void onSuccess(final IAuthenticationResult authenticationResult) {
                        mTracker.track(
                            TAG, LogLevel.VERBOSE, "request MSAL acquireToken success", null);
                        consumer.onSuccess(
                            MSQAAccountInfoInternal.getAccount(authenticationResult));
                      }

                      @Override
                      public void onError(final MsalException exception) {
                        mTracker.track(
                            TAG, LogLevel.ERROR, "request MSAL acquireToken error", exception);
                        consumer.onError(exception);
                      }
                    });
              }
            })
        .upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain());
  }
}
