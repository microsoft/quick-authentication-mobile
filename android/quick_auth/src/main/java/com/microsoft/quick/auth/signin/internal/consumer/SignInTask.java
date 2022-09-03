package com.microsoft.quick.auth.signin.internal.consumer;

import android.app.Activity;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.internal.entity.MSQAInnerAccountInfo;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.Consumer;
import com.microsoft.quick.auth.signin.internal.task.Convert;
import com.microsoft.quick.auth.signin.internal.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.Task;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;

public class SignInTask implements Convert<IClientApplication, Task<MSQAInnerAccountInfo>> {

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
  public Task<MSQAInnerAccountInfo> convert(@NonNull final IClientApplication iClientApplication)
      throws Exception {
    return Task.create(
            new Task.ConsumerHolder<Pair<Boolean, IAccount>>() {
              @Override
              public void start(@NonNull Consumer<? super Pair<Boolean, IAccount>> consumer) {
                IAccount iAccount = null;
                try {
                  mTracker.track(TAG, LogLevel.VERBOSE, "start sign in task", null);
                  iAccount = iClientApplication.getCurrentAccount();
                } catch (Exception e) {
                  mTracker.track(TAG, LogLevel.ERROR, "current account get error", e);
                }
                consumer.onSuccess(new Pair<>(iAccount != null, iAccount));
              }
            })
        .then(
            new Convert<Pair<Boolean, IAccount>, Task<MSQAInnerAccountInfo>>() {
              @Override
              public Task<MSQAInnerAccountInfo> convert(
                  @NonNull Pair<Boolean, IAccount> booleanIAccountPair) {
                if (booleanIAccountPair.first) {
                  return Task.with(iClientApplication)
                      .then(new AcquireCurrentTokenTask(mActivity, true, mScopes, mTracker));
                } else {
                  return Task.create(
                      new Task.ConsumerHolder<MSQAInnerAccountInfo>() {
                        @Override
                        public void start(
                            @NonNull final Consumer<? super MSQAInnerAccountInfo> consumer) {
                          mTracker.track(
                              TAG, LogLevel.VERBOSE, "start request msal sign in api", null);
                          iClientApplication.signIn(
                              mActivity,
                              null,
                              mScopes,
                              new AuthenticationCallback() {

                                @Override
                                public void onSuccess(
                                    final IAuthenticationResult authenticationResult) {
                                  mTracker.track(
                                      TAG,
                                      LogLevel.VERBOSE,
                                      "request msal sign in " + "success",
                                      null);
                                  MSQAInnerAccountInfo account =
                                      MSQAInnerAccountInfo.getAccount(authenticationResult);
                                  consumer.onSuccess(account);
                                }

                                @Override
                                public void onError(final MsalException exception) {
                                  mTracker.track(
                                      TAG,
                                      LogLevel.VERBOSE,
                                      "request msal sign in error",
                                      exception);

                                  consumer.onError(exception);
                                }

                                @Override
                                public void onCancel() {
                                  mTracker.track(
                                      TAG,
                                      LogLevel.VERBOSE,
                                      "request msal sign in " + "cancel",
                                      null);
                                  consumer.onCancel();
                                }
                              });
                        }
                      });
                }
              }
            })
        .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
  }
}
