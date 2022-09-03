package com.microsoft.quick.auth.signin.view;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
  ButtonText.SIGN_IN_WITH,
  ButtonText.SIGNUP_WITH,
  ButtonText.SIGNIN,
  ButtonText.CONTINUE_WITH
})
public @interface ButtonText {
  int SIGN_IN_WITH = 0;
  int SIGNUP_WITH = 1;
  int SIGNIN = 2;
  int CONTINUE_WITH = 3;
}
