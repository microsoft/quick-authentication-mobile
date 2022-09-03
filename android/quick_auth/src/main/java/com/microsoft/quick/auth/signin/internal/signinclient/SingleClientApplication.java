package com.microsoft.quick.auth.signin.internal.signinclient;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.quick.auth.signin.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import java.util.ArrayList;
import java.util.List;

public class SingleClientApplication implements IClientApplication {

  private final @NonNull ISingleAccountPublicClientApplication mSignClient;
  private final List<IAccount> mAccounts = new ArrayList<>();

  public SingleClientApplication(@NonNull ISingleAccountPublicClientApplication signClient) {
    mSignClient = signClient;
  }

  @Override
  public void signIn(
      @NonNull Activity activity,
      @Nullable String loginHint,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    mSignClient.signIn(activity, loginHint, scopes, callback);
  }

  @Override
  public boolean signOut() throws Exception {
    return mSignClient.signOut();
  }

  @Override
  public void signOut(@NonNull ISingleAccountPublicClientApplication.SignOutCallback callback) {
    mSignClient.signOut(callback);
  }

  @Override
  public IAuthenticationResult acquireTokenSilent(
      @NonNull IAccount account, @NonNull String[] scopes) throws Exception {
    return mSignClient.acquireTokenSilent(scopes, account.getAuthority());
  }

  @Override
  public void acquireTokenSilentAsync(
      @NonNull IAccount account,
      @NonNull String[] scopes,
      @NonNull SilentAuthenticationCallback callback) {
    mSignClient.acquireTokenSilentAsync(scopes, account.getAuthority(), callback);
  }

  @Override
  public void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
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

  @Override
  public void getCurrentAccountAsync(
      @NonNull ISingleAccountPublicClientApplication.CurrentAccountCallback callback) {
    mSignClient.getCurrentAccountAsync(callback);
  }

  @Nullable
  @Override
  public IAccount getAccount(@NonNull MSQAAccountInfo accountInfo) throws Exception {
    if (accountInfo instanceof MSQAAccountInfoInternal
        && ((MSQAAccountInfoInternal) accountInfo).getIAccount() != null) {
      return ((MSQAAccountInfoInternal) accountInfo).getIAccount();
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
