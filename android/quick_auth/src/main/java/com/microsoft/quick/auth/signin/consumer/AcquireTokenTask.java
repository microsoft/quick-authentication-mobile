package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.ThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Switchers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class AcquireTokenTask implements Convert<ISignInClientApplication,
        Task<TokenResult>> {
    private @NonNull
    final Activity mActivity;
    private @NonNull
    final String[] mScopes;
    private @Nullable
    final String mLoginHint;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = AcquireTokenTask.class.getSimpleName();

    public AcquireTokenTask(@NonNull final Activity activity, @NonNull final String[] scopes,
                            @Nullable final String loginHint,
                            @NonNull final MSQATrackerUtil tracker) {
        mActivity = activity;
        mScopes = scopes;
        mLoginHint = loginHint;
        mTracker = tracker;
    }

    @Override
    public Task<TokenResult> convert(@NonNull final ISignInClientApplication iSignInClientApplication) throws Exception {
        final ThreadSwitcher scheduler = Switchers.io();
        return Task.create(new Task.ConsumerHolder<TokenResult>() {
            @Override
            public void start(@NonNull final Consumer<? super TokenResult> consumer) {
                mTracker.track(TAG, "start request MSAL acquireToken api");
                IAccount iAccount = null;
                try {
                    iAccount = iSignInClientApplication.getCurrentAccount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // If no signed account, return error.
                if (iAccount == null) {
                    consumer.onError(new MSQASignInException(MSQAErrorString.NO_CURRENT_ACCOUNT,
                            MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
                    return;
                }
                iSignInClientApplication.acquireToken(mActivity, iAccount, mScopes,
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken api cancel");
                                        consumer.onCancel();
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken api " +
                                                "success");
                                        consumer.onSuccess(new MSQASignInTokenResult(authenticationResult));
                                    }
                                });
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken api " +
                                                "error:" + exception);
                                        consumer.onError(exception);
                                    }
                                });
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToThreadSwitcher.directToIOWhenCreateInMain());
    }
}
