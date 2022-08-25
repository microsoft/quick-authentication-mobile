package com.microsoft.quick.auth.signin;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.tracker.MSQATracker;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Task;

class MSQAApplicationTask implements Task.OnSubscribe<IAccountClientApplication> {
    private final @NonNull
    IAccountClientHolder mClientHolder;
    private final @NonNull
    MSQATracker mMSQATracker;
    private static final String TAG = MSQAApplicationTask.class.getSimpleName();

    public MSQAApplicationTask(@NonNull IAccountClientHolder clientHolder,
                               @NonNull MSQATracker tracker) {
        mClientHolder = clientHolder;
        mMSQATracker = tracker;
    }

    @Override
    public void subscribe(@NonNull Consumer<? super IAccountClientApplication> observer) {
        try {
            mMSQATracker.track(TAG, "start get application");
            observer.onSuccess(mClientHolder.getClientApplication());
            mMSQATracker.track(TAG, "get application success");
        } catch (Exception e) {
            observer.onError(e);
            mMSQATracker.track(TAG, "get application error:" + e.getMessage());
        }
    }

    public static Task<IAccountClientApplication> getApplicationObservable(@NonNull IAccountClientHolder clientHolder,
                                                                           @NonNull MSQATracker tracker) {
        return Task.create(new MSQAApplicationTask(clientHolder, tracker))
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain())
                .nextConsumerOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
