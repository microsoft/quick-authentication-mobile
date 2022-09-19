package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
  MSQAMetricMessage.START_SIGN_IN_API,
  MSQAMetricMessage.SIGN_IN_BUTTON,
  MSQAMetricMessage.SUCCESS,
  MSQAMetricMessage.CANCELED,
  MSQAMetricMessage.FAILURE,
  MSQAMetricMessage.NO_SCOPES,
  MSQAMetricMessage.NO_ACCOUNT_PRESENT
})
public @interface MSQAMetricMessage {
  String START_SIGN_IN_API = "start-signin-api";
  String SIGN_IN_BUTTON = "sign-in-button";
  String SUCCESS = "success";
  String CANCELED = "canceled";
  String FAILURE = "failure";
  String NO_SCOPES = "no-scopes";
  String NO_ACCOUNT_PRESENT = "no-account-present";
}
