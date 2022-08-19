package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;
import com.microsoft.quick.auth.signin.logger.LogUtil;

public class CurrentTokenRequestConsumer implements Function<IAccount, Task<MQAAccountInfo>> {

    private final @NonNull
    IAccountClientHolder mClientHolder;
    private final @NonNull
    Activity mActivity;
    private static final String TAG = CurrentTokenRequestConsumer.class.getSimpleName();
    private final boolean mErrorRetry;

    public CurrentTokenRequestConsumer(@NonNull Activity activity, boolean errorRetry,
                                       @NonNull IAccountClientHolder clientHolder) {
        mClientHolder = clientHolder;
        mActivity = activity;
        mErrorRetry = errorRetry;
    }

    @Override
    public Task<MQAAccountInfo> apply(@NonNull final IAccount iAccount) throws Exception {
        final IAccountClientApplication clientApplication = mClientHolder.getClientApplication();
        return Task.create(new Task.OnSubscribe<MQAAccountInfo>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super MQAAccountInfo> consumer) {
                final Scheduler scheduler = TaskExecutorUtil.IO();
                // Get silent token first, if error will request token with acquireToken api
                try {
                    IAuthenticationResult authenticationResult =
                            clientApplication.acquireTokenSilent(iAccount,
                                    mClientHolder.getOptions().getScopes());
                    if (authenticationResult != null) {
                        consumer.onSuccess(MQAAccountInfo.getAccount(authenticationResult));
                        return;
                    }
                } catch (final Exception exception) {
                    if (!mErrorRetry) {
                        scheduler.schedule(new Runnable() {
                            @Override
                            public void run() {
                                consumer.onError(exception);
                            }
                        });
                        return;
                    }
                    LogUtil.error(TAG, "acquire token silent catch an error, will start acquire " +
                            "token", exception);
                }
                clientApplication.acquireToken(mActivity, mClientHolder.getOptions().getScopes(),
                        mClientHolder.getOptions().getLoginHint(),
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onCancel();
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onSuccess(MQAAccountInfo.getAccount(authenticationResult));
                                    }
                                });
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onError(exception);
                                    }
                                });
                                LogUtil.error(TAG, "acquire token catch an error acquire token",
                                        exception);
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
