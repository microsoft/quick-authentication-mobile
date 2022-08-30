package com.microsoft.quick.auth.signin.task;

import androidx.annotation.NonNull;

public interface Convert<T, R> {
    /**
     * Convert the input value to other value.
     *
     * @param t the input value
     * @return the output value
     * @throws Exception on error
     */
    R convert(@NonNull T t) throws Exception;
}
