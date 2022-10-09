package com.azuresamples.quickauth.sign.test;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalUserCancelException;
import com.microsoft.quickauth.signin.internal.signinclient.MSQASingleSignInClientInternal;

/** MSQA ckient, mock the action of {@link MSQASingleSignInClientInternal} */
public class MSQATestSingleSignInClient extends MSQASingleSignInClientInternal {
  protected final @NonNull ISingleAccountPublicClientApplication mSignClient;

  private @MSQATestFlag String mFlag = MSQATestFlag.SUCCESS;
  private Context mContext;

  public MSQATestSingleSignInClient(
      ISingleAccountPublicClientApplication application, Context context) {
    super(application);
    mSignClient = application;
    mContext = context;
  }

  public void setFlag(@MSQATestFlag String flag) {
    mFlag = flag;
  }

  @Override
  public void signIn(
      @NonNull Activity activity,
      @Nullable String loginHint,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    switch (mFlag) {
      case MSQATestFlag.ERROR:
        callback.onError(new MsalClientException("sign in test error"));
        break;
      case MSQATestFlag.CANCEL:
        callback.onError(new MsalUserCancelException());
        break;
      default:
        callback.onSuccess(new MSQATestAuthenticationResult(activity));
        break;
    }
  }

  @Override
  public boolean signOut() throws Exception {
    switch (mFlag) {
      case MSQATestFlag.ERROR:
        throw new MsalClientException("sign out test error");
      case MSQATestFlag.CANCEL:
        throw new MsalUserCancelException();
      default:
        return true;
    }
  }

  @Override
  public void signOut(@NonNull ISingleAccountPublicClientApplication.SignOutCallback callback) {
    if (MSQATestFlag.ERROR.equals(mFlag)) {
      callback.onError(new MsalClientException("sign out test error"));
    } else if (MSQATestFlag.CANCEL.equals(mFlag)) {
      callback.onError(new MsalUserCancelException());
    } else if (!TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      callback.onSignOut();
    } else {
      callback.onError(
          new MsalClientException(
              MsalClientException.NO_CURRENT_ACCOUNT,
              MsalClientException.NO_CURRENT_ACCOUNT_ERROR_MESSAGE));
    }
  }

  @Override
  public IAuthenticationResult acquireTokenSilent(
      @NonNull IAccount account, @NonNull String[] scopes) throws Exception {
    switch (mFlag) {
      case MSQATestFlag.ERROR:
        throw new MsalClientException("acquire token silent test error");
      case MSQATestFlag.CANCEL:
        throw new MsalUserCancelException();
      default:
        return new MSQATestAuthenticationResult(mContext);
    }
  }

  @Override
  public void acquireTokenSilentAsync(
      @NonNull IAccount account,
      @NonNull String[] scopes,
      @NonNull SilentAuthenticationCallback callback) {
    switch (mFlag) {
      case MSQATestFlag.ERROR:
        callback.onError(new MsalClientException("acquire token silent test error"));
        break;
      case MSQATestFlag.CANCEL:
        callback.onError(new MsalUserCancelException());
        break;
      default:
        callback.onSuccess(new MSQATestAuthenticationResult(mContext));
        break;
    }
  }

  @Override
  public void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
      @NonNull AuthenticationCallback callback) {
    switch (mFlag) {
      case MSQATestFlag.ERROR:
        callback.onError(new MsalClientException("acquire token test error"));
        break;
      case MSQATestFlag.CANCEL:
        callback.onError(new MsalUserCancelException());
        break;
      default:
        callback.onSuccess(new MSQATestAuthenticationResult(mContext));
        break;
    }
  }

  @Nullable
  @Override
  public IAccount getCurrentAccount() throws Exception {
    if (!TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      return new MSQATestIAccount(mContext);
    } else {
      return null;
    }
  }

  @Override
  public void getCurrentAccountAsync(
      @NonNull ISingleAccountPublicClientApplication.CurrentAccountCallback callback) {
    if (!TextUtils.isEmpty(MSQATestMockUtil.getCurrentAccount(mContext))) {
      callback.onAccountLoaded(new MSQATestIAccount(mContext));
    } else {
      callback.onAccountLoaded(null);
    }
  }
}
