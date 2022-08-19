package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonText.SIGN_IN_WITH, ButtonText.SIGN_OUT})
public @interface ButtonText {
    int SIGN_IN_WITH = 0;
    int SIGN_OUT = 1;
}