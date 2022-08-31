package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.consumer.AcquireUserPhotoTask;
import com.microsoft.quick.auth.signin.consumer.AcquireUserIdTask;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenTask;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenSilentTask;
import com.microsoft.quick.auth.signin.consumer.AcquireCurrentTokenTask;
import com.microsoft.quick.auth.signin.consumer.SignInTask;
import com.microsoft.quick.auth.signin.consumer.SignOutTask;
import com.microsoft.quick.auth.signin.consumer.TokenSilentErrorWrapTask;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.entity.MSQASignInScope;
import com.microsoft.quick.auth.signin.error.MSQACancelException;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;
import com.microsoft.quick.auth.signin.signinclient.SingleApplicationHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.consumer.AcquireClientApplicationTask;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public final class MSQASignInClient implements SignInClient {
    private static final String TAG = MSQASignInClient.class.getSimpleName();
    private final String[] mScopes;
    private volatile ISignInClientHolder mClientHolder;

    private MSQASignInClient() {
        mScopes = new String[]{MSQASignInScope.READ};
    }

    private static class SingletonHolder {
        private static final MSQASignInClient sInstance = new MSQASignInClient();
    }

    public static MSQASignInClient sharedInstance() {
        return MSQASignInClient.SingletonHolder.sInstance;
    }

    public synchronized void setSignInOptions(@NonNull Context context,
                                              final @NonNull MQASignInOptions signInOptions) {
        if (mClientHolder != null) return;
        mClientHolder = new SingleApplicationHolder(context, signInOptions.getConfigResourceId());
        MSQALogger.getInstance().init(context);
        setEnableLogcatLog(signInOptions.isEnableLogcatLog());
        setLogLevel(signInOptions.getLogLevel());
        setExternalLogger(signInOptions.getExternalLogger());
    }

    /**
     * Set the log level for diagnostic purpose. By default, the sdk enables the verbose level
     * logging.
     *
     * @param logLevel The {@link LogLevel} to be enabled for the diagnostic logging.
     */
    public void setLogLevel(final @LogLevel int logLevel) {
        MSQALogger.getInstance().setLogLevel(logLevel);
    }

    /**
     * Enable/Disable the Android logcat logging. By default, the sdk enables it.
     *
     * @param enableLogcatLog True if enabling the logcat logging, false otherwise.
     */
    public void setEnableLogcatLog(final boolean enableLogcatLog) {
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
    public void setExternalLogger(final ILogger externalLogger) {
        if (externalLogger == null) return;
        MSQALogger.getInstance().setExternalLogger(externalLogger);
    }

    @Override
    public void signIn(@NonNull Activity activity,
                       @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("signIn");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .taskConvert(new SignInTask(activity, mScopes, tracker))
                .convert(new AcquireUserIdTask(tracker))
                .convert(new AcquireUserPhotoTask(tracker))
                .nextTaskSchedulerOn(DirectThreadSwitcher.directToMainWhenCreateInMain())
                .start(new Consumer<MSQAAccountInfo>() {
                    @Override
                    public void onSuccess(MSQAAccountInfo microsoftAccount) {
                        tracker.track(TAG, "inner request signIn api success");
                        completeListener.onComplete(microsoftAccount, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, "inner request signIn api error:" + t.getMessage());
                        completeListener.onComplete(null, MSQASignInException.create(t));
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request signIn api cancel");
                        completeListener.onComplete(null, MSQACancelException.create());
                    }
                });
    }


    @Override
    public void signOut(@NonNull final OnCompleteListener<Boolean> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("signOut");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .convert(new SignOutTask(tracker))
                .nextTaskSchedulerOn(DirectThreadSwitcher.directToMainWhenCreateInMain())
                .start(new Consumer<Boolean>() {
                    @Override
                    public void onSuccess(Boolean b) {
                        tracker.track(TAG, "inner request signOut api result=" + b);
                        completeListener.onComplete(b, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, "inner request signOut api error:" + t);
                        completeListener.onComplete(false, MSQASignInException.create(t));
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request signOut api cancel");
                        completeListener.onComplete(null, MSQACancelException.create());
                    }
                });
    }

    @Override
    public void getCurrentSignInAccount(@NonNull final Activity activity,
                                        @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("getCurrentSignInAccount");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .taskConvert(new AcquireCurrentTokenTask(activity, false, mScopes, tracker))
                .convert(new AcquireUserIdTask(tracker))
                .convert(new AcquireUserPhotoTask(tracker))
                .nextTaskSchedulerOn(DirectThreadSwitcher.directToMainWhenCreateInMain())
                .start(new Consumer<MSQAAccountInfo>() {
                    @Override
                    public void onSuccess(MSQAAccountInfo microsoftAccountInfo) {
                        tracker.track(TAG, "inner request getCurrentSignInAccount api success");
                        completeListener.onComplete(microsoftAccountInfo, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        if (t instanceof MSQASignInException && (MSQAErrorString.NO_CURRENT_ACCOUNT.equals(((MSQASignInException) t).getErrorCode()))) {
                            tracker.track(TAG,
                                    "inner request getCurrentSignInAccount api error:" + MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                            completeListener.onComplete(null, null);
                        } else {
                            tracker.track(TAG,
                                    "inner request getCurrentSignInAccount api error:" + t.getMessage());
                            completeListener.onComplete(null, MSQASignInException.create(t));
                        }
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request getCurrentSignInAccount api cancel");
                        completeListener.onComplete(null, MSQACancelException.create());
                    }
                });
    }

    @Override
    public void acquireToken(@NonNull final Activity activity, @NonNull final String[] scopes,
                             @NonNull final OnCompleteListener<TokenResult> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("acquireToken");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .taskConvert(new AcquireTokenTask(activity, scopes, tracker))
                .nextTaskSchedulerOn(DirectThreadSwitcher.directToMainWhenCreateInMain())
                .start(new Consumer<TokenResult>() {
                    @Override
                    public void onSuccess(TokenResult tokenResult) {
                        tracker.track(TAG, "inner request acquireToken api success");
                        completeListener.onComplete(tokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG,
                                "inner request acquireToken api error:" + t.getMessage());
                        completeListener.onComplete(null, MSQASignInException.create(t));
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request acquireToken api cancel");
                        completeListener.onComplete(null, MSQACancelException.create());
                    }
                });
    }

    @Override
    public void acquireTokenSilent(@NonNull final String[] scopes,
                                   @NonNull final OnCompleteListener<TokenResult> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("acquireTokenSilent");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .convert(new AcquireTokenSilentTask(scopes, tracker))
                .errorConvert(new TokenSilentErrorWrapTask(tracker))
                .nextTaskSchedulerOn(DirectThreadSwitcher.directToMainWhenCreateInMain())
                .start(new Consumer<TokenResult>() {
                    @Override
                    public void onSuccess(TokenResult tokenResult) {
                        tracker.track(TAG, "inner request acquireTokenSilent api success");
                        completeListener.onComplete(tokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        completeListener.onComplete(null, MSQASignInException.create(t));
                        tracker.track(TAG,
                                "inner request acquireTokenSilent api error:" + t.getMessage());
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request acquireTokenSilent api cancel");
                        completeListener.onComplete(null, MSQACancelException.create());
                    }
                });
    }
}
