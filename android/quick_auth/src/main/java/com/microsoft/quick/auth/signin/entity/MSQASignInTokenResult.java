package com.microsoft.quick.auth.signin.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;

import java.util.Date;
import java.util.UUID;

public class MSQASignInTokenResult implements ITokenResult {

    private final @NonNull
    IAuthenticationResult mAuthenticationResult;

    public MSQASignInTokenResult(@NonNull IAuthenticationResult authenticationResult) {
        mAuthenticationResult = authenticationResult;
    }

    @NonNull
    @Override
    public String getAccessToken() {
        return mAuthenticationResult.getAccessToken();
    }

    @NonNull
    @Override
    public String getAuthorizationHeader() {
        return mAuthenticationResult.getAuthorizationHeader();
    }

    @NonNull
    @Override
    public String getAuthenticationScheme() {
        return mAuthenticationResult.getAuthenticationScheme();
    }

    @NonNull
    @Override
    public Date getExpiresOn() {
        return mAuthenticationResult.getExpiresOn();
    }

    @Nullable
    @Override
    public String getTenantId() {
        return mAuthenticationResult.getTenantId();
    }

    @NonNull
    @Override
    public IAccount getAccount() {
        return mAuthenticationResult.getAccount();
    }

    @NonNull
    @Override
    public String[] getScope() {
        return mAuthenticationResult.getScope();
    }

    @Nullable
    @Override
    public UUID getCorrelationId() {
        return mAuthenticationResult.getCorrelationId();
    }
}
