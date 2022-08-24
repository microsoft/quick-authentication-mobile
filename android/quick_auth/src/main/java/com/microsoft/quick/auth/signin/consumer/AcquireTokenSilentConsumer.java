package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.entity.MQASignInTokenResult;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.tracker.MQATracker;

public class AcquireTokenSilentConsumer implements Function<IAccountClientApplication,
        ITokenResult> {
    private final @NonNull
    AccountInfo mAccountInfo;
    private final @NonNull
    String[] mScopes;
    private @NonNull
    final MQATracker mTracker;
    private static final String TAG = AcquireTokenSilentConsumer.class.getSimpleName();

    public AcquireTokenSilentConsumer(final @NonNull AccountInfo accountInfo,
                                      final @NonNull String[] scopes,
                                      @NonNull final MQATracker tracker) {
        mAccountInfo = accountInfo;
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public ITokenResult apply(@NonNull IAccountClientApplication iAccountClientApplication) throws Exception {
        mTracker.track(TAG, "start request MSAL api acquireTokenSilent");
        IAccount iAccount = iAccountClientApplication.getAccount(mAccountInfo);
        IAuthenticationResult result = iAccountClientApplication.acquireTokenSilent(iAccount,
                mScopes);
        return new MQASignInTokenResult(result);
    }
}
