package com.microsoft.quick.auth.signin.error;

/**
 * Exception for user cancelling the flow.
 */
public class MSQACancelError extends MSQASignInError {
    public MSQACancelError(String errorCode) {
        super(errorCode);
    }

    public MSQACancelError(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public MSQACancelError(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }

    public static MSQACancelError create() {
        return new MSQACancelError(MSQAErrorString.USER_CANCEL_ERROR,
                MSQAErrorString.USER_CANCEL_ERROR_MESSAGE);
    }
}
