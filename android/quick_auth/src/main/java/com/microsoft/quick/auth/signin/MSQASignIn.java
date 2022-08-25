package com.microsoft.quick.auth.signin;

import android.content.Context;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.MSQALogger;

public class MSQASignIn {

    /**
     * @param options A config options which sdk initialize needed.
     */
    public static void init(Context context, MQASignInOptions options) {

    }

    /**
     * Create a new instance of GoogleSignInClient.
     *
     * @param context A Context used to provide information about the application's environment.
     * @return
     */
    public static MSQASignInClient getSignInClient(Context context) {
        return new MSQASignInClientImp(context);
    }

    /**
     * Set the log level for diagnostic purpose. By default, the sdk enables the verbose level
     * logging.
     *
     * @param logLevel The {@link LogLevel} to be enabled for the diagnostic logging.
     */
    public static void setLogLevel(final @LogLevel int logLevel) {
        MSQALogger.getInstance().setLogLevel(logLevel);
    }

    /**
     * Enable/Disable the Android logcat logging. By default, the sdk enables it.
     *
     * @param enableLogcatLog True if enabling the logcat logging, false otherwise.
     */
    public static void setEnableLogcatLog(final boolean enableLogcatLog) {
        MSQALogger.getInstance().setEnableLogcatLog(enableLogcatLog);
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
    public static void setExternalLogger(final @NonNull ILogger externalLogger) {
        MSQALogger.getInstance().setExternalLogger(externalLogger);
    }
}
