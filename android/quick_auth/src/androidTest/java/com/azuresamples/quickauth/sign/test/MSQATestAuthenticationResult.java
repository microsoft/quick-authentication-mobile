package com.azuresamples.quickauth.sign.test;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import java.util.Date;
import java.util.UUID;

// MSQA mock test AuthenticationResult.
public class MSQATestAuthenticationResult implements IAuthenticationResult {

  private final Context mContext;
  private @NonNull final MSQATestIAccount mAccount;

  public MSQATestAuthenticationResult(@NonNull Context context) {
    mContext = context;
    mAccount = new MSQATestIAccount(context);
  }

  @NonNull
  @Override
  public String getAccessToken() {
    return MSQATestMockUtil.getCurrentToken(mContext);
  }

  @NonNull
  @Override
  public String getAuthorizationHeader() {
    return MSQATestMockUtil.EMPTY_STRING;
  }

  @NonNull
  @Override
  public String getAuthenticationScheme() {
    return MSQATestMockUtil.EMPTY_STRING;
  }

  @NonNull
  @Override
  public Date getExpiresOn() {
    return new Date();
  }

  @Nullable
  @Override
  public String getTenantId() {
    return null;
  }

  @NonNull
  @Override
  public IAccount getAccount() {
    return mAccount;
  }

  @NonNull
  @Override
  public String[] getScope() {
    return new String[] {"user.read"};
  }

  @Nullable
  @Override
  public UUID getCorrelationId() {
    return null;
  }
}
