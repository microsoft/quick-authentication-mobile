package com.microsoft.quick.auth.signin.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.ILoggerCallback;
import com.microsoft.identity.client.Logger;

public class LogUtil {

    private static final String TAG = LogUtil.class.getSimpleName();

    private LogUtil() {
    }

    /**
     * Set the log level for diagnostic purpose. By default, the sdk enables the verbose level
     * logging.
     *
     * @param logLevel The {@link LogLevel} to be enabled for the diagnostic logging.
     */
    public static void setLogLevel(final @LogLevel int logLevel) {
        Logger.getInstance().setLogLevel(adapter(logLevel));
    }

    /**
     * Enable/Disable the Android logcat logging. By default, the sdk enables it.
     *
     * @param enableLogcatLog True if enabling the logcat logging, false otherwise.
     */
    public static void setEnableLogcatLog(final boolean enableLogcatLog) {
        Logger.getInstance().setEnableLogcatLog(enableLogcatLog);
    }

    /**
     * Set the custom logger. Configures external logging to configure a callback that the sdk
     * will use to pass each
     * log message. Overriding the logger callback is not allowed.
     *
     * @param externalLogger The reference to the ILoggerCallback that can output the logs to the
     *                       designated
     *                       places.
     */
    public static void setExternalLogger(final @NonNull ILogger externalLogger) throws IllegalStateException {
        try {
            Logger.getInstance().setExternalLogger(new ILoggerCallback() {
                @Override
                public void log(String tag, Logger.LogLevel logLevel, String message,
                                boolean containsPII) {
                    externalLogger.log(adapter(logLevel), message);
                }
            });

        } catch (Exception e) {
            error(TAG, e.getMessage(), e);
        }
    }

    private static @LogLevel
    int adapter(Logger.LogLevel logLevel) {
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

    private static Logger.LogLevel adapter(@LogLevel int logLevel) {
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

    /**
     * Send a Logger.LogLevel.ERROR log message.
     *
     * @param tag          Used to identify the source of a log message. It usually identifies
     *                     the class or activity
     *                     where the log
     * @param errorMessage The error message to log.
     * @param exception    An exception to log
     */
    public static void error(final String tag, @Nullable final String errorMessage,
                             @Nullable final Throwable exception) {
        com.microsoft.identity.common.logging.Logger.error(tag, errorMessage, exception);
    }

    public static void error(final String tag, @Nullable final String errorMessage) {
        com.microsoft.identity.common.logging.Logger.error(tag, errorMessage, null);
    }

    public static void error(final String tag, @Nullable final Throwable exception) {
        com.microsoft.identity.common.logging.Logger.error(tag, null, exception);
    }

    /**
     * Send a Logger.LogLevel.WARN log message.
     *
     * @param tag     Used to identify the source of a log message. It usually identifies the
     *                class or activity where
     *                the log call occurs.
     * @param message The message to log.
     */
    public static void warn(final String tag, @Nullable final String message) {
        com.microsoft.identity.common.logging.Logger.warn(tag, message);
    }

    /**
     * Send a Logger.LogLevel.INFO log message.
     * message – The message to log.
     *
     * @param tag     Used to identify the source of a log message. It usually identifies the
     *                class or activity where
     *                the log call occurs.
     * @param message The message to log.
     */
    public static void info(final String tag, @Nullable final String message) {
        com.microsoft.identity.common.logging.Logger.info(tag, message);
    }

    /**
     * Send a Logger.LogLevel.VERBOSE log message.
     * message – The message to log.
     *
     * @param tag     Used to identify the source of a log message. It usually identifies the
     *                class or activity where
     *                the log call occurs.
     * @param message The message to log.
     */
    public static void verbose(final String tag, @Nullable final String message) {
        com.microsoft.identity.common.logging.Logger.verbose(tag, message);
    }
}
