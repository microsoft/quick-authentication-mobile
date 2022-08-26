package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Schedulers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class SignInTask implements Function<ISignInClientApplication, Task<MSQAAccountInfo>> {

    private @NonNull
    final Activity mActivity;
    private @NonNull
    final List<String> mScopes;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = SignInTask.class.getSimpleName();

    public SignInTask(@NonNull final Activity activity, @NonNull List<String> scopes,
                      @NonNull final MSQATrackerUtil tracker) {
        mActivity = activity;
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> apply(@NonNull final ISignInClientApplication iSignInClientApplication) throws Exception {
        return Task.create(new Task.OnSubscribe<MSQAAccountInfo>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                final Scheduler scheduler = Schedulers.io();
                mTracker.track(TAG, "start request msal sign in api");
                iSignInClientApplication.signIn(mActivity, null, mScopes,
                        new AuthenticationCallback() {

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request msal sign in success");
                                        MSQAAccountInfo account =
                                                MSQAAccountInfo.getAccount(authenticationResult);
                                        consumer.onSuccess(account);
                                    }
                                });
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG,
                                                "request msal sign in error:" + exception.getMessage());
                                        consumer.onError(exception);
                                    }
                                });
                            }

                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request msal sign in cancel");
                                        consumer.onCancel();
                                    }
                                });
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
