package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.identity.client.internal.MsalUtils;
import com.microsoft.identity.common.java.exception.BaseException;

public class MSQASignInError extends BaseException {

    /**
     * No initialize sdk.
     */
    public static final String NO_INITIALIZE = "no_initialize";
    public static final String NO_INITIALIZE_MESSAGE = "Haven't initialize, please initialize first with " +
            "MSQASignInClient class";

    /**
     * No account currently signed in to SingleAccountPublicClientApplication
     */
    public static final String NO_CURRENT_ACCOUNT = MsalClientException.NO_CURRENT_ACCOUNT;
    public static final String NO_CURRENT_ACCOUNT_ERROR_MESSAGE =
            MsalClientException.NO_CURRENT_ACCOUNT_ERROR_MESSAGE;
    /**
     * Current sign in account change
     */
    public static final String ACCOUNT_CHANGE_ERROR = "account_change";
    public static final String ACCOUNT_CHANGE_ERROR_MESSAGE = "Current sign in account has " +
            "changed.";
    /**
     * Thread interrupted error
     */
    public static final String INTERRUPTED_ERROR = "interrupted_error";
    public static final String INTERRUPTED_ERROR_MESSAGE = "Request has been interrupted";

    /**
     * Http account info request error
     */
    public static final String HTTP_ACCOUNT_REQUEST_ERROR = "http_account_request_error";
    public static final String HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE = "Account info request " +
            "error.";

    /**
     * Http account photo request error
     */
    public static final String HTTP_ACCOUNT_PHOTO_REQUEST_ERROR =
            "http_account_photo_request_error";
    /**
     * Unsupported error
     */
    public static final String UNSUPPORTED_ERROR = "unsupported_error";
    public static final String UNEXPECTED_ERROR = "Unsupported error";

    public MSQASignInError() {
        super();
    }

    /**
     * Initiates the detailed error code.
     *
     * @param errorCode The error code contained in the exception.
     */
    public MSQASignInError(final String errorCode) {
        super(errorCode);
    }

    /**
     * Initiates the {@link MSQASignInError} with error code and error message.
     *
     * @param errorCode    The error code contained in the exception.
     * @param errorMessage The error message contained in the exception.
     */
    public MSQASignInError(final String errorCode, final String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Initiates the {@link MSQASignInError} with error code, error message and throwable.
     *
     * @param errorCode    The error code contained in the exception.
     * @param errorMessage The error message contained in the exception.
     * @param throwable    The {@link Throwable} contains the cause for the exception.
     */
    public MSQASignInError(final String errorCode, final String errorMessage,
                           final Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }

    /**
     * @return The error code for the exception, could be null. {@link MSQASignInError} is
     * the top level base exception, for the constants value of all the error code.
     */
    @Override
    public String getErrorCode() {
        return super.getErrorCode();
    }

    /**
     * {@inheritDoc}
     * Return the detailed description explaining why the exception is returned back.
     */
    @Override
    public String getMessage() {
        if (!MsalUtils.isEmpty(super.getMessage())) {
            return super.getMessage();
        }

        return "";
    }

    /**
     * Check if
     *
     * @param exception
     * @return
     */
    public static boolean isUiRequiredException(Exception exception) {
        return exception instanceof MsalUiRequiredException;
    }
}
