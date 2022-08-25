package com.microsoft.quick.auth.signin.logger;

import android.util.Log;

import com.microsoft.identity.client.ILoggerCallback;
import com.microsoft.identity.client.Logger;

public class MSQALogCatLogger implements ILoggerCallback {

    @Override
    public void log(String tag, Logger.LogLevel logLevel, String message, boolean containsPII) {
        switch (logLevel) {
            case ERROR:
                Log.e(tag, message);
                break;
            case WARNING:
                Log.w(tag, message);
                break;
            case INFO:
                Log.i(tag, message);
                break;
            case VERBOSE:
                Log.v(tag, message);
                break;
        }
    }
}
