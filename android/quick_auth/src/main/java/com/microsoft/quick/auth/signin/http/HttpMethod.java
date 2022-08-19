package com.microsoft.quick.auth.signin.http;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({HttpMethod.GET, HttpMethod.HEAD, HttpMethod.PUT, HttpMethod.POST, HttpMethod.OPTIONS,
        HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.TRACE})
public @interface HttpMethod {
    String GET = "GET";
    String HEAD = "HEAD";
    String PUT = "PUT";
    String POST = "POST";
    String OPTIONS = "OPTIONS";
    String PATCH = "PATCH";
    String DELETE = "DELETE";
    String TRACE = "TRACE";
}
