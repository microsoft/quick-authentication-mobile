package com.microsoft.quick.auth.signin.signapplicationclient;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;

import java.util.ArrayList;
import java.util.List;

public class SingleAccountClientApplication implements IAccountClientApplication {

    private final @NonNull
    ISingleAccountPublicClientApplication mSignClient;
    private final List<IAccount> mAccounts = new ArrayList<>();
    private static final String TAG = SingleAccountClientApplication.class.getSimpleName();

    public SingleAccountClientApplication(@NonNull ISingleAccountPublicClientApplication signClient) {
        mSignClient = signClient;
    }

    @Override
    public void signIn(@NonNull Activity activity, @Nullable String loginHint,
                       @NonNull String[] scopes,
                       @NonNull AuthenticationCallback callback) {
        mSignClient.signIn(activity, loginHint, scopes, callback);
    }

    @Override
    public boolean signOut(@Nullable IAccount account) throws Exception {
        return mSignClient.signOut();
    }

    @Override
    public IAuthenticationResult acquireTokenSilent(@NonNull IAccount account,
                                                    @NonNull String[] scopes) throws Exception {
        return mSignClient.acquireTokenSilent(scopes, account.getAuthority());
    }

    @Override
    public void acquireToken(@NonNull Activity activity, @NonNull String[] scopes, @Nullable final String loginHint,
                             @NonNull AuthenticationCallback callback) {
        mSignClient.acquireToken(activity, scopes, callback);
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
        if (accountInfo instanceof MQAAccountInfo && ((MQAAccountInfo) accountInfo).getIAccount() != null) {
            return ((MQAAccountInfo) accountInfo).getIAccount();
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
