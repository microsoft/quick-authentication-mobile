package com.microsoft.quick.auth.signin;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.tracker.MQATracker;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Task;

class MQAApplicationTask implements Task.OnSubscribe<IAccountClientApplication> {
    private final @NonNull
    IAccountClientHolder mClientHolder;
    private final @NonNull
    MQATracker mMqaTracker;
    private static final String TAG = MQAApplicationTask.class.getSimpleName();

    public MQAApplicationTask(@NonNull IAccountClientHolder clientHolder,
                              @NonNull MQATracker tracker) {
        mClientHolder = clientHolder;
        mMqaTracker = tracker;
    }

    @Override
    public void subscribe(@NonNull Consumer<? super IAccountClientApplication> observer) {
        try {
            mMqaTracker.track(TAG, "start get application");
            observer.onSuccess(mClientHolder.getClientApplication());
            mMqaTracker.track(TAG, "get application success");
        } catch (Exception e) {
            observer.onError(e);
            mMqaTracker.track(TAG, "get application error:" + e.getMessage());
        }
    }

    public static Task<IAccountClientApplication> getApplicationObservable(@NonNull IAccountClientHolder clientHolder,
                                                                           @NonNull MQATracker tracker) {
        return Task.create(new MQAApplicationTask(clientHolder, tracker))
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain())
                .nextConsumerOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
