package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskScheduleOn<T> extends Task<T> {

    private final Task<T> mSource;
    private final ThreadSwitcher mSwitcher;

    public TaskScheduleOn(Task<T> source, ThreadSwitcher switcher) {
        this.mSource = source;
        this.mSwitcher = switcher;
    }

    @Override
    protected void startActual(@NonNull Consumer<? super T> consumer) {
        final TaskScheduleConsumer<? super T> parent = new TaskScheduleConsumer<>(consumer);
        mSwitcher.schedule(new Runnable() {
            @Override
            public void run() {
                mSource.start(parent);
            }
        });
    }

    static class TaskScheduleConsumer<T> implements Consumer<T> {

        private final @NonNull
        Consumer<? super T> mDownStreamConsumer;

        public TaskScheduleConsumer(@NonNull Consumer<? super T> consumer) {
            this.mDownStreamConsumer = consumer;
        }

        @Override
        public void onSuccess(T t) {
            mDownStreamConsumer.onSuccess(t);
        }

        @Override
        public void onError(Exception t) {
            mDownStreamConsumer.onError(t);
        }

        @Override
        public void onCancel() {
            mDownStreamConsumer.onCancel();
        }
    }
}

