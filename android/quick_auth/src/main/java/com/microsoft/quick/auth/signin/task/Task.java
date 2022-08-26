package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public abstract class Task<T> {

    public static <T> Task<T> create(@NonNull OnSubscribe<T> onSubscribe) {
        return new TaskCreate<>(onSubscribe);
    }

    public Disposable subscribe(@NonNull Consumer<? super T> consumer) {
        CancelableConsumer<T> cancelableConsumer = new CancelableConsumer<>(consumer);
        subscribeActual(cancelableConsumer);
        return cancelableConsumer;
    }

    protected abstract void subscribeActual(@NonNull Consumer<? super T> consumer);

    public <R> Task<R> map(@NonNull Function<? super T, ? extends R> mapper) {
        return new TaskMap<>(this, mapper);
    }

    public <R> Task<R> flatMap(@NonNull Function<? super T, ? extends Task<? extends R>> mapper) {
        return new TaskFlatMap<>(this, mapper);
    }

    public Task<T> errorRetry(@NonNull Function<? super Exception, ? extends Task<? extends T>> mapper) {
        return new TaskErrorRetry<>(this, mapper);
    }


    public Task<T> taskScheduleOn(Scheduler scheduler) {
        return new TaskScheduleOn<>(this, scheduler);
    }

    public Task<T> nextTaskSchedulerOn(Scheduler scheduler) {
        return new ConsumerScheduleOn<>(this, scheduler);
    }

    public interface OnSubscribe<T> {
        void subscribe(@NonNull Consumer<? super T> consumer);
    }
}

