package com.microsoft.quick.auth.signin.internal.consumer;

import android.app.Activity;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class SignInTask
    implements MSQATaskFunction<
        Pair<IClientApplication, IAccount>, MSQATask<MSQAAccountInfoInternal>> {

  private @NonNull final Activity mActivity;
  private @NonNull final String[] mScopes;
  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "SignInTask";

  public SignInTask(
      @NonNull final Activity activity,
      @NonNull String[] scopes,
      @NonNull final MSQATracker tracker) {
    mActivity = activity;
    mScopes = scopes;
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final Pair<IClientApplication, IAccount> pair) {
    final IClientApplication clientApplication = pair.first;
    IAccount iAccount = pair.second;
    if (iAccount == null) {
      return MSQATask.create(
          new MSQATask.ConsumerHolder<MSQAAccountInfoInternal>() {
            @Override
            public void start(
                @NonNull final MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
              mTracker.track(TAG, LogLevel.VERBOSE, "start request msal sign in api", null);
              clientApplication.signIn(
                  mActivity,
                  null,
                  mScopes,
                  new AuthenticationCallback() {

                    @Override
                    public void onSuccess(final IAuthenticationResult authenticationResult) {
                      mTracker.track(TAG, LogLevel.VERBOSE, "request msal sign in success", null);
                      MSQAAccountInfoInternal account =
                          MSQAAccountInfoInternal.getAccount(authenticationResult);
                      consumer.onSuccess(account);
                    }

                    @Override
                    public void onError(final MsalException exception) {
                      mTracker.track(
                          TAG, LogLevel.VERBOSE, "request msal sign in error", exception);

                      consumer.onError(exception);
                    }

                    @Override
                    public void onCancel() {
                      mTracker.track(TAG, LogLevel.VERBOSE, "request msal sign in cancel", null);
                      consumer.onCancel();
                    }
                  });
            }
          });
    } else {
      return MSQATask.with(pair)
          .then(new AcquireCurrentTokenTask(mActivity, true, mScopes, mTracker))
          .upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain());
    }
  }
}
