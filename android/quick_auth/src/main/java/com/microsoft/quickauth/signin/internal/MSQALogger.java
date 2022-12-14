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
package com.microsoft.quickauth.signin.internal;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.ILoggerCallback;
import com.microsoft.identity.client.Logger;
import com.microsoft.identity.common.java.util.StringUtil;
import com.microsoft.identity.common.java.util.ThrowableUtil;
import com.microsoft.quickauth.signin.logger.ILogger;
import com.microsoft.quickauth.signin.logger.LogLevel;
import java.util.concurrent.atomic.AtomicBoolean;

public class MSQALogger {
  private final MSQALogCatLogger MSQALogCatLogger;
  private ILogger mExternalLogger;
  private boolean mEnableLogcatLog;
  private @LogLevel int mLogLevel;
  private final AtomicBoolean mIsInitialized;

  private MSQALogger() {
    MSQALogCatLogger = new MSQALogCatLogger();
    mLogLevel = LogLevel.VERBOSE;
    mIsInitialized = new AtomicBoolean(false);
  }

  private static class SingletonHolder {
    private static final MSQALogger sInstance = new MSQALogger();
  }

  public static MSQALogger getInstance() {
    return MSQALogger.SingletonHolder.sInstance;
  }

  private final ILoggerCallback innerLogger =
      new ILoggerCallback() {
        @Override
        public void log(String tag, Logger.LogLevel logLevel, String message, boolean containsPII) {
          int level = adapter(logLevel);
          if (mLogLevel > level) return;
          if (mEnableLogcatLog) {
            MSQALogCatLogger.log(tag, logLevel, message, containsPII);
          }
          if (mExternalLogger != null) mExternalLogger.log(level, message);
        }
      };

  public void init(Context context) {
    if (mIsInitialized.get()) return;
    mIsInitialized.set(true);
    // Disable Sdk android logcat log by default.
    Logger.getInstance().setEnableLogcatLog(false);
    Logger.getInstance().setEnablePII(false);
    Logger.getInstance().setLogLevel(Logger.LogLevel.VERBOSE);
    try {
      Logger.getInstance().setExternalLogger(innerLogger);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setEnableLogcatLog(final boolean enableLogcatLog) {
    mEnableLogcatLog = enableLogcatLog;
  }

  public void setExternalLogger(final @NonNull ILogger externalLogger) {
    mExternalLogger = externalLogger;
  }

  public void setLogLevel(final @LogLevel int logLevel) {
    mLogLevel = logLevel;
  }

  private @LogLevel int adapter(Logger.LogLevel logLevel) {
    switch (logLevel) {
      case VERBOSE:
        return LogLevel.VERBOSE;
      case INFO:
        return LogLevel.INFO;
      case WARNING:
        return LogLevel.WARN;
      case ERROR:
        return LogLevel.ERROR;
      default:
        return LogLevel.VERBOSE;
    }
  }

  private Logger.LogLevel adapter(@LogLevel int logLevel) {
    switch (logLevel) {
      case LogLevel.VERBOSE:
        return Logger.LogLevel.VERBOSE;
      case LogLevel.INFO:
        return Logger.LogLevel.INFO;
      case LogLevel.WARN:
        return Logger.LogLevel.WARNING;
      case LogLevel.ERROR:
        return Logger.LogLevel.ERROR;
      default:
        return Logger.LogLevel.VERBOSE;
    }
  }

  public void error(
      final String tag, @Nullable final String errorMessage, @Nullable final Throwable exception) {
    innerLogger.log(tag, Logger.LogLevel.ERROR, getErrorMsg(errorMessage, exception), false);
  }

  public void warn(final String tag, @Nullable final String message) {
    innerLogger.log(tag, Logger.LogLevel.WARNING, message, false);
  }

  public void info(final String tag, @Nullable final String message) {
    innerLogger.log(tag, Logger.LogLevel.INFO, message, false);
  }

  public void verbose(final String tag, @Nullable final String message) {
    innerLogger.log(tag, Logger.LogLevel.VERBOSE, message, false);
  }

  private String getErrorMsg(String message, Throwable throwable) {
    String logMessage = StringUtil.isNullOrEmpty(message) ? "N/A" : message;
    return logMessage
        + (throwable == null ? "" : '\n' + ThrowableUtil.getStackTraceAsString(throwable));
  }
}
