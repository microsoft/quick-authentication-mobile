package com.microsoft.quick.auth.signin.signinclient;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quick.auth.signin.entity.AccountInfo;

import java.util.List;

public interface ISignInClientApplication {
    void signIn(@NonNull final Activity activity,
                @Nullable final String loginHint,
                @NonNull final String[] scopes,
                @NonNull final AuthenticationCallback callback
    );

    @WorkerThread
    boolean signOut(@Nullable IAccount account) throws Exception;

    @WorkerThread
    IAuthenticationResult acquireTokenSilent(@NonNull IAccount account,
                                             @NonNull final String[] scopes) throws Exception;

    void acquireToken(@NonNull final Activity activity,
                      @Nullable IAccount account,
                      @NonNull final String[] scopes,
                      @NonNull final AuthenticationCallback callback
    );

    @Nullable
    @WorkerThread
    IAccount getCurrentAccount() throws Exception;

    @Nullable
    IAccount getAccount(@NonNull final AccountInfo accountInfo) throws Exception;

    /**
     * Returns a List of {@link IAccount} objects for which this application has RefreshTokens.
     */
    @Nullable
    @WorkerThread
    List<IAccount> getAccounts() throws Exception;
}
