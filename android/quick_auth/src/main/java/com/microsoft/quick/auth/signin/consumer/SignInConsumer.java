package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.entity.MQASignInInnerConfig;
import com.microsoft.quick.auth.signin.signapplication.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class SignInConsumer implements Function<IAccountClientApplication, Task<MSQAAccountInfo>> {

    private final @NonNull
    Activity mActivity;
    private final @NonNull
    MQASignInInnerConfig mOptions;
    private final @NonNull
    MSQATrackerUtil mTracker;
    private static final String TAG = SignInConsumer.class.getSimpleName();

    public SignInConsumer(final @NonNull Activity activity,
                          final @NonNull MQASignInInnerConfig options, @NonNull MSQATrackerUtil tracker) {
        mActivity = activity;
        mOptions = options;
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> apply(@NonNull final IAccountClientApplication iAccountClientApplication) throws Exception {
        return Task.create(new Task.OnSubscribe<MSQAAccountInfo>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                final Scheduler scheduler = TaskExecutorUtil.IO();
                mTracker.track(TAG, "start request msal sign in api");
                iAccountClientApplication.signIn(mActivity, mOptions.getLoginHint(),
                        mOptions.getScopes(),
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
                                        consumer.onError(exception);
                                        mTracker.track(TAG,
                                                "request msal sign in error:" + exception.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onCancel();
                                        mTracker.track(TAG, "request msal sign in cancel");
                                    }
                                });
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
