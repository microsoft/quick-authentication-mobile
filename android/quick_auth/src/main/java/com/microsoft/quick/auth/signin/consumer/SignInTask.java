package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

public class SignInTask implements Convert<IClientApplication, Task<MSQAAccountInfo>> {

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
  public Task<MSQAAccountInfo> convert(@NonNull final IClientApplication iClientApplication)
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
            new Convert<Pair<Boolean, IAccount>, Task<MSQAAccountInfo>>() {
              @Override
              public Task<MSQAAccountInfo> convert(
                  @NonNull Pair<Boolean, IAccount> booleanIAccountPair) {
                if (booleanIAccountPair.first) {
                  return Task.with(iClientApplication)
                      .then(new AcquireCurrentTokenTask(mActivity, true, mScopes, mTracker));
                } else {
                  return Task.create(
                      new Task.ConsumerHolder<MSQAAccountInfo>() {
                        @Override
                        public void start(
                            @NonNull final Consumer<? super MSQAAccountInfo> consumer) {
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
                                  MSQAAccountInfo account =
                                      MSQAAccountInfo.getAccount(authenticationResult);
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
