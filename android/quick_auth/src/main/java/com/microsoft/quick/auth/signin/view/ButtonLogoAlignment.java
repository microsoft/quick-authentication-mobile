package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonLogoAlignment.LEFT, ButtonLogoAlignment.CENTER})
public @interface ButtonLogoAlignment {
    int LEFT = 0;
    int CENTER = 1;
}
