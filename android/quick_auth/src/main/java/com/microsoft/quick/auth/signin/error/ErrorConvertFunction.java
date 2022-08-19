package com.microsoft.quick.auth.signin.error;

public interface ErrorConvertFunction<T> {
    T run() throws Exception;
}
