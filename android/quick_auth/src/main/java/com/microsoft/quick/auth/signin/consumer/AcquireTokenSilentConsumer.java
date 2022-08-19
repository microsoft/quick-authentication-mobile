package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.entity.MQASignInTokenResult;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Function;

public class AcquireTokenSilentConsumer implements Function<IAccountClientApplication, ITokenResult> {
    private final @NonNull
    AccountInfo mAccountInfo;
    private final @NonNull
    String[] mScopes;

    public AcquireTokenSilentConsumer(final @NonNull AccountInfo accountInfo,
                                      final @NonNull String[] scopes) {
        mAccountInfo = accountInfo;
        mScopes = scopes;
    }

    @Override
    public ITokenResult apply(@NonNull IAccountClientApplication iAccountClientApplication) throws Exception {
        IAccount iAccount = iAccountClientApplication.getAccount(mAccountInfo);
        IAuthenticationResult result = iAccountClientApplication.acquireTokenSilent(iAccount, mScopes);
        return new MQASignInTokenResult(result);
    }
}
