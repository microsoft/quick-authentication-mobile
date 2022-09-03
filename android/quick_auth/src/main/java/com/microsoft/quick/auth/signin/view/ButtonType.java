package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ButtonType.STANDARD, ButtonType.ICON})
public @interface ButtonType {
  int STANDARD = 0;
  int ICON = 1;
}
