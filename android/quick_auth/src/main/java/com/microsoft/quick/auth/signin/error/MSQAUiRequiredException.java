package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalUiRequiredException;

/**
 * This error class is a wrapper error class for {@link MsalUiRequiredException}.
 */
public class MSQAUiRequiredException extends MSQASignInException {

    public MSQAUiRequiredException(String errorCode) {
        super(errorCode);
    }

    public MSQAUiRequiredException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public MSQAUiRequiredException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }
}
