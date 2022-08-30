package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskFlatMap<T, R> extends Task<R> {

    private final @NonNull
    Task<T> mSource;
    private final @NonNull
    Convert<? super T, ? extends Task<? extends R>> mConverter;

    public TaskFlatMap(@NonNull Task<T> source, @NonNull Convert<? super T, ? extends Task<?
            extends R>> converter) {
        this.mSource = source;
        this.mConverter = converter;
    }

    @Override
    protected void startActual(@NonNull Consumer<? super R> consumer) {
        MapConsumer<T, R> parent = new MapConsumer<>(consumer, mConverter);
        mSource.start(parent);
    }

    static class MapConsumer<T, R> implements Consumer<T> {
        private final @NonNull
        Consumer<? super R> mDownStream;
        private final @NonNull
        Convert<? super T, ? extends Task<? extends R>> mConverter;
        private Disposable mDisposable;

        public MapConsumer(@NonNull Consumer<? super R> consumer, @NonNull Convert<? super T, ?
                extends Task<?
                extends R>> convert) {
            this.mDownStream = consumer;
            this.mConverter = convert;
        }

        @Override
        public void onSuccess(T t) {
            try {
                Task<? extends R> task = mConverter.convert(t);
                mDisposable = task.start(mDownStream);
            } catch (Exception e) {
                onError(e);
            }
        }

        @Override
        public void onError(Exception t) {
            mDownStream.onError(t);
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
