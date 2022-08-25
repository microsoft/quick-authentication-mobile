package com.microsoft.quick.auth.signin.entity;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({MSQAAccountMode.SINGLE, MSQAAccountMode.MULTIPLE})
public @interface MSQAAccountMode {
    String SINGLE = "SINGLE";
    String MULTIPLE = "SINGLE";
}
