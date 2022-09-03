package com.microsoft.quick.auth.signin.internal.http;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
  MSQAHttpMethod.GET,
  MSQAHttpMethod.HEAD,
  MSQAHttpMethod.PUT,
  MSQAHttpMethod.POST,
  MSQAHttpMethod.OPTIONS,
  MSQAHttpMethod.PATCH,
  MSQAHttpMethod.DELETE,
  MSQAHttpMethod.TRACE
})
public @interface MSQAHttpMethod {
  String GET = "GET";
  String HEAD = "HEAD";
  String PUT = "PUT";
  String POST = "POST";
  String OPTIONS = "OPTIONS";
  String PATCH = "PATCH";
  String DELETE = "DELETE";
  String TRACE = "TRACE";
}
