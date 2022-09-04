package com.microsoft.quick.auth.signin.internal.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quick.auth.signin.TokenResult;
import java.util.Date;
import java.util.UUID;

public class MSQATokenResultInternal implements TokenResult {

  private final @NonNull IAuthenticationResult mAuthenticationResult;

  public MSQATokenResultInternal(@NonNull IAuthenticationResult authenticationResult) {
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
  public String[] getScope() {
    return mAuthenticationResult.getScope();
  }

  @Nullable
  @Override
  public UUID getCorrelationId() {
    return mAuthenticationResult.getCorrelationId();
  }
}
