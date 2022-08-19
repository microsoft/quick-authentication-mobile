package com.microsoft.quick.auth.signin.entity;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({MQASignInScope.READ, MQASignInScope.READ_AND_WRITE})
public @interface MQASignInScope {
    String READ = "user.read";// Read only scope.
    String READ_AND_WRITE = "user.readwrite";// Read and Write scope.
}
