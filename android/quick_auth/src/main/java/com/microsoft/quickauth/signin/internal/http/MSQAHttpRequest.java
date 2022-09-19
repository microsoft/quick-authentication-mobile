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
package com.microsoft.quickauth.signin.internal.http;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

public class MSQAHttpRequest {

  private final String mUrl;
  private @NonNull final Map<String, String> mHeader;
  private @NonNull final Map<String, Object> mParams;
  private final int mConnectTimeout;
  private final int mReadTimeout;

  public MSQAHttpRequest(Builder builder) {
    mUrl = builder.mUrl;
    mHeader = builder.mHeader;
    mParams = builder.mParams;
    mConnectTimeout = builder.mConnectTimeout;
    mReadTimeout = builder.mReadTimeout;
  }

  public String getUrl() {
    return mUrl;
  }

  @NonNull
  public Map<String, String> getHeader() {
    return mHeader;
  }

  public Map<String, Object> getParams() {
    return mParams;
  }

  public int getConnectTimeout() {
    return mConnectTimeout;
  }

  public int getReadTimeout() {
    return mReadTimeout;
  }

  public static class Builder {
    private String mUrl;
    private @NonNull final Map<String, String> mHeader;
    private @NonNull Map<String, Object> mParams;
    private int mConnectTimeout;
    private int mReadTimeout;

    public Builder() {
      mHeader = new HashMap<>();
      mParams = new HashMap<>();
      mConnectTimeout = MSQAAPIConstant.CONNECT_TIMEOUT;
      mReadTimeout = MSQAAPIConstant.READ_TIMEOUT;
    }

    public Builder setUrl(String url) {
      mUrl = url;
      return this;
    }

    public Builder addHeader(@NonNull String key, @NonNull String value) {
      mHeader.put(key, value);
      return this;
    }

    public Builder addParams(@NonNull String key, @NonNull String value) {
      mParams.put(key, value);
      return this;
    }

    public Builder setParams(@NonNull Map<String, Object> params) {
      mParams = params;
      return this;
    }

    public Builder setConnectTimeout(int connectTimeout) {
      mConnectTimeout = connectTimeout;
      return this;
    }

    public Builder setReadTimeout(int readTimeout) {
      mReadTimeout = readTimeout;
      return this;
    }

    public MSQAHttpRequest builder() {
      return new MSQAHttpRequest(this);
    }
  }
}
