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
import com.microsoft.quickauth.signin.error.MSQANoAccountException;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.internal.entity.MSQASignInScopeInternal;
import com.microsoft.quickauth.signin.internal.entity.MSQATokenResultInternal;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricListener;
import com.microsoft.quickauth.signin.internal.metric.MSQASignInMetricListener;
import com.microsoft.quickauth.signin.internal.signinclient.MSQASingleSignInClientInternal;
import com.microsoft.quickauth.signin.logger.ILogger;
import com.microsoft.quickauth.signin.logger.LogLevel;

/**
 * This is the entry point for developer to create public native applications and make API calls.
 */
public final class MSQASignInClient {
  private static final String TAG = "MSQASignInClient";
  private final String[] mScopes;
  private final @NonNull MSQASingleSignInClientInternal mSignInClient;

  private MSQASignInClient(@NonNull ISingleAccountPublicClientApplication signInClientApplication) {
    mScopes = new String[] {MSQASignInScopeInternal.READ};
    mSignInClient = new MSQASingleSignInClientInternal(signInClientApplication);
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
      MSQAException exception =
          new MSQAException(
              MSQAErrorString.NO_CONFIGURATION_FILE_ERROR,
              MSQAErrorString.NO_CONFIGURATION_FILE_ERROR_MESSAGE);
      listener.onError(exception);
      MSQALogger.getInstance().error(TAG, "client initialize error", exception);
      return;
    }
    MSQALogger.getInstance().verbose(TAG, "client initialize started");
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
            MSQASignInClient client = new MSQASignInClient(application);
            MSQALogger.getInstance().verbose(TAG, "client initialize success");
            listener.onCreated(client);
          }

          @Override
          public void onError(MsalException exception) {
            MSQALogger.getInstance().error(TAG, "client initialize error", exception);
            listener.onError(MSQAException.mapToMSQAException(exception));
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

  /**
   * Allows a user to sign in to your application with one of their accounts. This method may only
   * be called once: once a user is signed in, they must first be signed out before another user may
   * sign in.
   *
   * @param activity Activity that is used as the parent activity for launching sign in page.
   * @param completeListener A callback to be invoked when sign in success and will return sign in
   *     account info {@link AccountInfo}.
   */
  public void signIn(
      @NonNull final Activity activity,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    OnCompleteListener<AccountInfo> internalListener = completeListener;
    if (!(completeListener instanceof MSQAMetricListener)) {
      MSQAMetricController controller = new MSQAMetricController(MSQAMetricEvent.SIGN_IN);
      internalListener = new MSQASignInMetricListener<>(controller, completeListener, false);
    }
    OnCompleteListener<AccountInfo> finalInternalListener = internalListener;
    mSignInClient.getCurrentAccountAsync(
        new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
          @Override
          public void onAccountLoaded(@Nullable IAccount activeAccount) {
            mSignInClient.signIn(activity, activeAccount, mScopes, finalInternalListener);
          }

          @Override
          public void onAccountChanged(
              @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
            mSignInClient.signIn(activity, currentAccount, mScopes, finalInternalListener);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            finalInternalListener.onComplete(null, MSQAException.mapToMSQAException(exception));
          }
        });
  }

  /**
   * Signs out the current the Account and Credentials (tokens).
   *
   * @param completeListener A callback to be invoked when sign out finishes and will return sign
   *     out result.
   */
  public void signOut(@NonNull final OnCompleteListener<Boolean> completeListener) {
    MSQAMetricController controller = new MSQAMetricController(MSQAMetricEvent.SIGN_OUT);
    OnCompleteListener<Boolean> internalListener =
        new MSQAMetricListener<>(controller, completeListener);

    mSignInClient.signOut(
        new ISingleAccountPublicClientApplication.SignOutCallback() {
          @Override
          public void onSignOut() {
            internalListener.onComplete(true, null);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            internalListener.onComplete(false, MSQAException.mapToMSQAException(exception));
          }
        });
  }

  /**
   * Gets the current account. This method must be called whenever the application is resumed or
   * prior to running a scheduled background operation.
   *
   * @param completeListener A callback to be invoked when complete and will return sign in account
   *     info {@link AccountInfo} if success
   */
  public void getCurrentAccount(@NonNull final OnCompleteListener<AccountInfo> completeListener) {
    MSQAMetricController controller = new MSQAMetricController(MSQAMetricEvent.GET_CURRENT_ACCOUNT);
    OnCompleteListener<AccountInfo> internalListener =
        new MSQAMetricListener<AccountInfo>(controller, null) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            // If no account return no error.
            if (error instanceof MSQANoAccountException) {
              error = null;
            }
            completeListener.onComplete(accountInfo, error);
          }
        };

    mSignInClient.getCurrentAccountAsync(
        new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
          @Override
          public void onAccountLoaded(@Nullable IAccount activeAccount) {
            mSignInClient.getCurrentAccount(activeAccount, mScopes, internalListener);
          }

          @Override
          public void onAccountChanged(
              @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
            mSignInClient.getCurrentAccount(currentAccount, mScopes, internalListener);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            internalListener.onComplete(null, MSQAException.mapToMSQAException(exception));
          }
        });
  }

  /**
   * Acquire token interactively, will pop-up webUI. Interactive flow will skip the cache lookup.
   *
   * @param activity Activity that is used as the parent activity for get token.
   * @param scopes The non-null array of scopes to be requested for the access token, the supported
   *     scopes can be found in{@link MSQASignInScopeInternal}.
   * @param completeListener A callback to be invoked when token get finished.
   */
  public void acquireToken(
      @NonNull final Activity activity,
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<TokenResult> completeListener) {
    MSQAMetricController controller = new MSQAMetricController(MSQAMetricEvent.ACQUIRE_TOKEN);
    OnCompleteListener<TokenResult> internalListener =
        new MSQAMetricListener<>(controller, completeListener);
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
                    internalListener.onComplete(
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
                    internalListener.onComplete(
                        iAuthenticationResult != null
                            ? new MSQATokenResultInternal(iAuthenticationResult)
                            : null,
                        error);
                  }
                });
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            internalListener.onComplete(null, MSQAException.mapToMSQAException(exception));
          }
        });
  }

  /**
   * Perform acquire token silent call. If there is a valid access token in the cache, the sdk will
   * return the access token; If no valid access token exists, the sdk will try to find a refresh
   * token and use the refresh token to get a new access token. If refresh token does not exist or
   * it fails the refresh, exception will be sent back via callback.
   *
   * @param scopes The non-null array of scopes to be requested for the access token, the supported
   *     scopes can be found in{@link MSQASignInScopeInternal}.
   * @param completeListener A callback to be invoked when token get finished.
   */
  public void acquireTokenSilent(
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<TokenResult> completeListener) {
    MSQAMetricController controller =
        new MSQAMetricController(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT);
    OnCompleteListener<TokenResult> internalListener =
        new MSQAMetricListener<>(controller, completeListener);
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
                    internalListener.onComplete(
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
                    internalListener.onComplete(
                        iAuthenticationResult != null
                            ? new MSQATokenResultInternal(iAuthenticationResult)
                            : null,
                        error);
                  }
                });
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            internalListener.onComplete(null, MSQAException.mapToMSQAException(exception));
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
