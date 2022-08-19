package com.microsoft.quick.auth.signin.logger;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({LogLevel.VERBOSE, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR})
public @interface LogLevel {
    int VERBOSE = 0;
    int INFO = 1;
    int WARN = 2;
    int ERROR = 3;
}