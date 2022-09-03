package com.microsoft.quick.auth.signin;

import androidx.annotation.Nullable;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class MSQASignInOptions {
  private final int mConfigResourceId;
  private final boolean mEnableLogcatLog;
  private final @Nullable ILogger mExternalLogger;
  private final @LogLevel int mLogLevel;

  public MSQASignInOptions(MSQASignInOptions.Builder builder) {
    mConfigResourceId = builder.mConfigResourceId;
    mEnableLogcatLog = builder.mEnableLogcatLog;
    mExternalLogger = builder.mExternalLogger;
    mLogLevel = builder.mLogLevel;
  }

  public int getConfigResourceId() {
    return mConfigResourceId;
  }

  public boolean isEnableLogcatLog() {
    return mEnableLogcatLog;
  }

  @Nullable
  public ILogger getExternalLogger() {
    return mExternalLogger;
  }

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
