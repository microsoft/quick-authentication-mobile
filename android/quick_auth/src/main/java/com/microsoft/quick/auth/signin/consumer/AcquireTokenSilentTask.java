package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quick.auth.signin.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.error.MSQAUiRequiredError;
import com.microsoft.quick.auth.signin.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Switchers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

public class AcquireTokenSilentTask implements Convert<IClientApplication,
        Task<TokenResult>> {

    private @NonNull
    final String[] mScopes;
    private @NonNull
    final MSQATracker mTracker;
    private static final String TAG = "AcquireTokenSilentTask";

    public AcquireTokenSilentTask(@NonNull final String[] scopes,
                                  @NonNull final MSQATracker tracker) {
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public Task<TokenResult> convert(@NonNull final IClientApplication clientApplication) throws Exception {
        return Task.create(new Task.ConsumerHolder<TokenResult>() {
            @Override
            public void start(@NonNull Consumer<? super TokenResult> consumer) {
                try {
                    mTracker.track(TAG, "start request MSAL api acquireTokenSilent");
                    IAccount iAccount = clientApplication.getCurrentAccount();
                    if (iAccount == null)
                        throw new MSQASignInError(MSQAErrorString.NO_CURRENT_ACCOUNT,
                                MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                    IAuthenticationResult result = clientApplication.acquireTokenSilent(iAccount,
                            mScopes);
                    consumer.onSuccess(new MSQASignInTokenResult(result));
                } catch (Exception exception) {
                    Exception silentException = exception;
                    if (silentException instanceof MsalUiRequiredException) {
                        mTracker.track(TAG, "token silent error instanceof MsalUiRequiredException, " +
                                "will return wrap error");
                        silentException =
                                new MSQAUiRequiredError(((MsalUiRequiredException) exception).getErrorCode(),
                                        exception.getMessage());
                    }
                    consumer.onError(silentException);
                }
            }
        })
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain())
                .nextTaskSchedulerOn(Switchers.mainThread());
    }
}
