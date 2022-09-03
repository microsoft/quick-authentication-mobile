package com.microsoft.quick.auth.signin.internal.entity;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({MSQASignInScope.READ, MSQASignInScope.READ_AND_WRITE})
public @interface MSQASignInScope {
  String READ = "user.read"; // Read only scope.
  String READ_AND_WRITE = "user.readwrite"; // Read and Write scope.
}
