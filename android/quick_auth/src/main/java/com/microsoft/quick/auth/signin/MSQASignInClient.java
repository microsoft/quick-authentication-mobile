package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.consumer.AcquireUserPhotoTask;
import com.microsoft.quick.auth.signin.consumer.SignedErrorRetryTask;
import com.microsoft.quick.auth.signin.consumer.AcquireUserIdTask;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenTask;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenSilentTask;
import com.microsoft.quick.auth.signin.consumer.AcquireCurrentAccountTask;
import com.microsoft.quick.auth.signin.consumer.AcquireCurrentTokenTask;
import com.microsoft.quick.auth.signin.consumer.SignInTask;
import com.microsoft.quick.auth.signin.consumer.SignOutTask;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.entity.MSQASignInScope;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientHolder;
import com.microsoft.quick.auth.signin.signinclient.SingleApplicationHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.consumer.AcquireClientApplicationTask;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.ArrayList;
import java.util.List;

public final class MSQASignInClient implements SignInClient {
    private static final String TAG = MSQASignInClient.class.getSimpleName();
    private final List<String> mScopes;
    private ISignInClientHolder mClientHolder;

    private MSQASignInClient() {
        mScopes = new ArrayList<>();
        mScopes.add(MSQASignInScope.READ);
    }

    private static class SingletonHolder {
        private static final MSQASignInClient sInstance = new MSQASignInClient();
    }

    public static MSQASignInClient sharedInstance() {
        return MSQASignInClient.SingletonHolder.sInstance;
    }

    //    public void setSignInOptions(Context context, final MQASignInOptions signInOptions) throws MSQASignInError {
    public void setSignInOptions(Context context, final MQASignInOptions signInOptions) {
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
    public void setExternalLogger(final @NonNull ILogger externalLogger) {
        MSQALogger.getInstance().setExternalLogger(externalLogger);
    }

    @Override
    public void signIn(@NonNull Activity activity,
                       @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("signIn");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .flatMap(new SignInTask(activity, mScopes, tracker))
                .errorRetry(new SignedErrorRetryTask(activity, signClient, mScopes, tracker))
                .map(new AcquireUserIdTask(tracker))
                .map(new AcquireUserPhotoTask(tracker))
                .nextTaskSchedulerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new Consumer<MSQAAccountInfo>() {
                    @Override
                    public void onSuccess(MSQAAccountInfo microsoftAccount) {
                        tracker.track(TAG, "inner request signIn api success");
                        completeListener.onComplete(microsoftAccount, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, "inner request signIn api error:" + t.getMessage());
                        completeListener.onComplete(null, t);
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request signIn api cancel");
                    }
                });
    }


    @Override
    public void signOut(@NonNull final OnCompleteListener<Boolean> callback) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("signOut");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .map(new SignOutTask(tracker))
                .nextTaskSchedulerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void onSuccess(Boolean b) {
                        tracker.track(TAG, "inner request signOut api result=" + b);
                        callback.onComplete(b, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, "inner request signOut api error:" + t);
                        callback.onComplete(false, t);
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request signOut api cancel");
                    }
                });
    }

    @Override
    public void getCurrentSignInAccount(@NonNull final Activity activity,
                                        @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("getCurrentSignInAccount");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .map(new AcquireCurrentAccountTask(tracker))
                .flatMap(new AcquireCurrentTokenTask(activity, false, signClient, mScopes, null, tracker))
                .map(new AcquireUserIdTask(tracker))
                .map(new AcquireUserPhotoTask(tracker))
                .nextTaskSchedulerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new Consumer<MSQAAccountInfo>() {
                    @Override
                    public void onSuccess(MSQAAccountInfo microsoftAccountInfo) {
                        tracker.track(TAG, "inner request getCurrentSignInAccount api success");
                        completeListener.onComplete(microsoftAccountInfo, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        if (t instanceof MSQASignInError && (MSQASignInError.NO_CURRENT_ACCOUNT.equals(((MSQASignInError) t).getErrorCode()))) {
                            tracker.track(TAG,
                                    "inner request getCurrentSignInAccount api error:" + MSQASignInError.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                            completeListener.onComplete(null, null);
                        } else {
                            tracker.track(TAG,
                                    "inner request getCurrentSignInAccount api error:" + t.getMessage());
                            completeListener.onComplete(null, t);
                        }
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request getCurrentSignInAccount api cancel");
                    }
                });
    }

    @Override
    public void acquireToken(@NonNull final Activity activity, @NonNull final List<String> scopes,
                             @NonNull final OnCompleteListener<TokenResult> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("acquireToken");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .flatMap(new AcquireTokenTask(activity, scopes, null, tracker))
                .nextTaskSchedulerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new Consumer<TokenResult>() {
                    @Override
                    public void onSuccess(TokenResult tokenResult) {
                        tracker.track(TAG, "inner request acquireToken api success");
                        completeListener.onComplete(tokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG,
                                "inner request acquireToken api error:" + t.getMessage());
                        completeListener.onComplete(null, t);
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request acquireToken api cancel");
                    }
                });
    }

    @Override
    public void acquireTokenSilent(@NonNull final List<String> scopes,
                                   @NonNull final OnCompleteListener<TokenResult> completeListener) {
        ISignInClientHolder signClient = mClientHolder;
        final MSQATrackerUtil tracker = new MSQATrackerUtil("acquireTokenSilent");
        AcquireClientApplicationTask.getApplicationTask(signClient, tracker)
                .map(new AcquireTokenSilentTask(scopes, tracker))
                .nextTaskSchedulerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new Consumer<TokenResult>() {
                    @Override
                    public void onSuccess(TokenResult tokenResult) {
                        tracker.track(TAG, "inner request acquireTokenSilent api success");
                        completeListener.onComplete(tokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        completeListener.onComplete(null, t);
                        tracker.track(TAG,
                                "inner request acquireTokenSilent api error:" + t.getMessage());
                    }

                    @Override
                    public void onCancel() {
                        tracker.track(TAG, "inner request acquireTokenSilent api cancel");
                    }
                });
    }
}
