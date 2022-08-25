package com.microsoft.quick.auth.signin.error;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;

public class MSQASignInErrorHelper {
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

    public static final String HTTP_ACCOUNT_REQUEST_ERROR = "http_account_request_error";
    public static final String HTTP_ACCOUNT_PHOTO_REQUEST_ERROR =
            "http_account_photo_request_error";
    public static final String HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE = "Account info request " +
            "error.";

    public static final String UNSUPPORTED_ERROR = "unsupported_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    public static MSQASignInError convertToSignError(@NonNull Exception e) {
        MSQASignInError error;
        if (e instanceof MsalException) {
            error = new MSQASignInError(((MsalException) e).getErrorCode(), e.getMessage());
        } else if (e instanceof InterruptedException) {
            error = new MSQASignInError(INTERRUPTED_ERROR, e.getMessage());
        } else {
            error = new MSQASignInError(UNEXPECTED_ERROR, e.getMessage());
        }
        error.addSuppressedException(e);
        return error;
    }

    public static <T> T convertToSignError(ErrorConvertFunction<T> function) throws MSQASignInError {
        try {
            return function.run();
        } catch (MSQASignInError e) {
            throw e;
        } catch (MsalException e) {
            throw convertToSignError(e);
        } catch (InterruptedException e) {
            throw convertToSignError(e);
        } catch (Exception e) {
            throw convertToSignError(e);
        }
    }
}
