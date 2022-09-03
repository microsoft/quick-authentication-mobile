package com.microsoft.quick.auth.signin.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface for apps to configure the external logging and implement the callback to designate the
 * output of the log messages.
 */
public interface ILogger {
  /**
   * Interface method for apps to hand off each log message as it's generated.
   *
   * @param logLevel The Logger.LogLevel for the generated message.
   * @param message The detailed message.
   */
  void log(@NonNull @LogLevel int logLevel, @Nullable String message);
}
