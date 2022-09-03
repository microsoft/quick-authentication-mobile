package com.microsoft.quick.auth.signin.error;

import com.microsoft.identity.client.exception.MsalClientException;

public class MSQAErrorString {
  /** No initialize sdk. */
  public static final String NO_INITIALIZE = "no_initialize";

  public static final String NO_INITIALIZE_MESSAGE =
      "Haven't initialize SDK, please initialize" + " first with MSQASignInClient class";

  /** No account currently signed in to SingleAccountPublicClientApplication */
  public static final String NO_CURRENT_ACCOUNT = MsalClientException.NO_CURRENT_ACCOUNT;

  public static final String NO_CURRENT_ACCOUNT_ERROR_MESSAGE =
      MsalClientException.NO_CURRENT_ACCOUNT_ERROR_MESSAGE;

  /** Thread interrupted error */
  public static final String INTERRUPTED_ERROR = "interrupted_error";

  /** Http request error */
  public static final String HTTP_REQUEST_ERROR = "http_request_error";

  /** Http account info request error */
  public static final String HTTP_ACCOUNT_REQUEST_ERROR = "http_account_request_error";

  public static final String HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE =
      "Account info request " + "error.";

  /** User cancel error */
  public static final String USER_CANCEL_ERROR = "user_cancel_error";

  public static final String USER_CANCEL_ERROR_MESSAGE =
      "User canceled the authentication session.";

  /** Unknown error. */
  public static final String UNKNOWN_ERROR = MsalClientException.UNKNOWN_ERROR;
}
