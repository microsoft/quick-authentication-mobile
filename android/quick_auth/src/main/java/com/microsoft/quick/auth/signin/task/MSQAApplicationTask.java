package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.signapplication.IAccountClientApplication;
import com.microsoft.quick.auth.signin.signapplication.IAccountClientHolder;

public class MSQAApplicationTask implements Task.OnSubscribe<IAccountClientApplication> {
    private final @NonNull
    IAccountClientHolder mClientHolder;
    private final @NonNull
    MSQATrackerUtil mTrack;
    private static final String TAG = MSQAApplicationTask.class.getSimpleName();

    public MSQAApplicationTask(@NonNull IAccountClientHolder clientHolder,
                               @NonNull MSQATrackerUtil tracker) {
        mClientHolder = clientHolder;
        mTrack = tracker;
    }

    @Override
    public void subscribe(@NonNull Consumer<? super IAccountClientApplication> observer) {
        try {
            mTrack.track(TAG, "start get application");
            observer.onSuccess(mClientHolder.getClientApplication());
            mTrack.track(TAG, "get application success");
        } catch (Exception e) {
            observer.onError(e);
            mTrack.track(TAG, "get application error:" + e.getMessage());
        }
    }

    public static Task<IAccountClientApplication> getApplicationObservable(@NonNull IAccountClientHolder clientHolder,
                                                                           @NonNull MSQATrackerUtil tracker) {
        return Task.create(new MSQAApplicationTask(clientHolder, tracker))
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain())
                .nextConsumerOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
