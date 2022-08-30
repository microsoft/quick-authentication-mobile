package com.microsoft.quick.auth.signin.signinclient;

public interface ISignInClientHolder {
    ISignInClientApplication getClientApplication() throws Exception;

    boolean isInitializeSuccess();
}
