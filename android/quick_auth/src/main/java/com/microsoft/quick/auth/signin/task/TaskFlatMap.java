package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.Disposable;

public class TaskFlatMap<T, R> extends Task<R> {

    private final @NonNull
    Task<T> mSource;
    private final @NonNull
    Function<? super T, ? extends Task<? extends R>> mMapper;

    public TaskFlatMap(@NonNull Task<T> source, @NonNull Function<? super T, ? extends Task<?
            extends R>> mapper) {
        this.mSource = source;
        this.mMapper = mapper;
    }

    @Override
    protected void subscribeActual(@NonNull Consumer<? super R> consumer) {
        MapObserver<T, R> parent = new MapObserver<>(consumer, mMapper);
        mSource.subscribe(parent);
    }

    static class MapObserver<T, R> implements Consumer<T> {
        private final @NonNull
        Consumer<? super R> mDownStream;
        private final @NonNull
        Function<? super T, ? extends Task<? extends R>> mMapper;
        private Disposable mDisposable;

        public MapObserver(@NonNull Consumer<? super R> observer, @NonNull Function<? super T, ?
                extends Task<?
                extends R>> mapper) {
            this.mDownStream = observer;
            this.mMapper = mapper;
        }

        @Override
        public void onSuccess(T t) {
            try {
                Task<? extends R> observable = mMapper.apply(t);
                mDisposable = observable.subscribe(mDownStream);
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
