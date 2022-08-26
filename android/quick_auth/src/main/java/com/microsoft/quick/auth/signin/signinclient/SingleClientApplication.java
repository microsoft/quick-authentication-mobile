package com.microsoft.quick.auth.signin.signinclient;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AcquireTokenParameters;
import com.microsoft.identity.client.AcquireTokenSilentParameters;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SignInParameters;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;

import java.util.ArrayList;
import java.util.List;

public class SingleClientApplication implements ISignInClientApplication {

    private final @NonNull
    ISingleAccountPublicClientApplication mSignClient;
    private final List<IAccount> mAccounts = new ArrayList<>();
    private static final String TAG = SingleClientApplication.class.getSimpleName();

    public SingleClientApplication(@NonNull ISingleAccountPublicClientApplication signClient) {
        mSignClient = signClient;
    }

    @Override
    public void signIn(@NonNull Activity activity, @Nullable String loginHint,
                       @NonNull List<String> scopes,
                       @NonNull AuthenticationCallback callback) {
        mSignClient.signIn(SignInParameters.builder()
                .withActivity(activity)
                .withLoginHint(loginHint)
                .withScopes(scopes)
                .withCallback(callback)
                .build());
    }

    @Override
    public boolean signOut(@Nullable IAccount account) throws Exception {
        return mSignClient.signOut();
    }

    @Override
    public IAuthenticationResult acquireTokenSilent(@NonNull IAccount account,
                                                    @NonNull List<String> scopes) throws Exception {
        return mSignClient.acquireTokenSilent(new AcquireTokenSilentParameters(new AcquireTokenSilentParameters.Builder()
                .withScopes(scopes)
                .forAccount(account)
                .fromAuthority(account.getAuthority())
        ));
    }

    @Override
    public void acquireToken(@NonNull Activity activity, @Nullable IAccount account, @NonNull List<String> scopes,
                             @Nullable final String loginHint,
                             @NonNull AuthenticationCallback callback) {
        mSignClient.acquireToken(new AcquireTokenParameters(
                new AcquireTokenParameters.Builder()
                        .startAuthorizationFromActivity(activity)
                        .withScopes(scopes)
                        .forAccount(account)
                        .withLoginHint(loginHint)
                        .withCallback(callback)
        ));
    }

    @Nullable
    @Override
    public IAccount getCurrentAccount() throws Exception {
        ICurrentAccountResult currentAccount = mSignClient.getCurrentAccount();
        if (currentAccount != null && currentAccount.getCurrentAccount() != null) {
            return currentAccount.getCurrentAccount();
        }
        return null;
    }

    @Nullable
    @Override
    public IAccount getAccount(@NonNull AccountInfo accountInfo) throws Exception {
        if (accountInfo instanceof MSQAAccountInfo && ((MSQAAccountInfo) accountInfo).getIAccount() != null) {
            return ((MSQAAccountInfo) accountInfo).getIAccount();
        }
        return null;
    }

    @Nullable
    @Override
    public List<IAccount> getAccounts() throws Exception {
        IAccount account = getCurrentAccount();
        mAccounts.clear();
        if (account != null) {
            mAccounts.add(account);
        }
        return mAccounts;
    }
}
