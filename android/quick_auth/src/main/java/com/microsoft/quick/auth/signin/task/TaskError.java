package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskError<T> extends Task<T> {

    private final @NonNull
    Task<T> mSource;
    private final @NonNull
    Convert<? super Exception, ? extends Task<? extends T>> mMapper;


    public TaskError(@NonNull Task<T> source,
                     @NonNull Convert<? super Exception, ? extends Task<? extends T>> mapper) {
        this.mSource = source;
        this.mMapper = mapper;
    }

    @Override
    protected void startActual(@NonNull Consumer<? super T> consumer) {
        ErrorConsumer<T> parent = new ErrorConsumer<>(consumer, mMapper);
        mSource.start(parent);
    }

    static class ErrorConsumer<T> implements Consumer<T> {
        private final @NonNull
        Consumer<? super T> mDownStream;
        private final @NonNull
        Convert<? super Exception, ? extends Task<? extends T>> mMapper;
        private Disposable mDisposable;

        public ErrorConsumer(@NonNull Consumer<? super T> consumer,
                             @NonNull Convert<? super Exception, ? extends Task<? extends T>> mapper) {
            this.mDownStream = consumer;
            this.mMapper = mapper;
        }

        @Override
        public void onSuccess(T t) {
            mDownStream.onSuccess(t);
        }

        @Override
        public void onError(Exception t) {
            try {
                Task<? extends T> observable = mMapper.convert(t);
                mDisposable = observable.start(mDownStream);
            } catch (Exception e) {
                mDownStream.onError(e);
            }
        }

        @Override
        public void onCancel() {
            if (mDisposable != null) {
                mDisposable.dispose();
            }
            mDownStream.onCancel();
        }
    }
}
