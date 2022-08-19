package com.microsoft.quick.auth.signin;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Task;

class MQAApplicationTask implements Task.OnSubscribe<IAccountClientApplication> {
    private final @NonNull
    IAccountClientHolder mClientHolder;

    public MQAApplicationTask(@NonNull IAccountClientHolder clientHolder) {
        mClientHolder = clientHolder;
    }

    @Override
    public void subscribe(@NonNull Consumer<? super IAccountClientApplication> observer) {
        try {
            observer.onSuccess(mClientHolder.getClientApplication());
        } catch (Exception e) {
            observer.onError(e);
        }
    }

    public static Task<IAccountClientApplication> getApplicationObservable(@NonNull IAccountClientHolder clientHolder) {
        return Task.create(new MQAApplicationTask(clientHolder))
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain())
                .nextConsumerOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}
