package com.example.signdemo1;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.logger.LogUtil;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setLogLevel(LogLevel.VERBOSE);
        LogUtil.setEnableLogcatLog(true);
        LogUtil.setExternalLogger(new ILogger() {
            @Override
            public void log(@NonNull int logLevel, @Nullable String message) {
                Log.e("DemoApplication",message);
            }
        });
    }
}
