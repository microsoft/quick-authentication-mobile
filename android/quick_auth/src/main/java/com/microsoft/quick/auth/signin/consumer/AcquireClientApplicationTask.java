package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;

public class AcquireClientApplicationTask implements Task.ConsumerHolder<ISignInClientApplication> {
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
    public void start(@NonNull Consumer<? super ISignInClientApplication> consumer) {
        try {
            if (mClientHolder == null) {
                consumer.onError(new MSQASignInException(MSQAErrorString.NO_INITIALIZE,
                        MSQAErrorString.NO_INITIALIZE_MESSAGE));
                return;
            }
            mTrack.track(TAG, "start get application");
            consumer.onSuccess(mClientHolder.getClientApplication());
            mTrack.track(TAG, "get application success");
        } catch (Exception e) {
            consumer.onError(e);
            mTrack.track(TAG, "get application error:" + e.getMessage());
        }
    }

    public static Task<ISignInClientApplication> getApplicationTask(@Nullable ISignInClientHolder clientHolder,
                                                                    @NonNull MSQATrackerUtil tracker) {
        return Task.create(new AcquireClientApplicationTask(clientHolder, tracker))
                .taskScheduleOn(DirectToThreadSwitcher.directToIOWhenCreateInMain())
                .nextTaskSchedulerOn(DirectToThreadSwitcher.directToIOWhenCreateInMain());
    }
}
