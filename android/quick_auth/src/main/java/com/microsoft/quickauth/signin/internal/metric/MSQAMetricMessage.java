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
