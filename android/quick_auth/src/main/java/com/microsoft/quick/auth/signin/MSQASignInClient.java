package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenSilentTask;
import com.microsoft.quick.auth.signin.consumer.AcquireUserIdTask;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenTask;
import com.microsoft.quick.auth.signin.consumer.AcquireCurrentTokenTask;
import com.microsoft.quick.auth.signin.consumer.AcquireUserPhotoTask;
import com.microsoft.quick.auth.signin.consumer.SignInTask;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.entity.MSQASignInScope;
import com.microsoft.quick.auth.signin.error.MSQACancelError;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.signinclient.SingleClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

public final class MSQASignInClient implements SignInClient {
    private static final String TAG = "MSQASignInClient";
    private final String[] mScopes;
    private final @NonNull
    IClientApplication mSignInClientApplication;

    private MSQASignInClient(@NonNull SingleClientApplication signInClientApplication) {
        mScopes = new String[]{MSQASignInScope.READ};
        mSignInClientApplication = signInClientApplication;
    }

    public static void create(@NonNull final Context context,
                              @NonNull final MSQASignInOptions signInOptions,
                              @NonNull final ClientCreatedListener listener) {
        PublicClientApplication.createSingleAccountPublicClientApplication(context.getApplicationContext(),
                signInOptions.getConfigResourceId(),
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        MSQALogger.getInstance().init(context);
                        setEnableLogcatLog(signInOptions.isEnableLogcatLog());
                        setLogLevel(signInOptions.getLogLevel());
                        setExternalLogger(signInOptions.getExternalLogger());
                        MSQASignInClient client = new MSQASignInClient(new SingleClientApplication(application));
                        listener.onCreated(client);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        listener.onError(MSQASignInError.create(exception));
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
     * Set the custom logger. Configures external logging to configure a callback that the sdk
     * will use to pass each
     * log message. Overriding the logger callback is not allowed.
     *
     * @param externalLogger The reference to the ILoggerCallback that can output the logs to the
     *                       designated
     *                       places.
     */
    public static void setExternalLogger(final ILogger externalLogger) {
        if (externalLogger == null) return;
        MSQALogger.getInstance().setExternalLogger(externalLogger);
    }

    @Override
    public void signIn(@NonNull Activity activity,
                       @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        final MSQATracker tracker = new MSQATracker("signIn");
        Task.with(mSignInClientApplication)
                .then(new SignInTask(activity, mScopes, tracker))
                .then(new AcquireUserIdTask(tracker))
                .then(new AcquireUserPhotoTask(tracker))
                .start(new Consumer<MSQAAccountInfo>() {
                    @Override
                    public void onSuccess(MSQAAccountInfo microsoftAccount) {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request signIn api success", null);
                        completeListener.onComplete(microsoftAccount, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, LogLevel.ERROR, "inner request signIn api error", t);
                        completeListener.onComplete(null, MSQASignInError.create(t));
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request signIn api cancel", null);
                        completeListener.onComplete(null, MSQACancelError.create());
                    }
                });
    }


    @Override
    public void signOut(@NonNull final OnCompleteListener<Boolean> completeListener) {
        final MSQATracker tracker = new MSQATracker("signOut");
        mSignInClientApplication.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                tracker.track(TAG, LogLevel.VERBOSE, "inner request signOut api result= true", null);
                completeListener.onComplete(true, null);
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                tracker.track(TAG, LogLevel.ERROR, "inner request signOut api error", exception);
                completeListener.onComplete(false, MSQASignInError.create(exception));
            }
        });
    }

    @Override
    public void getCurrentSignInAccount(@NonNull final Activity activity,
                                        @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        final MSQATracker tracker = new MSQATracker("getCurrentSignInAccount");
        Task.with(mSignInClientApplication)
                .then(new AcquireCurrentTokenTask(activity, false, mScopes, tracker))
                .then(new AcquireUserIdTask(tracker))
                .then(new AcquireUserPhotoTask(tracker))
                .start(new Consumer<MSQAAccountInfo>() {
                    @Override
                    public void onSuccess(MSQAAccountInfo microsoftAccountInfo) {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request getCurrentSignInAccount api success", null);
                        completeListener.onComplete(microsoftAccountInfo, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        if (t instanceof MSQASignInError && (MSQAErrorString.NO_CURRENT_ACCOUNT.equals(((MSQASignInError) t).getErrorCode()))) {
                            tracker.track(TAG, LogLevel.ERROR, "inner request getCurrentSignInAccount api error: no " +
                                    "account signed", t);
                            completeListener.onComplete(null, null);
                        } else {
                            tracker.track(TAG, LogLevel.ERROR, "inner request getCurrentSignInAccount api error", t);
                            completeListener.onComplete(null, MSQASignInError.create(t));
                        }
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request getCurrentSignInAccount api cancel", null);
                        completeListener.onComplete(null, MSQACancelError.create());
                    }
                });
    }

    @Override
    public void acquireToken(@NonNull final Activity activity, @NonNull final String[] scopes,
                             @NonNull final OnCompleteListener<TokenResult> completeListener) {
        final MSQATracker tracker = new MSQATracker("acquireToken");

        Task.with(mSignInClientApplication)
                .then(new AcquireTokenTask(activity, scopes, tracker))
                .start(new Consumer<TokenResult>() {
                    @Override
                    public void onSuccess(TokenResult tokenResult) {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request acquireToken api success", null);
                        completeListener.onComplete(tokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request acquireToken api error", t);
                        completeListener.onComplete(null, MSQASignInError.create(t));
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request acquireToken api cancel", null);
                        completeListener.onComplete(null, MSQACancelError.create());
                    }
                });
    }

    @Override
    public void acquireTokenSilent(@NonNull final String[] scopes,
                                   @NonNull final OnCompleteListener<TokenResult> completeListener) {
        final MSQATracker tracker = new MSQATracker("acquireTokenSilent");
        Task.with(mSignInClientApplication)
                .then(new AcquireTokenSilentTask(scopes, tracker))
                .start(new Consumer<TokenResult>() {
                    @Override
                    public void onSuccess(TokenResult tokenResult) {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request acquireTokenSilent api success", null);
                        completeListener.onComplete(tokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        completeListener.onComplete(null, MSQASignInError.create(t));
                        tracker.track(TAG, LogLevel.ERROR, "inner request acquireTokenSilent api error", t);
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, LogLevel.VERBOSE, "inner request acquireTokenSilent api cancel", null);
                        completeListener.onComplete(null, MSQACancelError.create());
                    }
                });
    }
}
