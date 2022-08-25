package com.microsoft.quick.auth.signin.callback;

import androidx.annotation.NonNull;

public interface MSQASignInCallback<TResult> {
    /**
     * Called once succeed and pass the result object.
     *
     * @param result the success result.
     */
    void onSuccess(@NonNull TResult result);

    /**
     * Called once failure and pass the error object.
     *
     * @param error the error result.
     */
    void onFailure(@NonNull Exception error);

    void onCancel();
}
