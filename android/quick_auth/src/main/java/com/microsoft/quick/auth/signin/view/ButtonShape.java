package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonShape.RECTANGULAR, ButtonShape.PILL, ButtonShape.ROUNDED})
public @interface ButtonShape {
    int RECTANGULAR = 0;
    int PILL = 1;
    int ROUNDED = 2;
}