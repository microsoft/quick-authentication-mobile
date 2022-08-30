package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class AcquireTokenSilentTask implements Function<ISignInClientApplication,
        TokenResult> {
    private @NonNull
    final List<String> mScopes;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = AcquireTokenSilentTask.class.getSimpleName();

    public AcquireTokenSilentTask(@NonNull final List<String> scopes,
                                  @NonNull final MSQATrackerUtil tracker) {
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public TokenResult apply(@NonNull ISignInClientApplication iSignInClientApplication) throws Exception {
        mTracker.track(TAG, "start request MSAL api acquireTokenSilent");
        IAccount iAccount = iSignInClientApplication.getCurrentAccount();
        if (iAccount == null)
            throw new MSQASignInException(MSQAErrorString.NO_CURRENT_ACCOUNT,
                    MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
        IAuthenticationResult result = iSignInClientApplication.acquireTokenSilent(iAccount,
                mScopes);
        return new MSQASignInTokenResult(result);
    }
}
