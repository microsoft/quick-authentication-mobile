package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskMap<T, R> extends Task<R> {

    private final @NonNull
    Task<T> mSource;
    private final @NonNull
    Function<? super T, ? extends R> mMapper;

    public TaskMap(@NonNull Task<T> source, @NonNull Function<? super T, ? extends R> mapper) {
        this.mSource = source;
        this.mMapper = mapper;
    }

    @Override
    protected void subscribeActual(@NonNull Consumer<? super R> consumer) {
        MapConsumer<T, R> parent = new MapConsumer<>(consumer, mMapper);
        mSource.subscribe(parent);
    }

    static class MapConsumer<T, R> implements Consumer<T> {

        private final @NonNull
        Consumer<? super R> mDownStream;
        private final @NonNull
        Function<? super T, ? extends R> mMapper;

        public MapConsumer(@NonNull Consumer<? super R> observer, @NonNull Function<? super T, ?
                extends R> mapper) {
            this.mDownStream = observer;
            this.mMapper = mapper;
        }

        @Override
        public void onSuccess(T t) {
            R r;
            try {
                r = mMapper.apply(t);
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

