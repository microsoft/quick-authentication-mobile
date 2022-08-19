package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskCreate<T> extends Task<T> {

    private final @NonNull
    OnSubscribe<T> mSource;

    public TaskCreate(@NonNull OnSubscribe<T> source) {
        this.mSource = source;
    }

    @Override
    protected void subscribeActual(@NonNull Consumer<? super T> consumer) {
        CreateObserver<T> parent = new CreateObserver<>(consumer);
        mSource.subscribe(parent);
    }

    static class CreateObserver<T> implements Consumer<T> {

        private final @NonNull
        Consumer<? super T> mDownStream;

        public CreateObserver(@NonNull Consumer<? super T> consumer) {
            mDownStream = consumer;
        }

        @Override
        public void onSuccess(T t) {
            mDownStream.onSuccess(t);
        }

        @Override
        public void onError(Exception t) {
            mDownStream.onError(t);
        }

        @Override
        public void onCancel() {
            mDownStream.onCancel();
        }
    }
}

