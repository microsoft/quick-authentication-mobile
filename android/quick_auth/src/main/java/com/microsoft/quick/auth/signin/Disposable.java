package com.microsoft.quick.auth.signin;

public interface Disposable {
    /**
     * Cancel this request and release resource.
     */
    void dispose();

    /**
     * @return true if this resource has been disposed.
     */
    boolean isDisposed();
}
