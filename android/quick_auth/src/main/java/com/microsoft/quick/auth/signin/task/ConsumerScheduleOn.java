package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public class ConsumerScheduleOn<T> extends Task<T> {

    private final @NonNull
    Task<T> mTask;
    private final @NonNull
    Scheduler mScheduler;

    public ConsumerScheduleOn(@NonNull Task<T> task, @NonNull Scheduler scheduler) {
        this.mTask = task;
        this.mScheduler = scheduler;
    }

    @Override
    protected void subscribeActual(@NonNull Consumer<? super T> consumer) {
        mTask.subscribe(new ObserveOnObserver<>(mScheduler, consumer));
    }

    static final class ObserveOnObserver<T> implements Consumer<T> {

        private final @NonNull
        Scheduler mScheduler;
        private final @NonNull
        Consumer<? super T> mDownStreamConsumer;

        public ObserveOnObserver(@NonNull Scheduler scheduler,
                                 @NonNull Consumer<? super T> consumer) {
            this.mScheduler = scheduler;
            this.mDownStreamConsumer = consumer;
        }

        @Override
        public void onSuccess(final T t) {
            mScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    mDownStreamConsumer.onSuccess(t);
                }
            });
        }

        @Override
        public void onError(final Exception t) {
            mScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    mDownStreamConsumer.onError(t);
                }
            });
        }

        @Override
        public void onCancel() {
            mDownStreamConsumer.onCancel();
        }
    }
}

