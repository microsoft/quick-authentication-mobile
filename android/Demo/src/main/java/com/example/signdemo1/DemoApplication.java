package com.example.signdemo1;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.MQASignInOptions;
import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.logger.ILogger;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MSQASignInClient.sharedInstance().setSignInOptions(this, new MQASignInOptions.Builder()
                .setConfigResourceId(R.raw.auth_config_single_account)
                .setEnableLogcatLog(true)
                .setLogLevel(LogLevel.VERBOSE)
                .setExternalLogger((logLevel, message) -> {
                    // get log message in this
                })
                .build());
    }
}
