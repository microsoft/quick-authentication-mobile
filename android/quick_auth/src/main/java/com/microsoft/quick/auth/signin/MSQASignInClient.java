package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.tracker.MSQATracker;

public final class MSQASignInClient implements SignInClient {
    private final Context mContext;
    private final TaskDisposable mDisposable;
    private static final String TAG = MSQASignInClient.class.getSimpleName();

    public MSQASignInClient(@NonNull Context context) {
        mContext = context;
        mDisposable = new TaskDisposable();
    }

    @Override
    public Disposable signIn(@NonNull Activity activity,
                             @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        Disposable disposable = MSQAInnerSignInClient.signIn(activity, null, null,
                completeListener, new MSQATracker("signIn"));
        mDisposable.add(disposable);
        return disposable;
    }


    @Override
    public Disposable signOut(@NonNull final OnCompleteListener<Boolean> callback) {
        Disposable disposable = MSQAInnerSignInClient.signOut(callback, new MSQATracker("signOut"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable getCurrentSignInAccount(@NonNull final Activity activity,
                                              @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        Disposable disposable = MSQAInnerSignInClient.getCurrentSignInAccount(activity, false,
                completeListener, new MSQATracker("getCurrentSignInAccount"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable acquireToken(@NonNull final Activity activity, @NonNull final String[] scopes,
                                   @Nullable final String loginHint,
                                   @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        Disposable disposable = MSQAInnerSignInClient.acquireToken(activity, scopes, loginHint,
                completeListener, new MSQATracker("acquireToken"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable acquireTokenSilent(@NonNull final AccountInfo accountInfo,
                                         @NonNull final String[] scopes,
                                         @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        Disposable disposable = MSQAInnerSignInClient.acquireTokenSilent(accountInfo, scopes,
                completeListener, new MSQATracker("acquireTokenSilent"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public void release() {
        mDisposable.dispose();
    }
}
