package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonTheme.DARK, ButtonTheme.LIGHT})
public @interface ButtonTheme {
  int DARK = 0;
  int LIGHT = 1;
}
