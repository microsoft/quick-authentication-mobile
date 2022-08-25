package com.microsoft.quick.auth.signin.entity;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;

public class SignInParameter {
    private final @NonNull
    Activity mActivity;

    private final @NonNull
    MSQASignInClient mSignInClient;

    private final @NonNull
    OnCompleteListener<AccountInfo> mOnCompleteListener;

    /**
     * @param activity           Activity that is used as the parent activity for launching sign
     *                           in page.
     * @param signInClient       SignInClient that is a sign-in function delegate used to sign-in.
     * @param onCompleteListener A callback to be invoked when complete and will return sign in
     *                           account info.
     */
    public SignInParameter(@NonNull Activity activity, @NonNull MSQASignInClient signInClient,
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
    public MSQASignInClient getSignInClient() {
        return mSignInClient;
    }

    public void startSignIn() {
        mSignInClient.signIn(mActivity, mOnCompleteListener);
    }
}
