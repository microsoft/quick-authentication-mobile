package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.signapplication.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public class SignOutConsumer implements Function<IAccountClientApplication, Boolean> {
    private static final String TAG = SignOutConsumer.class.getSimpleName();
    private @NonNull
    final MSQATrackerUtil mTracker;

    public SignOutConsumer(@NonNull final MSQATrackerUtil tracker) {
        mTracker = tracker;
    }

    @Override
    public Boolean apply(@NonNull IAccountClientApplication iAccountClientApplication) throws Exception {
        mTracker.track(TAG, "start sign out");
        return iAccountClientApplication.signOut(null);
    }
}
