package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public abstract class Task<T> {

    public static <T> Task<T> create(@NonNull ConsumerHolder<T> consumerHolder) {
        return new TaskCreate<>(consumerHolder);
    }

    public Disposable start(@NonNull Consumer<? super T> consumer) {
        CancelableConsumer<T> cancelableConsumer = new CancelableConsumer<>(consumer);
        startActual(cancelableConsumer);
        return cancelableConsumer;
    }

    protected abstract void startActual(@NonNull Consumer<? super T> consumer);

    public <R> Task<R> convert(@NonNull Convert<? super T, ? extends R> mapper) {
        return new TaskMap<>(this, mapper);
    }

    public <R> Task<R> taskConvert(@NonNull Convert<? super T, ? extends Task<? extends R>> mapper) {
        return new TaskFlatMap<>(this, mapper);
    }

    public Task<T> errorConvert(@NonNull Convert<? super Exception, ? extends Task<? extends T>> mapper) {
        return new TaskError<>(this, mapper);
    }

    public Task<T> taskScheduleOn(ThreadSwitcher scheduler) {
        return new TaskScheduleOn<>(this, scheduler);
    }

    public Task<T> nextTaskSchedulerOn(ThreadSwitcher scheduler) {
        return new ConsumerScheduleOn<>(this, scheduler);
    }

    public interface ConsumerHolder<T> {
        void start(@NonNull Consumer<? super T> consumer);
    }
}

