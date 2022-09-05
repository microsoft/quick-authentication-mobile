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
import androidx.annotation.NonNull;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQASignInException;
import com.microsoft.quickauth.signin.internal.consumer.AcquireCurrentAccountTask;
import com.microsoft.quickauth.signin.internal.consumer.AcquireCurrentTokenTask;
import com.microsoft.quickauth.signin.internal.consumer.AcquireTokenSilentTask;
import com.microsoft.quickauth.signin.internal.consumer.AcquireTokenTask;
import com.microsoft.quickauth.signin.internal.consumer.AcquireUserIdTask;
import com.microsoft.quickauth.signin.internal.consumer.AcquireUserPhotoTask;
import com.microsoft.quickauth.signin.internal.consumer.SignInTask;
import com.microsoft.quickauth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quickauth.signin.internal.entity.MSQASignInScope;
import com.microsoft.quickauth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quickauth.signin.internal.signinclient.SingleClientApplication;
import com.microsoft.quickauth.signin.internal.task.MSQAConsumer;
import com.microsoft.quickauth.signin.internal.task.MSQASwitchers;
import com.microsoft.quickauth.signin.internal.task.MSQATask;
import com.microsoft.quickauth.signin.internal.util.MSQATracker;
import com.microsoft.quickauth.signin.logger.ILogger;
import com.microsoft.quickauth.signin.logger.LogLevel;
import com.microsoft.quickauth.signin.logger.MSQALogger;

public final class MSQASignInClient implements ISignInClient {
  private static final String TAG = "MSQASignInClient";
  private final String[] mScopes;
  private final @NonNull IClientApplication mSignInClientApplication;
  private final Context mContext;

  private MSQASignInClient(
      Context context, @NonNull SingleClientApplication signInClientApplication) {
    mScopes = new String[] {MSQASignInScope.READ};
    mSignInClientApplication = signInClientApplication;
    mContext = context;
  }

  public static void create(
      @NonNull final Context context,
      @NonNull final MSQASignInOptions signInOptions,
      @NonNull final ClientCreatedListener listener) {
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
                new MSQASignInClient(
                    context.getApplicationContext(), new SingleClientApplication(application));
            listener.onCreated(client);
          }

