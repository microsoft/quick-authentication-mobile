package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public class AcquireCurrentAccountTask implements Function<ISignInClientApplication, IAccount> {
    private static final String TAG = AcquireCurrentAccountTask.class.getSimpleName();
    private @NonNull
    final MSQATrackerUtil mTracker;

    public AcquireCurrentAccountTask(@NonNull final MSQATrackerUtil tracker) {
        mTracker = tracker;
    }

    @Override
    public IAccount apply(@NonNull ISignInClientApplication iSignInClientApplication) throws Exception {
        mTracker.track(TAG, "start get current account");
        IAccount currentAccount = iSignInClientApplication.getCurrentAccount();
        if (currentAccount != null) {
            mTracker.track(TAG, "get current account success");
            return currentAccount;
        } else {
            mTracker.track(TAG,
                    "get current account error:" + MSQASignInError.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
            throw new MSQASignInError(MSQASignInError.NO_CURRENT_ACCOUNT,
                    MSQASignInError.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
        }
    }
}
