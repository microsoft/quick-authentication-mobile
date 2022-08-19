package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInError;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInErrorHelper;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Function;

public class CurrentAccountConsumer implements Function<IAccountClientApplication, IAccount> {

    @Override
    public IAccount apply(@NonNull IAccountClientApplication iAccountClientApplication) throws Exception {
        IAccount currentAccount = iAccountClientApplication.getCurrentAccount();
        if (currentAccount != null) {
            return currentAccount;
        } else {
            throw new MicrosoftSignInError(MicrosoftSignInErrorHelper.NO_CURRENT_ACCOUNT,
                    MicrosoftSignInErrorHelper.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
        }
    }
}
