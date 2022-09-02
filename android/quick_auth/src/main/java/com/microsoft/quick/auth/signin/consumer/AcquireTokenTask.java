package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.Switchers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

public class AcquireTokenTask implements Convert<IClientApplication,
        Task<TokenResult>> {
    private @NonNull
    final Activity mActivity;
    private @NonNull
    final String[] mScopes;
    private @NonNull
    final MSQATracker mTracker;
    private static final String TAG = AcquireTokenTask.class.getSimpleName();

    public AcquireTokenTask(@NonNull final Activity activity, @NonNull final String[] scopes,
                            @NonNull final MSQATracker tracker) {
        mActivity = activity;
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public Task<TokenResult> convert(@NonNull final IClientApplication iClientApplication) throws Exception {
        return Task.create(new Task.ConsumerHolder<TokenResult>() {
            @Override
            public void start(@NonNull final Consumer<? super TokenResult> consumer) {
                mTracker.track(TAG, "start request MSAL acquireToken api");
                IAccount iAccount = null;
                try {
                    iAccount = iClientApplication.getCurrentAccount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // If no signed account, return error.
                if (iAccount == null) {
                    consumer.onError(new MSQASignInError(MSQAErrorString.NO_CURRENT_ACCOUNT,
                            MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
                    return;
                }
                iClientApplication.acquireToken(mActivity, mScopes,
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                mTracker.track(TAG, "request MSAL acquireToken api cancel");
                                consumer.onCancel();
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                mTracker.track(TAG, "request MSAL acquireToken api " +
                                        "success");
                                consumer.onSuccess(new MSQASignInTokenResult(authenticationResult));
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                mTracker.track(TAG, "request MSAL acquireToken api " +
                                        "error:" + exception);
                                consumer.onError(exception);
                            }
                        });
            }
        })
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain())
                .nextTaskSchedulerOn(Switchers.mainThread());
    }
}
