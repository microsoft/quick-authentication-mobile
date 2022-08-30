package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalClientException;

public class MSQAErrorString {
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
}
