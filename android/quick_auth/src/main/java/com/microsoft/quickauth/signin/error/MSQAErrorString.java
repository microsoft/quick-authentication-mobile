//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.error;

import com.microsoft.identity.client.exception.MsalClientException;

public class MSQAErrorString {
  /** Configuration file does not exist. */
  public static final String NO_CONFIGURATION_FILE_ERROR = "no_configuration_file_error";

  public static final String NO_CONFIGURATION_FILE_ERROR_MESSAGE =
      "Configuration file does not exist.";

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
