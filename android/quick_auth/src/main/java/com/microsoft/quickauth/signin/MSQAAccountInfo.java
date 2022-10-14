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
package com.microsoft.quickauth.signin;

import androidx.annotation.Nullable;

public interface MSQAAccountInfo {
  /**
   * @return User full name.
   */
  @Nullable
  String getFullName();

  /**
   * @return Email address or phone.
   */
  @Nullable
  String getUserName();

  /**
   * @return User given name.
   */
  @Nullable
  String getGivenName();

  /**
   * @return User surname.
   */
  @Nullable
  String getSurname();

  /**
   * @return User email.
   */
  @Nullable
  String getEmail();

  /**
   * @return CID for MSA.
   */
  @Nullable
  String getId();

  /**
   * @return MSA account id token.
   */
  @Nullable
  String getIdToken();

  /**
   * @return User photo Base64 string data.
   */
  @Nullable
  String getBase64Photo();
}