          @Override
          public void onError(MsalException exception) {
            listener.onError(MSQASignInException.create(exception));
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
   * Enable/Disable the Android logcat logging. By default, the sdk enables it.
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
      @NonNull Activity activity, @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "signIn");
    MSQATask.with(mSignInClientApplication)
        .then(new AcquireCurrentAccountTask(tracker))
        .then(new SignInTask(activity, mScopes, tracker))
        .then(new AcquireUserIdTask(tracker))
        .then(new AcquireUserPhotoTask(tracker))
        .downStreamSchedulerOn(MSQASwitchers.mainThread())
        .subscribe(
            new MSQAConsumer<MSQAAccountInfoInternal>() {
              @Override
              public void onSuccess(MSQAAccountInfoInternal microsoftAccount) {
                tracker.track(TAG, LogLevel.VERBOSE, "inner request signIn api success", null);
                completeListener.onComplete(microsoftAccount, null);
              }

              @Override
              public void onError(Exception t) {
                tracker.track(TAG, LogLevel.ERROR, "inner request signIn api error", t);
                completeListener.onComplete(null, MSQASignInException.create(t));
              }

              @Override
              public void onCancel() {
                tracker.track(TAG, LogLevel.VERBOSE, "inner request signIn api cancel", null);
                completeListener.onComplete(null, MSQACancelException.create());
              }
            });
  }

  @Override
  public void signOut(@NonNull final OnCompleteListener<Boolean> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "signOut");
    mSignInClientApplication.signOut(
        new ISingleAccountPublicClientApplication.SignOutCallback() {
          @Override
          public void onSignOut() {
            tracker.track(TAG, LogLevel.VERBOSE, "inner request signOut api result= true", null);
            completeListener.onComplete(true, null);
          }

          @Override
          public void onError(@NonNull MsalException exception) {
            tracker.track(TAG, LogLevel.ERROR, "inner request signOut api error", exception);
            completeListener.onComplete(false, MSQASignInException.create(exception));
          }
        });
  }

  @Override
  public void getCurrentSignInAccount(
      @NonNull final Activity activity,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "getCurrentSignInAccount");
    MSQATask.with(mSignInClientApplication)
        .then(new AcquireCurrentAccountTask(tracker))
        .then(new AcquireCurrentTokenTask(activity, false, mScopes, tracker))
        .then(new AcquireUserIdTask(tracker))
        .then(new AcquireUserPhotoTask(tracker))
        .downStreamSchedulerOn(MSQASwitchers.mainThread())
        .subscribe(
            new MSQAConsumer<MSQAAccountInfoInternal>() {
              @Override
              public void onSuccess(MSQAAccountInfoInternal microsoftAccountInfo) {
                tracker.track(
                    TAG,
                    LogLevel.VERBOSE,
                    "inner request getCurrentSignInAccount api success",
                    null);
                completeListener.onComplete(microsoftAccountInfo, null);
              }

              @Override
              public void onError(Exception t) {
                if (t instanceof MSQASignInException
                    && (MSQAErrorString.NO_CURRENT_ACCOUNT.equals(
                        ((MSQASignInException) t).getErrorCode()))) {
                  tracker.track(
                      TAG,
                      LogLevel.ERROR,
                      "inner request getCurrentSignInAccount api error: no " + "account signed",
                      t);
                  completeListener.onComplete(null, null);
                } else {
                  tracker.track(
                      TAG, LogLevel.ERROR, "inner request getCurrentSignInAccount api error", t);
                  completeListener.onComplete(null, MSQASignInException.create(t));
                }
              }

              @Override
              public void onCancel() {
                tracker.track(
                    TAG,
                    LogLevel.VERBOSE,
                    "inner request getCurrentSignInAccount api cancel",
                    null);
                completeListener.onComplete(null, MSQACancelException.create());
              }
            });
  }

  @Override
  public void acquireToken(
      @NonNull final Activity activity,
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<TokenResult> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "acquireToken");

    MSQATask.with(mSignInClientApplication)
        .then(new AcquireCurrentAccountTask(tracker))
        .then(new AcquireTokenTask(activity, scopes, tracker))
        .subscribe(
            new MSQAConsumer<TokenResult>() {
              @Override
              public void onSuccess(TokenResult tokenResult) {
                tracker.track(
                    TAG, LogLevel.VERBOSE, "inner request acquireToken api success", null);
                completeListener.onComplete(tokenResult, null);
              }

              @Override
              public void onError(Exception t) {
                tracker.track(TAG, LogLevel.ERROR, "inner request acquireToken api error", t);
                completeListener.onComplete(null, MSQASignInException.create(t));
              }

              @Override
              public void onCancel() {
                tracker.track(TAG, LogLevel.VERBOSE, "inner request acquireToken api cancel", null);
                completeListener.onComplete(null, MSQACancelException.create());
              }
            });
  }

  @Override
  public void acquireTokenSilent(
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<TokenResult> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "acquireTokenSilent");
    MSQATask.with(mSignInClientApplication)
        .then(new AcquireCurrentAccountTask(tracker))
        .then(new AcquireTokenSilentTask(scopes, tracker))
        .subscribe(
            new MSQAConsumer<TokenResult>() {
              @Override
              public void onSuccess(TokenResult tokenResult) {
                tracker.track(
                    TAG, LogLevel.VERBOSE, "inner request acquireTokenSilent api success", null);
                completeListener.onComplete(tokenResult, null);
              }

              @Override
              public void onError(Exception t) {
                tracker.track(TAG, LogLevel.ERROR, "inner request acquireTokenSilent api error", t);
                completeListener.onComplete(null, MSQASignInException.create(t));
              }

              @Override
              public void onCancel() {
                tracker.track(
                    TAG, LogLevel.VERBOSE, "inner request acquireTokenSilent api cancel", null);
                completeListener.onComplete(null, MSQACancelException.create());
              }
            });
  }
}
