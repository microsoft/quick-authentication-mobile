package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

public class AcquireCurrentTokenTask implements Convert<IClientApplication,
        Task<MSQAAccountInfo>> {

    private final @NonNull
    Activity mActivity;
    private static final String TAG = AcquireCurrentTokenTask.class.getSimpleName();
    private final boolean mErrorRetry;
    private @NonNull
    final MSQATracker mTracker;
    private @NonNull
    final String[] mScopes;

    public AcquireCurrentTokenTask(@NonNull final Activity activity, final boolean errorRetry,
                                   @NonNull final String[] scopes,
                                   @NonNull final MSQATracker tracker) {
        mTracker = tracker;
        mScopes = scopes;
        mActivity = activity;
        mErrorRetry = errorRetry;
    }

    @Override
    public Task<MSQAAccountInfo> convert(@NonNull final IClientApplication clientApplication) throws Exception {
        return Task.create(new Task.ConsumerHolder<MSQAAccountInfo>() {
            @Override
            public void start(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                mTracker.track(TAG, "start get current token task");
                // Get silent token first, if error will request token with acquireToken api
                try {
                    final IAccount iAccount = clientApplication.getCurrentAccount();
                    if (iAccount == null) {
                        mTracker.track(TAG,
                                "get current account error:" + MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                        throw new MSQASignInError(MSQAErrorString.NO_CURRENT_ACCOUNT,
                                MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                    }
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
                                consumer.onError(exception);
                        return;
                    }
                    MSQALogger.getInstance().error(TAG, "acquire token silent catch an error, " +
                            "will start acquire " +
                            "token", exception);
                }
                mTracker.track(TAG, "request MSAL acquireToken api");
                clientApplication.acquireToken(mActivity, mScopes,
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                mTracker.track(TAG, "request MSAL acquireToken cancel");
                                consumer.onCancel();
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                mTracker.track(TAG, "request MSAL acquireToken success");
                                consumer.onSuccess(MSQAAccountInfo.getAccount(authenticationResult));
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                        mTracker.track(TAG,
                                                "request MSAL acquireToken error:" + exception.getMessage());
                                        consumer.onError(exception);
                                MSQALogger.getInstance().error(TAG, "acquire token catch an error" +
                                                " acquire token",
                                        exception);
                            }
                        });
            }
        })
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
    }
}
