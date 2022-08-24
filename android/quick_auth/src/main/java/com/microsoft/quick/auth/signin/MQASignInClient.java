package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.tracker.MQATracker;

public final class MQASignInClient implements SignInClient {
    private final Context mContext;
    private final TaskDisposable mDisposable;
    private static final String TAG = MQASignInClient.class.getSimpleName();

    public MQASignInClient(@NonNull Context context) {
        mContext = context;
        mDisposable = new TaskDisposable();
    }

    @Override
    public Disposable signIn(@NonNull Activity activity,
                             @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        Disposable disposable = MQAInnerSignInClient.signIn(activity, null, null,
                completeListener, new MQATracker("signIn"));
        mDisposable.add(disposable);
        return disposable;
    }


    @Override
    public Disposable signOut(@NonNull final OnCompleteListener<Boolean> callback) {
        Disposable disposable = MQAInnerSignInClient.signOut(callback, new MQATracker("signOut"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable getCurrentSignInAccount(@NonNull final Activity activity,
                                              @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        Disposable disposable = MQAInnerSignInClient.getCurrentSignInAccount(activity, false,
                completeListener, new MQATracker("getCurrentSignInAccount"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable acquireToken(@NonNull final Activity activity, @NonNull final String[] scopes,
                                   @Nullable final String loginHint,
                                   @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        Disposable disposable = MQAInnerSignInClient.acquireToken(activity, scopes, loginHint,
                completeListener, new MQATracker("acquireToken"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable acquireTokenSilent(@NonNull final AccountInfo accountInfo,
                                         @NonNull final String[] scopes,
                                         @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        Disposable disposable = MQAInnerSignInClient.acquireTokenSilent(accountInfo, scopes,
                completeListener, new MQATracker("acquireTokenSilent"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public void release() {
        mDisposable.dispose();
    }
}
