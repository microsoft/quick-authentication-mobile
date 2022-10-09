package com.azuresamples.quickauth.sign.test;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import java.util.HashMap;
import java.util.Map;

// MSQA mock test IAccount.
public class MSQATestIAccount implements IAccount {

  private Context mContext;
  private Map<String, Object> mClaims;

  public MSQATestIAccount(Context context) {
    mContext = context;
    mClaims = new HashMap<>();
    mClaims.put("name", MSQATestMockUtil.CURRENT_FULL_NAME);
  }

  @NonNull
  @Override
  public String getId() {
    return MSQATestMockUtil.EMPTY_STRING;
  }

  @NonNull
  @Override
  public String getAuthority() {
    return MSQATestMockUtil.EMPTY_STRING;
  }

  @Nullable
  @Override
  public String getIdToken() {
    return MSQATestMockUtil.getCurrentToken(mContext);
  }

  @Nullable
  @Override
  public Map<String, ?> getClaims() {
    return mClaims;
  }

  @NonNull
  @Override
  public String getUsername() {
    return MSQATestMockUtil.CURRENT_USER_NAME;
  }

  @NonNull
  @Override
  public String getTenantId() {
    return MSQATestMockUtil.EMPTY_STRING;
  }
}
