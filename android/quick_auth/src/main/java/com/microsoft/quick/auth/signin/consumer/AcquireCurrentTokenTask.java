package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Schedulers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class AcquireCurrentTokenTask implements Function<IAccount, Task<MSQAAccountInfo>> {

    private final @NonNull
    ISignInClientHolder mClientHolder;
    private final @NonNull
    Activity mActivity;
    private static final String TAG = AcquireCurrentTokenTask.class.getSimpleName();
    private final boolean mErrorRetry;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private @NonNull
    final List<String> mScopes;
    private @Nullable
    final String mLoginHint;

    public AcquireCurrentTokenTask(@NonNull final Activity activity, final boolean errorRetry,
                                   @NonNull final ISignInClientHolder clientHolder,
                                   @NonNull final List<String> scopes,
                                   @Nullable final String loginHint,
                                   @NonNull final MSQATrackerUtil tracker) {
        mTracker = tracker;
        mLoginHint = loginHint;
        mScopes = scopes;
        mClientHolder = clientHolder;
        mActivity = activity;
        mErrorRetry = errorRetry;
    }

    @Override
    public Task<MSQAAccountInfo> apply(@NonNull final IAccount iAccount) throws Exception {
        final ISignInClientApplication clientApplication = mClientHolder.getClientApplication();
        return Task.create(new Task.OnSubscribe<MSQAAccountInfo>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                final Scheduler scheduler = Schedulers.io();
                // Get silent token first, if error will request token with acquireToken api
                try {
                    mTracker.track(TAG, "start request MSAL acquireTokenSilent api");
                    IAuthenticationResult authenticationResult =
                            clientApplication.acquireTokenSilent(iAccount, mScopes);
                    if (authenticationResult != null) {
                        mTracker.track(TAG, "request MSAL acquireTokenSilent api success");
                        consumer.onSuccess(MSQAAccountInfo.getAccount(authenticationResult));
                        return;
                    }
                } catch (final Exception exception) {
                    if (!mErrorRetry) {
                        mTracker.track(TAG,
                                "request MSAL acquireTokenSilent api error:" + exception.getMessage());
                        scheduler.schedule(new Runnable() {
                            @Override
                            public void run() {
                                consumer.onError(exception);
                            }
                        });
                        return;
                    }
                    MSQALogger.getInstance().error(TAG, "acquire token silent catch an error, will start acquire " +
                            "token", exception);
                }
                mTracker.track(TAG, "request MSAL acquireToken api");
                clientApplication.acquireToken(mActivity, iAccount, mScopes, mLoginHint,
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken cancel");
                                        consumer.onCancel();
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken success");
                                        consumer.onSuccess(MSQAAccountInfo.getAccount(authenticationResult));
                                    }
                                });
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG,
                                                "request MSAL acquireToken error:" + exception.getMessage());
                                        consumer.onError(exception);
                                    }
                                });
                                MSQALogger.getInstance().error(TAG, "acquire token catch an error acquire token",
                                        exception);
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
