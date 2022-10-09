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
package com.azuresamples.quickauth.sign.test.mock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

// Util class for mocking.
public class MSQATestMockUtil {
  private static final String MOCK_ACCOUNT_INFO_PREFERENCE = "quickauth.signin.test.account";
  private static final String CURRENT_ACCOUNT_KEY = "current_account_key";
  public static final String MOCK_STRING = "mock_string";
  public static final String DEFAULT_TEST_ACCOUNT = "default_test_account";

  // private constructor for Util class.
  private MSQATestMockUtil() {}

  public static void removeAccount(final Context appContext) {
    getAccountSharedPreference(appContext).edit().clear().apply();
  }

  public static String getCurrentAccount(final Context appContext) {
    return getAccountSharedPreference(appContext).getString(CURRENT_ACCOUNT_KEY, null);
  }

  public static void setCurrentAccount(final Context appContext, String account) {
    getAccountSharedPreference(appContext).edit().putString(CURRENT_ACCOUNT_KEY, account).apply();
  }

  static SharedPreferences getAccountSharedPreference(final Context appContext) {
    return appContext.getSharedPreferences(MOCK_ACCOUNT_INFO_PREFERENCE, Activity.MODE_PRIVATE);
  }
}
