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
package com.microsoft.quickauth.signin.internal.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quickauth.signin.MSQATokenResult;
import java.util.Date;
import java.util.UUID;

public class MSQATokenResultInternal implements MSQATokenResult {

  private final @NonNull IAuthenticationResult mAuthenticationResult;

  public MSQATokenResultInternal(@NonNull IAuthenticationResult authenticationResult) {
    mAuthenticationResult = authenticationResult;
  }

  @NonNull
  @Override
  public String getAccessToken() {
    return mAuthenticationResult.getAccessToken();
  }

  @NonNull
  @Override
  public String getAuthorizationHeader() {
    return mAuthenticationResult.getAuthorizationHeader();
  }

  @NonNull
  @Override
  public String getAuthenticationScheme() {
    return mAuthenticationResult.getAuthenticationScheme();
  }

  @NonNull
  @Override
  public Date getExpiresOn() {
    return mAuthenticationResult.getExpiresOn();
  }

  @Nullable
  @Override
  public String getTenantId() {
    return mAuthenticationResult.getTenantId();
  }

  @NonNull
  @Override
  public String[] getScope() {
    return mAuthenticationResult.getScope();
  }

  @Nullable
  @Override
  public UUID getCorrelationId() {
    return mAuthenticationResult.getCorrelationId();
  }
}
