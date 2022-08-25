package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.signapplication.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class AcquireTokenConsumer implements Function<IAccountClientApplication,
        Task<ITokenResult>> {
    private final @NonNull
    Activity mActivity;
    private @NonNull
    final String[] mScopes;
    private @Nullable
    final String mLoginHint;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = AcquireTokenConsumer.class.getSimpleName();

    public AcquireTokenConsumer(final @NonNull Activity activity, @NonNull final String[] scopes,
                                @Nullable final String loginHint,
                                @NonNull final MSQATrackerUtil tracker) {
        mActivity = activity;
        mScopes = scopes;
        mLoginHint = loginHint;
        mTracker = tracker;
    }

    @Override
    public Task<ITokenResult> apply(@NonNull final IAccountClientApplication iAccountClientApplication) throws Exception {
        final Scheduler scheduler = TaskExecutorUtil.IO();
        return Task.create(new Task.OnSubscribe<ITokenResult>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super ITokenResult> consumer) {
                mTracker.track(TAG, "start request MSAL acquireToken api");
                iAccountClientApplication.acquireToken(mActivity, mScopes, mLoginHint,
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
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
