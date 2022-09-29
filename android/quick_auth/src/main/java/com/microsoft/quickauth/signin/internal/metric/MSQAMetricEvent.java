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
