package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.MSQASignInClientHelper;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.signapplication.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.logger.LogUtil;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class AccountSignedErrorConsumer implements Function<Exception, Task<MSQAAccountInfo>> {
    private static final String TAG = AccountSignedErrorConsumer.class.getSimpleName();
    private final @NonNull
    IAccountClientHolder mSignClient;
    private final @NonNull
    Activity mActivity;
    private final @NonNull
    MSQATrackerUtil mTracker;

    public AccountSignedErrorConsumer(final @NonNull Activity activity,
                                      final @NonNull IAccountClientHolder signClient,
                                      @NonNull MSQATrackerUtil tracker) {
        mActivity = activity;
        mSignClient = signClient;
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> apply(@NonNull final Exception exception) throws Exception {
        return Task.create(new Task.OnSubscribe<MSQAAccountInfo>() {

            @Override
            public void subscribe(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                final Scheduler scheduler = TaskExecutorUtil.IO();
                if (exception instanceof MsalException && MsalClientException.INVALID_PARAMETER.equals(((MsalException) exception).getErrorCode())) {
                    LogUtil.error(TAG, "sign error with account has signed add will start" +
                            " get sign account");
                    mTracker.track(TAG, "sign error with account has signed add will start get " +
                            "sign account");
                    MSQASignInClientHelper.getCurrentSignInAccount(mActivity, true,
                            new OnCompleteListener<MSQAAccountInfo>() {
                                @Override
                                public void onComplete(@Nullable final MSQAAccountInfo accountInfo,
                                                       @Nullable final Exception error) {
                                    scheduler.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (accountInfo != null) {
                                                mTracker.track(TAG, "error and retry get account " +
                                                        "success");
                                                consumer.onSuccess(accountInfo);
                                            } else {
                                                mTracker.track(TAG, "error and retry get account " +
                                                        "error:" + error);
                                                consumer.onError(error);
                                            }
                                        }
                                    });
                                }
                            }, mTracker);
                } else {
                    consumer.onError(exception);
                }
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
