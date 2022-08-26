package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class SignedErrorRetryTask implements Function<Exception, Task<MSQAAccountInfo>> {
    private static final String TAG = SignedErrorRetryTask.class.getSimpleName();
    private @NonNull
    final ISignInClientHolder mSignClient;
    private @NonNull
    final Activity mActivity;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private @NonNull
    final List<String> mScopes;

    public SignedErrorRetryTask(@NonNull final Activity activity,
                                @NonNull final ISignInClientHolder signClient,
                                @NonNull final List<String> scopes,
                                @NonNull final MSQATrackerUtil tracker) {
        mActivity = activity;
        mSignClient = signClient;
        mTracker = tracker;
        mScopes = scopes;
    }

    @Override
    public Task<MSQAAccountInfo> apply(@NonNull final Exception exception) throws Exception {
        if (exception instanceof MsalException && MsalClientException.INVALID_PARAMETER.equals(((MsalException) exception).getErrorCode())) {
            final ISignInClientApplication clientApplication = mSignClient.getClientApplication();
            return Task.create(new Task.OnSubscribe<IAccount>() {

                @Override
                public void subscribe(@NonNull Consumer<? super IAccount> consumer) {
                    MSQALogger.getInstance().error(TAG, "sign error with account has signed add will start" +
                            " get current account", null);
                    mTracker.track(TAG, "sign error with account has signed add will start get " +
                            "current account");
                    try {
                        consumer.onSuccess(clientApplication.getCurrentAccount());
                    } catch (Exception e) {
                        consumer.onError(e);
                    }
                }
            })
                    .flatMap(new AcquireCurrentTokenTask(mActivity, true, mSignClient, mScopes, null, mTracker))
                    .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
        } else {
            return Task.create(new Task.OnSubscribe<MSQAAccountInfo>() {
                @Override
                public void subscribe(@NonNull Consumer<? super MSQAAccountInfo> consumer) {
                    consumer.onError(exception);
                }
            });
        }
    }
}
