package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.ThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Switchers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class AcquireCurrentTokenTask implements Convert<ISignInClientApplication,
        Task<MSQAAccountInfo>> {

    private final @NonNull
    Activity mActivity;
    private static final String TAG = AcquireCurrentTokenTask.class.getSimpleName();
    private final boolean mErrorRetry;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private @NonNull
    final String[] mScopes;

    public AcquireCurrentTokenTask(@NonNull final Activity activity, final boolean errorRetry,
                                   @NonNull final String[] scopes,
                                   @NonNull final MSQATrackerUtil tracker) {
        mTracker = tracker;
        mScopes = scopes;
        mActivity = activity;
        mErrorRetry = errorRetry;
    }

    @Override
    public Task<MSQAAccountInfo> convert(@NonNull final ISignInClientApplication clientApplication) throws Exception {
        final IAccount iAccount = clientApplication.getCurrentAccount();
        if (iAccount == null) {
            mTracker.track(TAG,
                    "get current account error:" + MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
            throw new MSQASignInException(MSQAErrorString.NO_CURRENT_ACCOUNT,
                    MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
        }
        return Task.create(new Task.ConsumerHolder<MSQAAccountInfo>() {
            @Override
            public void start(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                final ThreadSwitcher scheduler = Switchers.io();
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
                    MSQALogger.getInstance().error(TAG, "acquire token silent catch an error, " +
                            "will start acquire " +
                            "token", exception);
                }
                mTracker.track(TAG, "request MSAL acquireToken api");
                clientApplication.acquireToken(mActivity, iAccount, mScopes,
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
                                MSQALogger.getInstance().error(TAG, "acquire token catch an error" +
                                                " acquire token",
                                        exception);
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToThreadSwitcher.directToIOWhenCreateInMain());
    }
}
