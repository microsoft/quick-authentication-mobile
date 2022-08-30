package com.microsoft.quick.auth.signin.signinclient;

import androidx.annotation.WorkerThread;

public interface ISignInClientHolder {
    ISignInClientApplication getClientApplication() throws Exception;

    boolean isInitializeSuccess();
}
