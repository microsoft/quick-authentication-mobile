package com.microsoft.quick.auth.signin.internal.signinclient;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.quick.auth.signin.MSQAAccountInfo;
import java.util.List;

public interface IClientApplication {
  void signIn(
      @NonNull Activity activity,
      @Nullable String loginHint,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback);

  @WorkerThread
  boolean signOut() throws Exception;

  void signOut(@NonNull final ISingleAccountPublicClientApplication.SignOutCallback callback);

  @WorkerThread
  IAuthenticationResult acquireTokenSilent(@NonNull IAccount account, @NonNull String[] scopes)
      throws Exception;

  void acquireTokenSilentAsync(
      @NonNull IAccount account,
      @NonNull String[] scopes,
      @NonNull SilentAuthenticationCallback callback);

  void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback);

  @Nullable
  @WorkerThread
  IAccount getCurrentAccount() throws Exception;

  void getCurrentAccountAsync(
      @NonNull ISingleAccountPublicClientApplication.CurrentAccountCallback callback);

  @Nullable
  IAccount getAccount(@NonNull MSQAAccountInfo accountInfo) throws Exception;

  /** Returns a List of {@link IAccount} objects for which this application has RefreshTokens. */
  @Nullable
  @WorkerThread
  List<IAccount> getAccounts() throws Exception;
}
