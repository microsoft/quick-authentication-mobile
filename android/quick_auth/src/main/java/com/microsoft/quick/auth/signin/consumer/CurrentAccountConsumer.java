package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.error.MSQASignInErrorHelper;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.tracker.MSQATracker;

public class CurrentAccountConsumer implements Function<IAccountClientApplication, IAccount> {
    private static final String TAG = CurrentAccountConsumer.class.getSimpleName();
    private @NonNull
    final MSQATracker mTracker;

    public CurrentAccountConsumer(@NonNull final MSQATracker tracker) {
        mTracker = tracker;
    }

    @Override
    public IAccount apply(@NonNull IAccountClientApplication iAccountClientApplication) throws Exception {
        mTracker.track(TAG, "start get current account");
        IAccount currentAccount = iAccountClientApplication.getCurrentAccount();
        if (currentAccount != null) {
            mTracker.track(TAG, "get current account success");
            return currentAccount;
        } else {
            mTracker.track(TAG,
                    "get current account error:" + MSQASignInErrorHelper.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
            throw new MSQASignInError(MSQASignInErrorHelper.NO_CURRENT_ACCOUNT,
                    MSQASignInErrorHelper.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
        }
    }
}
