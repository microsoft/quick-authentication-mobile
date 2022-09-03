package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.error.MSQACancelException;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.internal.consumer.AcquireCurrentTokenTask;
import com.microsoft.quick.auth.signin.internal.consumer.AcquireTokenSilentTask;
import com.microsoft.quick.auth.signin.internal.consumer.AcquireTokenTask;
import com.microsoft.quick.auth.signin.internal.consumer.AcquireUserIdTask;
import com.microsoft.quick.auth.signin.internal.consumer.AcquireUserPhotoTask;
import com.microsoft.quick.auth.signin.internal.consumer.SignInTask;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quick.auth.signin.internal.entity.MSQASignInScope;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.signinclient.SingleClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.MSQALogger;

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
      @NonNull Activity activity,
      @NonNull final OnCompleteListener<MSQAAccountInfo> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "signIn");
    MSQATask.with(mSignInClientApplication)
        .then(new SignInTask(activity, mScopes, tracker))
        .then(new AcquireUserIdTask(tracker))
        .then(new AcquireUserPhotoTask(tracker))
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
      @NonNull final OnCompleteListener<MSQAAccountInfo> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "getCurrentSignInAccount");
    MSQATask.with(mSignInClientApplication)
        .then(new AcquireCurrentTokenTask(activity, false, mScopes, tracker))
        .then(new AcquireUserIdTask(tracker))
        .then(new AcquireUserPhotoTask(tracker))
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
      @NonNull final OnCompleteListener<MSQATokenResult> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "acquireToken");

    MSQATask.with(mSignInClientApplication)
        .then(new AcquireTokenTask(activity, scopes, tracker))
        .subscribe(
            new MSQAConsumer<MSQATokenResult>() {
              @Override
              public void onSuccess(MSQATokenResult tokenResult) {
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
      @NonNull final OnCompleteListener<MSQATokenResult> completeListener) {
    final MSQATracker tracker = new MSQATracker(mContext, "acquireTokenSilent");
    MSQATask.with(mSignInClientApplication)
        .then(new AcquireTokenSilentTask(scopes, tracker))
        .subscribe(
            new MSQAConsumer<MSQATokenResult>() {
              @Override
              public void onSuccess(MSQATokenResult tokenResult) {
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
