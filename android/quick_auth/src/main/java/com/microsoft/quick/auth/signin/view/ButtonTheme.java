package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonTheme.FILLED_BLACK, ButtonTheme.FILLED_BLUE, ButtonTheme.OUTLINE})
public @interface ButtonTheme {
    int FILLED_BLACK = 0;
    int FILLED_BLUE = 1;
    int OUTLINE = 2;
}