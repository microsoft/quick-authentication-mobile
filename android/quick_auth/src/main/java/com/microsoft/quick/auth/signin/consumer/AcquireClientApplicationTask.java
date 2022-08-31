package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;

public class AcquireClientApplicationTask implements Task.ConsumerHolder<ISignInClientApplication> {
    private @Nullable
    final ISignInClientHolder mClientHolder;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = AcquireClientApplicationTask.class.getSimpleName();

    public AcquireClientApplicationTask(@Nullable final ISignInClientHolder clientHolder,
                                        @NonNull final MSQATrackerUtil tracker) {
        mClientHolder = clientHolder;
        mTracker = tracker;
    }

    @Override
    public void start(@NonNull Consumer<? super ISignInClientApplication> consumer) {
        try {
            mTracker.track(TAG, "start get application");
            if (mClientHolder == null) {
                mTracker.track(TAG, "get application error:" + MSQAErrorString.NO_INITIALIZE_MESSAGE);
                consumer.onError(new MSQASignInException(MSQAErrorString.NO_INITIALIZE,
                        MSQAErrorString.NO_INITIALIZE_MESSAGE));
                return;
            }
            ISignInClientApplication application = mClientHolder.getClientApplication();
            mTracker.track(TAG, "get application success");
            consumer.onSuccess(application);
        } catch (Exception e) {
            mTracker.track(TAG, "get application error:" + e.getMessage());
            consumer.onError(e);
        }
    }

    public static Task<ISignInClientApplication> getApplicationTask(@Nullable ISignInClientHolder clientHolder,
                                                                    @NonNull MSQATrackerUtil tracker) {
        return Task.create(new AcquireClientApplicationTask(clientHolder, tracker))
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain())
                .nextTaskSchedulerOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
    }
}
