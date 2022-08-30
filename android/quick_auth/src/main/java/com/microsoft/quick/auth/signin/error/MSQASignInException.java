package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.internal.MsalUtils;

import java.util.ArrayList;
import java.util.List;

public class MSQASignInException extends Exception {

    private String mErrorCode;
    private final List<Exception> mSuppressedException;

    public MSQASignInException() {
        this(null);
    }

    /**
     * Initiates the detailed error code.
     *
     * @param errorCode The error code contained in the exception.
     */
    public MSQASignInException(final String errorCode) {
        this(errorCode, null);
    }

    /**
     * Initiates the {@link MSQASignInException} with error code and error message.
     *
     * @param errorCode    The error code contained in the exception.
     * @param errorMessage The error message contained in the exception.
     */
    public MSQASignInException(final String errorCode, final String errorMessage) {
        this(errorCode, errorMessage, null);
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
        super(errorMessage, throwable);
        mErrorCode = errorCode;
        this.mSuppressedException = new ArrayList();
    }

    public void addSuppressedException(Exception e) {
        if (e != null) {
            this.mSuppressedException.add(e);
        }
    }

    public List<Exception> getSuppressedException() {
        return this.mSuppressedException;
    }

    /**
     * @return The error code for the exception, could be null. {@link MSQASignInException} is
     * the top level base exception, for the constants value of all the error code.
     */
    public String getErrorCode() {
        return mErrorCode;
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

    public static MSQASignInException create(Exception exception) {
        if (exception instanceof MSQASignInException) return (MSQASignInException) exception;

        MSQASignInException signInException;
        if (exception instanceof MsalException) {
            signInException = new MSQASignInException(((MsalException) exception).getErrorCode(),
                    exception.getMessage());
        } else if (exception instanceof InterruptedException) {
            signInException = new MSQASignInException(MSQAErrorString.INTERRUPTED_ERROR,
                    exception.getMessage());
        } else {
            signInException = new MSQASignInException(MSQAErrorString.UNEXPECTED_ERROR, exception.getMessage());
        }
        signInException.addSuppressedException(exception);
        return signInException;
    }
}
