//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.internal.entity.MSQASignInScopeInternal;
import com.microsoft.quickauth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quickauth.signin.internal.signinclient.MSQASingleSignInClientInternal;
import com.microsoft.quickauth.signin.logger.ILogger;
import com.microsoft.quickauth.signin.logger.LogLevel;

/**
 * This is the entry point for developer to create public native applications and make API calls.
 */
public final class MSQASignInClient implements ISignInClient {
  private static final String TAG = "MSQASignInClient";
  private final String[] mScopes;
  private final @NonNull MSQASingleSignInClientInternal mSignInClient;
  private final Context mContext;

  private MSQASignInClient(
      Context context, @NonNull ISingleAccountPublicClientApplication signInClientApplication) {
    mScopes = new String[] {MSQASignInScopeInternal.READ};
    mSignInClient = new MSQASingleSignInClientInternal(signInClientApplication);
    mContext = context;
  }

  /**
   * This function will read the configurations from {@link MSQASignInOptions} to create the
   * MSQASignInClient.
   *
   * @param context The sdk requires the application context to be passed in {@link
   *     MSQASignInClient}. Cannot be null.
   * @param signInOptions A configuration item for client initialization.
   * @param listener A callback to be invoked when the object is successfully created. Cannot be
   *     null.
   */
  public static void create(
      @NonNull final Context context,
      @NonNull final MSQASignInOptions signInOptions,
      @NonNull final ClientCreatedListener listener) {
    if (!isResourceExist(context, signInOptions.getConfigResourceId())) {
      listener.onError(
          new MSQAException(
              MSQAErrorString.NO_CONFIGURATION_FILE_ERROR,
              MSQAErrorString.NO_CONFIGURATION_FILE_ERROR_MESSAGE));
      return;
    }
    PublicClientApplication.createSingleAccountPublicClientApplication(
        context.getApplicationContext(),
        signInOptions.getConfigResourceId(),
        new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
          @Override
          public void onCreated(ISingleAccountPublicClientApplication application) {
            MSQALogger.getInstance().init(context);
            setEnableLogcatLog(signInOptions.isEnableLogcatLog());
            setLogLevel(signInOptions.getLogLevel());
            setExternalLogger(signInOptions.getExternalLogger());
            MSQASignInClient client =
                new MSQASignInClient(context.getApplicationContext(), application);
            listener.onCreated(client);
          }

          @Override
          public void onError(MsalException exception) {
            listener.onError(MSQAException.create(exception));
          }
        });
  }

  /**
   * Set the log level for diagnostic purpose. By default, the sdk enables the verbose level
   * logging.
   *
   * @param logLevel The {@link LogLevel} to be enabled for the diagnostic logging.
   */
  public static void setLogLevel(final @LogLevel int logLevel) {
    MSQALogger.getInstance().setLogLevel(logLevel);
  }

  /**
   * Enable/Disable the Android logcat logging. By default, the sdk disables it.
   *
   * @param enableLogcatLog True if enabling the logcat logging, false otherwise.
   */
  public static void setEnableLogcatLog(final boolean enableLogcatLog) {
    MSQALogger.getInstance().setEnableLogcatLog(enableLogcatLog);
  }

  /**
   * Set the custom logger. Configures external logging to configure a callback that the sdk will
   * use to pass each log message. Overriding the logger callback is not allowed.
   *
   * @param externalLogger The reference to the ILoggerCallback that can output the logs to the
   *     designated places.
   */
  public static void setExternalLogger(final ILogger externalLogger) {
    if (externalLogger == null) return;
    MSQALogger.getInstance().setExternalLogger(externalLogger);
  }

  @Override
  public void signIn(
      @NonNull final Activity activity,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    mSignInClient.getCurrentAccountAsync(
        new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
          @Override
          public void onAccountLoaded(@Nullable IAccount activeAccount) {
            mSignInClient.signIn(activity, activeAccount, mScopes, completeListener);
          }

          @Override
          public void onAccountChanged(
              @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
            mSignInClient.signIn(activity, currentAccount, mScopes, completeListener);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            completeListener.onComplete(null, MSQAException.create(exception));
          }
        });
  }

  @Override
  public void signOut(@NonNull final OnCompleteListener<Boolean> completeListener) {
    mSignInClient.signOut(
        new ISingleAccountPublicClientApplication.SignOutCallback() {
          @Override
          public void onSignOut() {
            completeListener.onComplete(true, null);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            completeListener.onComplete(false, MSQAException.create(exception));
          }
        });
  }

  @Override
  public void getCurrentAccount(
      @NonNull final Activity activity,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    mSignInClient.getCurrentAccountAsync(
        new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
          @Override
          public void onAccountLoaded(@Nullable IAccount activeAccount) {
            mSignInClient.getCurrentSignInAccount(
                activity, activeAccount, mScopes, false, completeListener);
          }

          @Override
          public void onAccountChanged(
              @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
            mSignInClient.getCurrentSignInAccount(
                activity, currentAccount, mScopes, false, completeListener);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            completeListener.onComplete(null, MSQAException.create(exception));
          }
        });
  }

  @Override
  public void acquireToken(
      @NonNull final Activity activity,
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<TokenResult> completeListener) {
    mSignInClient.getCurrentAccountAsync(
        new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
          @Override
          public void onAccountLoaded(@Nullable IAccount activeAccount) {
            mSignInClient.acquireToken(
                activity,
                scopes,
                activeAccount,
                new OnCompleteListener<IAuthenticationResult>() {
                  @Override
                  public void onComplete(
                      @Nullable IAuthenticationResult iAuthenticationResult,
                      @Nullable MSQAException error) {
                    completeListener.onComplete(
                        iAuthenticationResult != null
                            ? new MSQATokenResultInternal(iAuthenticationResult)
                            : null,
                        error);
                  }
                });
          }

          @Override
          public void onAccountChanged(
              @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
            mSignInClient.acquireToken(
                activity,
                scopes,
                currentAccount,
                new OnCompleteListener<IAuthenticationResult>() {
                  @Override
                  public void onComplete(
                      @Nullable IAuthenticationResult iAuthenticationResult,
                      @Nullable MSQAException error) {
                    completeListener.onComplete(
                        iAuthenticationResult != null
                            ? new MSQATokenResultInternal(iAuthenticationResult)
                            : null,
                        error);
                  }
                });
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            completeListener.onComplete(null, MSQAException.create(exception));
          }
        });
  }

  @Override
  public void acquireTokenSilent(
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<TokenResult> completeListener) {
    mSignInClient.getCurrentAccountAsync(
        new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
          @Override
          public void onAccountLoaded(@Nullable final IAccount activeAccount) {
            mSignInClient.acquireTokenSilent(
                activeAccount,
                scopes,
                new OnCompleteListener<IAuthenticationResult>() {
                  @Override
                  public void onComplete(
                      @Nullable IAuthenticationResult iAuthenticationResult,
                      @Nullable MSQAException error) {
                    completeListener.onComplete(
                        iAuthenticationResult != null
                            ? new MSQATokenResultInternal(iAuthenticationResult)
                            : null,
                        error);
                  }
                });
          }

          @Override
          public void onAccountChanged(
              @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
            mSignInClient.acquireTokenSilent(
                currentAccount,
                scopes,
                new OnCompleteListener<IAuthenticationResult>() {
                  @Override
                  public void onComplete(
                      @Nullable IAuthenticationResult iAuthenticationResult,
                      @Nullable MSQAException error) {
                    completeListener.onComplete(
                        iAuthenticationResult != null
                            ? new MSQATokenResultInternal(iAuthenticationResult)
                            : null,
                        error);
                  }
                });
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            completeListener.onComplete(null, MSQAException.create(exception));
          }
        });
  }

  /**
   * Check if the resource is exist.
   *
   * @param context Context that is used check resource.
   * @param resId The resource ID in android which you want to check.
   * @return true if resource is exist.
   */
  private static boolean isResourceExist(Context context, int resId) {
    if (context != null) {
      try {
        return context.getResources().getResourceName(resId) != null;
      } catch (Resources.NotFoundException ignore) {
      }
    }
    return false;
  }
}
