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
import com.microsoft.quickauth.signin.internal.logger.ILogger;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;

/** Encapsulates the parameters passed to the create MSQASignInClient. */
public class MSQASignInOptions {
  /**
   * The resource ID of the raw file containing the JSON configuration for the {@link
   * MSQASignInClient}.
   */
  private final int mConfigResourceId;
  /** Set Enable/Disable the Android logcat logging. */
  private final boolean mEnableLogcatLog;
  /**
   * Configures external logging to configure a callback that the sdk will use to pass each log
   * message.
   */
  private final @Nullable ILogger mExternalLogger;
  /**
   * Set the log level for diagnostic purpose. By default, the sdk enables the verbose level
   * logging.
   */
  private final @LogLevel int mLogLevel;

  public MSQASignInOptions(MSQASignInOptions.Builder builder) {
    mConfigResourceId = builder.mConfigResourceId;
    mEnableLogcatLog = builder.mEnableLogcatLog;
    mExternalLogger = builder.mExternalLogger;
    mLogLevel = builder.mLogLevel;
  }

  /**
   * @return The resource ID of the raw file.
   */
  public int getConfigResourceId() {
    return mConfigResourceId;
  }

  /**
   * @return If true enable logcat logging.
   */
  public boolean isEnableLogcatLog() {
    return mEnableLogcatLog;
  }

  /**
   * @return external logging callback.
   */
  @Nullable
  public ILogger getExternalLogger() {
    return mExternalLogger;
  }

  /**
   * @return The log level in {@link LogLevel}.
   */
  public int getLogLevel() {
    return mLogLevel;
  }

  public static final class Builder {
    private int mConfigResourceId;
    private boolean mEnableLogcatLog;
    private @Nullable ILogger mExternalLogger;
    private @LogLevel int mLogLevel;

    public Builder() {
      mLogLevel = LogLevel.VERBOSE;
    }

    public Builder setConfigResourceId(int configResourceId) {
      mConfigResourceId = configResourceId;
      return this;
    }

    public Builder setEnableLogcatLog(boolean enableLogcatLog) {
      mEnableLogcatLog = enableLogcatLog;
      return this;
    }

    public Builder setExternalLogger(ILogger externalLogger) {
      mExternalLogger = externalLogger;
      return this;
    }

    public Builder setLogLevel(@LogLevel int logLevel) {
      mLogLevel = logLevel;
      return this;
    }

    public MSQASignInOptions build() {
      return new MSQASignInOptions(this);
    }
  }
}
