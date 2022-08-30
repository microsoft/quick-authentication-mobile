package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.internal.MsalUtils;
import com.microsoft.identity.common.java.exception.BaseException;

public class MSQASignInException extends BaseException {

    public MSQASignInException() {
        super();
    }

    /**
     * Initiates the detailed error code.
     *
     * @param errorCode The error code contained in the exception.
     */
    public MSQASignInException(final String errorCode) {
        super(errorCode);
    }

    /**
     * Initiates the {@link MSQASignInException} with error code and error message.
     *
     * @param errorCode    The error code contained in the exception.
     * @param errorMessage The error message contained in the exception.
     */
    public MSQASignInException(final String errorCode, final String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Initiates the {@link MSQASignInException} with error code, error message and throwable.
     *
     * @param errorCode    The error code contained in the exception.
     * @param errorMessage The error message contained in the exception.
     * @param throwable    The {@link Throwable} contains the cause for the exception.
     */
    public MSQASignInException(final String errorCode, final String errorMessage,
                               final Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }

    /**
     * @return The error code for the exception, could be null. {@link MSQASignInException} is
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
}
