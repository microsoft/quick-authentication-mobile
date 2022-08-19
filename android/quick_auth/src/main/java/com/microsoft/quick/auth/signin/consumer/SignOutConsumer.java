package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Function;

public class SignOutConsumer implements Function<IAccountClientApplication, Boolean> {

    @Override
    public Boolean apply(@NonNull IAccountClientApplication iAccountClientApplication) throws Exception {
        return iAccountClientApplication.signOut(null);
    }
}
