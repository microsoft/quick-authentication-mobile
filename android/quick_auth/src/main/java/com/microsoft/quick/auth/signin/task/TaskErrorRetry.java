package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.Disposable;

public class TaskErrorRetry<T> extends Task<T> {

    private final @NonNull
    Task<T> mSource;
    private final @NonNull
    Function<? super Exception, ? extends Task<? extends T>> mMapper;


    public TaskErrorRetry(@NonNull Task<T> source,
                          @NonNull Function<? super Exception, ? extends Task<? extends T>> mapper) {
        this.mSource = source;
        this.mMapper = mapper;
    }

    @Override
    protected void subscribeActual(@NonNull Consumer<? super T> consumer) {
        ErrorConsumer<T> parent = new ErrorConsumer<>(consumer, mMapper);
        mSource.subscribe(parent);
    }

    static class ErrorConsumer<T> implements Consumer<T> {
        private final @NonNull
        Consumer<? super T> mDownStream;
        private final @NonNull
        Function<? super Exception, ? extends Task<? extends T>> mMapper;
        private Disposable mDisposable;

        public ErrorConsumer(@NonNull Consumer<? super T> consumer,
                             @NonNull Function<? super Exception, ? extends Task<? extends T>> mapper) {
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
                Task<? extends T> observable = mMapper.apply(t);
                mDisposable = observable.subscribe(mDownStream);
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
