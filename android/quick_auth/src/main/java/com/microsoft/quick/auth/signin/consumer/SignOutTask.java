package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public class SignOutTask implements Function<ISignInClientApplication, Boolean> {
    private static final String TAG = SignOutTask.class.getSimpleName();
    private @NonNull
    final MSQATrackerUtil mTracker;

    public SignOutTask(@NonNull final MSQATrackerUtil tracker) {
        mTracker = tracker;
    }

    @Override
    public Boolean apply(@NonNull ISignInClientApplication iSignInClientApplication) throws Exception {
        mTracker.track(TAG, "start sign out");
        return iSignInClientApplication.signOut(null);
    }
}
