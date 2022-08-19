package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public interface Function<T, R> {
    /**
     * Apply some calculation to the input value and return some other value.
     *
     * @param t the input value
     * @return the output value
     * @throws Exception on error
     */
    R apply(@NonNull T t) throws Exception;
}
