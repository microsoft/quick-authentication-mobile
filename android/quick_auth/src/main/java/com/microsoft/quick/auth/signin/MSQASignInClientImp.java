package com.microsoft.quick.auth.signin;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.task.TaskDisposable;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public final class MSQASignInClientImp implements MSQASignInClient {
    private final Context mContext;
    private final TaskDisposable mDisposable;
    private static final String TAG = MSQASignInClientImp.class.getSimpleName();

    public MSQASignInClientImp(@NonNull Context context) {
        mContext = context;
        mDisposable = new TaskDisposable();
    }

    @Override
    public Disposable signIn(@NonNull Activity activity,
                             @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        Disposable disposable = MSQASignInClientHelper.signIn(activity, null, null,
                completeListener, new MSQATrackerUtil("signIn"));
        mDisposable.add(disposable);
        return disposable;
    }


    @Override
    public Disposable signOut(@NonNull final OnCompleteListener<Boolean> callback) {
        Disposable disposable = MSQASignInClientHelper.signOut(callback, new MSQATrackerUtil("signOut"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable getCurrentSignInAccount(@NonNull final Activity activity,
                                              @NonNull final OnCompleteListener<AccountInfo> completeListener) {
        Disposable disposable = MSQASignInClientHelper.getCurrentSignInAccount(activity, false,
                completeListener, new MSQATrackerUtil("getCurrentSignInAccount"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable acquireToken(@NonNull final Activity activity, @NonNull final String[] scopes,
                                   @Nullable final String loginHint,
                                   @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        Disposable disposable = MSQASignInClientHelper.acquireToken(activity, scopes, loginHint,
                completeListener, new MSQATrackerUtil("acquireToken"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public Disposable acquireTokenSilent(@NonNull final AccountInfo accountInfo,
                                         @NonNull final String[] scopes,
                                         @NonNull final OnCompleteListener<ITokenResult> completeListener) {
        Disposable disposable = MSQASignInClientHelper.acquireTokenSilent(accountInfo, scopes,
                completeListener, new MSQATrackerUtil("acquireTokenSilent"));
        mDisposable.add(disposable);
        return disposable;
    }

    @Override
    public void release() {
        mDisposable.dispose();
    }
}
