package com.microsoft.quick.auth.signin.entity;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.SignInClient;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.task.Task;

public class SignInParameter {
    private final @NonNull
    Activity mActivity;

    private final @NonNull
    SignInClient mSignInClient;

    private final @NonNull
    OnCompleteListener<AccountInfo> mOnCompleteListener;

    /**
     * @param activity           Activity that is used as the parent activity for launching sign
     *                           in page.
     * @param signInClient       SignInClient that is a sign-in function delegate used to sign-in.
     * @param onCompleteListener A callback to be invoked when complete and will return sign in
     *                           account info.
     */
    public SignInParameter(@NonNull Activity activity, @NonNull SignInClient signInClient,
                           @NonNull OnCompleteListener<AccountInfo> onCompleteListener) {
        mActivity = activity;
        mSignInClient = signInClient;
        mOnCompleteListener = onCompleteListener;
        mSignInClient.signIn(mActivity, onCompleteListener);
    }

    @NonNull
    public Activity getActivity() {
        return mActivity;
    }

    @NonNull
    public OnCompleteListener<AccountInfo> getOnCompleteListener() {
        return mOnCompleteListener;
    }

    @NonNull
    public SignInClient getSignInClient() {
        return mSignInClient;
    }

    public void startSignIn() {
        mSignInClient.signIn(mActivity, mOnCompleteListener);
    }
}
