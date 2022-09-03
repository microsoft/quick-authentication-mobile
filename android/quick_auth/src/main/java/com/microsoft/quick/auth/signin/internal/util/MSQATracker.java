package com.microsoft.quick.auth.signin.internal.util;

import static com.microsoft.quick.auth.signin.logger.LogLevel.ERROR;
import static com.microsoft.quick.auth.signin.logger.LogLevel.INFO;
import static com.microsoft.quick.auth.signin.logger.LogLevel.VERBOSE;
import static com.microsoft.quick.auth.signin.logger.LogLevel.WARN;

import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.MSQALogger;

public class MSQATracker {
  private static final String TAG = "MSQATracker";
  private final @NonNull String mFrom;
  private final long mCurrentTime;

  public MSQATracker(String from) {
    mFrom = from;
    mCurrentTime = System.currentTimeMillis();
  }

  public void track(String tag, @LogLevel int level, String message, Throwable throwable) {
    String generateTag = TAG + "-mFrom=" + mFrom + "," + tag;
    switch (level) {
      case ERROR:
        MSQALogger.getInstance().error(generateTag, message, throwable);
        break;
      case WARN:
        MSQALogger.getInstance().warn(generateTag, message);
        break;
      case INFO:
        MSQALogger.getInstance().info(generateTag, message);
        break;
      case VERBOSE:
        MSQALogger.getInstance().verbose(generateTag, message);
        break;
    }
  }
}
