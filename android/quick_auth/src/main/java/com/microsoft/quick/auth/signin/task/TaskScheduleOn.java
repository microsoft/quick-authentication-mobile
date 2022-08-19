package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class TaskScheduleOn<T> extends Task<T> {

    private final Task<T> mSource;
    private final Scheduler mScheduler;

    public TaskScheduleOn(Task<T> source, Scheduler scheduler) {
        this.mSource = source;
        this.mScheduler = scheduler;
    }

    @Override
    protected void subscribeActual(@NonNull Consumer<? super T> consumer) {
        final TaskScheduleConsumer<? super T> parent = new TaskScheduleConsumer<>(consumer);
        mScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                mSource.subscribe(parent);
            }
        });
    }

    static class TaskScheduleConsumer<T> implements Consumer<T> {

        private final @NonNull
        Consumer<? super T> mDownStreamConsumer;

        public TaskScheduleConsumer(@NonNull Consumer<? super T> observer) {
            this.mDownStreamConsumer = observer;
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

