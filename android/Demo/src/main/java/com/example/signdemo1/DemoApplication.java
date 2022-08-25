package com.example.signdemo1;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.MQASignInOptions;
import com.microsoft.quick.auth.signin.MSQASignIn;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MSQASignIn.init(this, new MQASignInOptions.Builder()
                .setClientId("client id")
                .setRedirectUri("redirect url")
                .build());
        MSQASignIn.setLogLevel(LogLevel.VERBOSE);
        MSQASignIn.setEnableLogcatLog(true);
        MSQASignIn.setExternalLogger(new ILogger() {
            @Override
            public void log(@NonNull int logLevel, @Nullable String message) {
                Log.e("DemoApplication", message);
            }
        });
    }
}
