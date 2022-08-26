package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;

public class AcquireClientApplicationTask implements Task.OnSubscribe<ISignInClientApplication> {
    private @Nullable
    final ISignInClientHolder mClientHolder;
    private @NonNull
    final MSQATrackerUtil mTrack;
    private static final String TAG = AcquireClientApplicationTask.class.getSimpleName();

    public AcquireClientApplicationTask(@Nullable final ISignInClientHolder clientHolder,
                                        @NonNull final MSQATrackerUtil tracker) {
        mClientHolder = clientHolder;
        mTrack = tracker;
    }

    @Override
    public void subscribe(@NonNull Consumer<? super ISignInClientApplication> observer) {
        try {
            if (mClientHolder == null) {
                observer.onError(new MSQASignInError(MSQASignInError.NO_INITIALIZE,
                        MSQASignInError.NO_INITIALIZE_MESSAGE));
                return;
            }
            mTrack.track(TAG, "start get application");
            observer.onSuccess(mClientHolder.getClientApplication());
            mTrack.track(TAG, "get application success");
        } catch (Exception e) {
            observer.onError(e);
            mTrack.track(TAG, "get application error:" + e.getMessage());
        }
    }

    public static Task<ISignInClientApplication> getApplicationTask(@Nullable ISignInClientHolder clientHolder,
                                                                    @NonNull MSQATrackerUtil tracker) {
        return Task.create(new AcquireClientApplicationTask(clientHolder, tracker))
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain())
                .nextTaskSchedulerOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
