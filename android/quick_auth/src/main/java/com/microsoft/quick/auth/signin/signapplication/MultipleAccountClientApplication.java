package com.microsoft.quick.auth.signin.signapplication;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;

import java.util.List;

public class MultipleAccountClientApplication implements IAccountClientApplication {

    private final @NonNull
    IMultipleAccountPublicClientApplication mSignClient;
    private static final String TAG = MultipleAccountClientApplication.class.getSimpleName();

    public MultipleAccountClientApplication(@NonNull IMultipleAccountPublicClientApplication signClient) {
        mSignClient = signClient;
    }

    @Override
    public void signIn(@NonNull Activity activity, @Nullable String loginHint,
                       @NonNull String[] scopes,
                       @NonNull AuthenticationCallback callback) {
        mSignClient.acquireToken(activity, scopes, callback);
    }

    @Override
    public boolean signOut(@Nullable IAccount account) throws Exception {
        if (account == null) return false;
        return mSignClient.removeAccount(account);
    }

    @Override
    public IAuthenticationResult acquireTokenSilent(@NonNull IAccount account,
                                                    @NonNull String[] scopes) throws Exception {
        return mSignClient.acquireTokenSilent(scopes, account, account.getAuthority());
    }

    @Override
    public void acquireToken(@NonNull Activity activity, @NonNull String[] scopes, @Nullable final String loginHint,
                             @NonNull AuthenticationCallback callback) {
        mSignClient.acquireToken(activity, scopes, loginHint, callback);
    }

    @Nullable
    @Override
    public IAccount getCurrentAccount() throws Exception {
        List<IAccount> accounts = getAccounts();
        return accounts != null && accounts.size() > 0 ? accounts.get(0) : null;
    }

    @Override
    public IAccount getAccount(@NonNull AccountInfo accountInfo) throws Exception {
        if (accountInfo instanceof MSQAAccountInfo && ((MSQAAccountInfo) accountInfo).getIAccount() != null) {
            return ((MSQAAccountInfo) accountInfo).getIAccount();
        }
        if (TextUtils.isEmpty(accountInfo.getUserName())) return null;
        return mSignClient.getAccount(accountInfo.getUserName());
    }

    @Nullable
    @Override
    public List<IAccount> getAccounts() throws Exception {
        return mSignClient.getAccounts();
    }
}
