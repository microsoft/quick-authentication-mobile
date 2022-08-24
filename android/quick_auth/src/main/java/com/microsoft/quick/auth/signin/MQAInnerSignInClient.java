package com.microsoft.quick.auth.signin;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.callback.OnFailureListener;
import com.microsoft.quick.auth.signin.callback.OnSuccessListener;
import com.microsoft.quick.auth.signin.consumer.AccountPhotoConsumer;
import com.microsoft.quick.auth.signin.consumer.AccountSignedErrorConsumer;
import com.microsoft.quick.auth.signin.consumer.AccountUpdateWithGraphConsumer;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenConsumer;
import com.microsoft.quick.auth.signin.consumer.AcquireTokenSilentConsumer;
import com.microsoft.quick.auth.signin.consumer.CurrentAccountConsumer;
import com.microsoft.quick.auth.signin.consumer.CurrentTokenRequestConsumer;
import com.microsoft.quick.auth.signin.consumer.SignInConsumer;
import com.microsoft.quick.auth.signin.consumer.SignOutConsumer;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;
import com.microsoft.quick.auth.signin.tracker.MQATracker;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInError;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInErrorHelper;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.DefaultConsumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;

public final class MQAInnerSignInClient {
    private static final String TAG = MQAInnerSignInClient.class.getSimpleName();

    public static Disposable signIn(@NonNull Activity activity,
                                    final OnSuccessListener<? super MQAAccountInfo> successListener,
                                    final OnFailureListener failureListener,
                                    final OnCompleteListener<? super MQAAccountInfo> completeListener, @NonNull final MQATracker tracker) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(true);
        return MQAApplicationTask.getApplicationObservable(signClient, tracker)
                .flatMap(new SignInConsumer(activity, signClient.getOptions(), tracker))
                .errorRetry(new AccountSignedErrorConsumer(activity, signClient, tracker))
                .map(new AccountUpdateWithGraphConsumer(tracker))
                .map(new AccountPhotoConsumer(tracker))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<MQAAccountInfo>() {
                    @Override
                    public void onSuccess(MQAAccountInfo microsoftAccount) {
                        tracker.track(TAG, "inner request signIn api success");
                        if (successListener != null) {
                            successListener.onSuccess(microsoftAccount);
                        }
                        if (completeListener != null) {
                            completeListener.onComplete(microsoftAccount, null);
                        }
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, "inner request signIn api error:" + t.getMessage());
                        if (failureListener != null) {
                            failureListener.onFailure(t);
                        }
                        if (completeListener != null) {
                            completeListener.onComplete(null, t);
                        }
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        tracker.track(TAG, "inner request signIn api cancel");
                    }
                });
    }

    public static Disposable signOut(@NonNull final OnCompleteListener<Boolean> callback,
                                     @NonNull final MQATracker tracker) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(true);
        return MQAApplicationTask.getApplicationObservable(signClient, tracker)
                .map(new SignOutConsumer(tracker))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<Boolean>() {
                    @Override
                    public void onSuccess(Boolean b) {
                        tracker.track(TAG, "inner request signOut api result=" + b);
                        if (callback != null) {
                            callback.onComplete(b, null);
                        }
                    }

                    @Override
                    public void onError(Exception t) {
                        tracker.track(TAG, "inner request signOut api error:" + t);
                        if (callback != null) {
                            callback.onComplete(false, t);
                        }
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        tracker.track(TAG, "inner request signOut api cancel");
                    }
                });
    }

    public static Disposable getCurrentSignInAccount(@NonNull final Activity activity,
                                                     boolean errorRetry,
                                                     @NonNull final OnCompleteListener<?
                                                             super MQAAccountInfo> completeListener, @NonNull final MQATracker tracker) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(true);
        return MQAApplicationTask.getApplicationObservable(signClient, tracker)
                .map(new CurrentAccountConsumer(tracker))
                .flatMap(new CurrentTokenRequestConsumer(activity, errorRetry, signClient, tracker))
                .map(new AccountUpdateWithGraphConsumer(tracker))
                .map(new AccountPhotoConsumer(tracker))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<MQAAccountInfo>() {
                    @Override
                    public void onSuccess(MQAAccountInfo microsoftAccountInfo) {
                        tracker.track(TAG, "inner request getCurrentSignInAccount api success");
                        completeListener.onComplete(microsoftAccountInfo, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        if (t instanceof MicrosoftSignInError && (MicrosoftSignInErrorHelper.NO_CURRENT_ACCOUNT.equals(((MicrosoftSignInError) t).getErrorCode()))) {
                            tracker.track(TAG,
                                    "inner request getCurrentSignInAccount api error:" + MicrosoftSignInErrorHelper.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
                            completeListener.onComplete(null, null);
                        } else {
                            tracker.track(TAG,
                                    "inner request getCurrentSignInAccount api error:" + t.getMessage());
                            completeListener.onComplete(null, t);
                        }
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        tracker.track(TAG, "inner request getCurrentSignInAccount api cancel");
                    }
                });
    }

    public static Disposable acquireTokenSilent(@NonNull final AccountInfo accountInfo,
                                                @NonNull final String[] scopes,
                                                @NonNull final OnCompleteListener<ITokenResult> completeListener, @NonNull final MQATracker tracker) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(false);
        return MQAApplicationTask.getApplicationObservable(signClient, tracker)
                .map(new AcquireTokenSilentConsumer(accountInfo, scopes, tracker))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<ITokenResult>() {
                    @Override
                    public void onSuccess(ITokenResult iTokenResult) {
                        super.onSuccess(iTokenResult);
                        tracker.track(TAG, "inner request acquireTokenSilent api success");
                        completeListener.onComplete(iTokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        super.onError(t);
                        completeListener.onComplete(null, t);
                        tracker.track(TAG,
                                "inner request acquireTokenSilent api error:" + t.getMessage());
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        tracker.track(TAG, "inner request acquireTokenSilent api cancel");
                    }
                });
    }

    public static Disposable acquireToken(@NonNull final Activity activity,
                                          @NonNull final String[] scopes,
                                          @Nullable final String loginHint,
                                          @NonNull final OnCompleteListener<ITokenResult> completeListener,
                                          @NonNull final MQATracker tracker) {
        final IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(false);
        return MQAApplicationTask.getApplicationObservable(signClient, tracker)
                .flatMap(new AcquireTokenConsumer(activity, scopes, loginHint, tracker))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<ITokenResult>() {
                    @Override
                    public void onSuccess(ITokenResult iTokenResult) {
                        super.onSuccess(iTokenResult);
                        tracker.track(TAG, "inner request acquireToken api success");
                        completeListener.onComplete(iTokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        super.onError(t);
                        tracker.track(TAG,
                                "inner request acquireToken api error:" + t.getMessage());
                        completeListener.onComplete(null, t);
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        tracker.track(TAG, "inner request acquireToken api cancel");
                    }
                });
    }
}
