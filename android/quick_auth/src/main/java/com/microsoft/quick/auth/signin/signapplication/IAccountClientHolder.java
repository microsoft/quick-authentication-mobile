package com.microsoft.quick.auth.signin.signapplication;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MQASignInInnerConfig;

public interface IAccountClientHolder {
    IAccountClientApplication getClientApplication() throws Exception;

    @NonNull
    MQASignInInnerConfig getOptions();
}
