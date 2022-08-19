package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.MQAInnerSignInClient;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.logger.LogUtil;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class AccountSignedErrorConsumer implements Function<Exception, Task<MQAAccountInfo>> {
    private static final String TAG = AccountSignedErrorConsumer.class.getSimpleName();
    private final @NonNull
    IAccountClientHolder mSignClient;
    private final @NonNull
    Activity mActivity;

    public AccountSignedErrorConsumer(final @NonNull Activity activity,
                                      final @NonNull IAccountClientHolder signClient) {
        mActivity = activity;
        mSignClient = signClient;
    }

    @Override
    public Task<MQAAccountInfo> apply(@NonNull final Exception exception) throws Exception {
        return Task.create(new Task.OnSubscribe<MQAAccountInfo>() {

            @Override
            public void subscribe(@NonNull final Consumer<? super MQAAccountInfo> consumer) {
                final Scheduler scheduler = TaskExecutorUtil.IO();
                if (exception instanceof MsalException && MsalClientException.INVALID_PARAMETER.equals(((MsalException) exception).getErrorCode())) {
                    LogUtil.error(TAG, "sign error with account has signed add will start" +
                            " get sign account");
                    MQAInnerSignInClient.getCurrentSignInAccount(mActivity, true,
                            new OnCompleteListener<MQAAccountInfo>() {
                                @Override
                                public void onComplete(@Nullable final MQAAccountInfo accountInfo,
                                                       @Nullable final Exception error) {
                                    scheduler.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (accountInfo != null) {
                                                consumer.onSuccess(accountInfo);
                                            } else {
                                                consumer.onError(error);
                                            }
                                        }
                                    });
                                }
                            });
                } else {
                    consumer.onError(exception);
                }
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
