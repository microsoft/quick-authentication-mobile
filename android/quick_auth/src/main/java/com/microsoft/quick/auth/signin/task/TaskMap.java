package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskMap<T, R> extends Task<R> {

    private final @NonNull
    Task<T> mSource;
    private final @NonNull
    Convert<? super T, ? extends R> mConverter;

    public TaskMap(@NonNull Task<T> source, @NonNull Convert<? super T, ? extends R> convert) {
        this.mSource = source;
        this.mConverter = convert;
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
        Convert<? super T, ? extends R> mConverter;

        public MapConsumer(@NonNull Consumer<? super R> consumer, @NonNull Convert<? super T, ?
                extends R> convert) {
            this.mDownStream = consumer;
            this.mConverter = convert;
        }

        @Override
        public void onSuccess(T t) {
            R r;
            try {
                r = mConverter.convert(t);
            } catch (Exception e) {
                onError(e);
                return;
            }
            mDownStream.onSuccess(r);
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

