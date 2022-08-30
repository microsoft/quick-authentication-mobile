package com.microsoft.quick.auth.signin.callback;

import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.error.MSQASignInException;

public interface OnCompleteListener<TResult> {
    /**
     * Called once finished and pass the nullable result and nullable error object.
     *
     * @param result the finished result. Can be null
     * @param error  the finished error. Can be null
     */
    void onComplete(@Nullable TResult result, @Nullable MSQASignInException error);
}
