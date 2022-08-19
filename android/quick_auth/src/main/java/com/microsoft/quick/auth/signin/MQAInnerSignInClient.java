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
import com.microsoft.quick.auth.signin.error.MicrosoftSignInError;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInErrorHelper;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.DefaultConsumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;

public final class MQAInnerSignInClient {

    public static Disposable signIn(@NonNull Activity activity,
                                    final OnSuccessListener<? super MQAAccountInfo> successListener,
                                    final OnFailureListener failureListener,
                                    final OnCompleteListener<? super MQAAccountInfo> completeListener) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(true);
        return MQAApplicationTask.getApplicationObservable(signClient)
                .flatMap(new SignInConsumer(activity, signClient.getOptions()))
                .errorRetry(new AccountSignedErrorConsumer(activity, signClient))
                .map(new AccountUpdateWithGraphConsumer())
                .map(new AccountPhotoConsumer())
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<MQAAccountInfo>() {
                    @Override
                    public void onSuccess(MQAAccountInfo microsoftAccount) {
                        if (successListener != null) {
                            successListener.onSuccess(microsoftAccount);
                        }
                        if (completeListener != null) {
                            completeListener.onComplete(microsoftAccount, null);
                        }
                    }

                    @Override
                    public void onError(Exception t) {
                        if (failureListener != null) {
                            failureListener.onFailure(t);
                        }
                        if (completeListener != null) {
                            completeListener.onComplete(null, t);
                        }
                    }
                });
    }

    public static Disposable signOut(@NonNull final OnCompleteListener<Boolean> callback) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(true);
        return MQAApplicationTask.getApplicationObservable(signClient)
                .map(new SignOutConsumer())
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<Boolean>() {
                    @Override
                    public void onSuccess(Boolean b) {
                        if (callback != null) {
                            callback.onComplete(b, null);
                        }
                    }

                    @Override
                    public void onError(Exception t) {
                        if (callback != null) {
                            callback.onComplete(false, t);
                        }
                    }
                });
    }

    public static Disposable getCurrentSignInAccount(@NonNull final Activity activity,
                                                     boolean errorRetry,
                                                     @NonNull final OnCompleteListener<?
                                                             super MQAAccountInfo> completeListener) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(true);
        return MQAApplicationTask.getApplicationObservable(signClient)
                .map(new CurrentAccountConsumer())
                .flatMap(new CurrentTokenRequestConsumer(activity, errorRetry, signClient))
                .map(new AccountUpdateWithGraphConsumer())
                .map(new AccountPhotoConsumer())
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<MQAAccountInfo>() {
                    @Override
                    public void onSuccess(MQAAccountInfo microsoftAccountInfo) {
                        completeListener.onComplete(microsoftAccountInfo, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        if (t instanceof MicrosoftSignInError && (MicrosoftSignInErrorHelper.NO_CURRENT_ACCOUNT.equals(((MicrosoftSignInError) t).getErrorCode()))) {
                            completeListener.onComplete(null, null);
                        } else {
                            completeListener.onComplete(null, t);
                        }
                    }
                });
    }

    public static Disposable acquireTokenSilent(@NonNull final AccountInfo accountInfo,
                                                @NonNull final String[] scopes,
                                                @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(false);
        return MQAApplicationTask.getApplicationObservable(signClient)
                .map(new AcquireTokenSilentConsumer(accountInfo, scopes))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<ITokenResult>() {
                    @Override
                    public void onSuccess(ITokenResult iTokenResult) {
                        super.onSuccess(iTokenResult);
                        completeListener.onComplete(iTokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        super.onError(t);
                        completeListener.onComplete(null, t);
                    }
                });
    }

    public static Disposable acquireToken(@NonNull final Activity activity,
                                          @NonNull final String[] scopes,
                                          @Nullable final String loginHint,
                                          @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        final IAccountClientHolder signClient =
                MQAApplicationManager.getInstance().getSignInApplication(false);
        return MQAApplicationTask.getApplicationObservable(signClient)
                .flatMap(new AcquireTokenConsumer(activity, scopes, loginHint))
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain())
                .subscribe(new DefaultConsumer<ITokenResult>() {
                    @Override
                    public void onSuccess(ITokenResult iTokenResult) {
                        super.onSuccess(iTokenResult);
                        completeListener.onComplete(iTokenResult, null);
                    }

                    @Override
                    public void onError(Exception t) {
                        super.onError(t);
                        completeListener.onComplete(null, t);
                    }
                });
    }
}
