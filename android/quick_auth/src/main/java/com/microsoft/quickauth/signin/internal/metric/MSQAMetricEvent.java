package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
  MSQAMetricEvent.SIGN_IN_SUCCESS,
  MSQAMetricEvent.SIGN_IN_FAILURE,
  MSQAMetricEvent.BUTTON_SIGN_IN,
  MSQAMetricEvent.SIGN_OUT,
  MSQAMetricEvent.GET_CURRENT_ACCOUNT,
  MSQAMetricEvent.SIGN_IN,
  MSQAMetricEvent.ACQUIRE_TOKEN,
  MSQAMetricEvent.ACQUIRE_TOKEN_SILENT
})
public @interface MSQAMetricEvent {
  String SIGN_IN_SUCCESS = "SignIn.Success";
  String SIGN_IN_FAILURE = "SignIn.Failure";
  String BUTTON_SIGN_IN = "button-sign-in";
  String SIGN_OUT = "signOut";
  String GET_CURRENT_ACCOUNT = "getCurrentAccount";
  String SIGN_IN = "signIn";
  String ACQUIRE_TOKEN = "acquireToken";
  String ACQUIRE_TOKEN_SILENT = "acquireTokenSilent";
}
