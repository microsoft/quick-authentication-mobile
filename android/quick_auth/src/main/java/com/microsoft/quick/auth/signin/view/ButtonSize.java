package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonSize.LARGE, ButtonSize.MEDIUM, ButtonSize.SMALL})
public @interface ButtonSize {
  int LARGE = 0;
  int MEDIUM = 1;
  int SMALL = 2;
}
