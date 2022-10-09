package com.azuresamples.quickauth.sign.test;

import androidx.annotation.StringDef;
import com.microsoft.quickauth.signin.internal.entity.MSQASignInScopeInternal;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({MSQASignInScopeInternal.READ, MSQASignInScopeInternal.READ_AND_WRITE})
public @interface MSQATestFlag {
  String SUCCESS = "success";
  String CANCEL = "cancel";
  String ERROR = "error";
}
