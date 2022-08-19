package com.microsoft.quick.auth.signin.signapplicationclient;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MQASignInOptions;

public interface IAccountClientHolder {
    IAccountClientApplication getClientApplication() throws Exception;

    @NonNull
    MQASignInOptions getOptions();
}
