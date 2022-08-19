package com.microsoft.quick.auth.signin.callback;

import androidx.annotation.NonNull;

public interface OnFailureListener {
    /**
     * Called once failure and pass the error object.
     *
     * @param error the error result.
     */
    void onFailure(@NonNull Exception error);
}
