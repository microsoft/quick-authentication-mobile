package com.microsoft.quick.auth.signin.signapplication;

import android.content.Context;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MQASignInInnerConfig;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.error.MSQASignInErrorHelper;
import com.microsoft.quick.auth.signin.util.FileUtil;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;
import com.microsoft.quick.auth.signin.logger.LogUtil;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class MultipleApplicationHolder implements IAccountClientHolder {
    private volatile IAccountClientApplication mClient;
    private volatile MSQASignInError mClientError;
    private final @NonNull
    MQASignInInnerConfig mOptions;
    private volatile CountDownLatch mCountDownLatch;
    private static final String TAG = MultipleApplicationHolder.class.getSimpleName();
    private final String MULTIPLE_CONFIG_FILE_NAME = "multiple_config_account.json";

    public MultipleApplicationHolder(final Context context,
                                     @NonNull final MQASignInInnerConfig options) {
        mOptions = options;
        mCountDownLatch = new CountDownLatch(1);
        TaskExecutorUtil.io().execute(new Runnable() {
            @Override
            public void run() {
                // Rewrite account config with clientId and redirectUri
                File configFile = FileUtil.reWriteConfig(context, MULTIPLE_CONFIG_FILE_NAME,
                        options);
                try {
                    IMultipleAccountPublicClientApplication application =
                            PublicClientApplication.createMultipleAccountPublicClientApplication(context.getApplicationContext(), configFile);
                    mClient = new MultipleAccountClientApplication(application);
                    mCountDownLatch.countDown();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    if (exception instanceof MsalException) {
                        mClientError =
                                new MSQASignInError(((MsalException) exception).getErrorCode(),
                                        exception.getMessage());
                    } else {
                        mClientError =
                                new MSQASignInError(MSQASignInErrorHelper.INTERRUPTED_ERROR,
                                        exception.getMessage());
                    }
                    // Remove config file
                    FileUtil.removeSignInConfigFile(context, MULTIPLE_CONFIG_FILE_NAME);
                    mCountDownLatch.countDown();
                    LogUtil.error(TAG, "multiple application create error", exception);
                }
            }
        });

    }

    @Override
    public IAccountClientApplication getClientApplication() throws Exception {
        this.mCountDownLatch.await();
        if (null != mClientError) {
            throw mClientError;
        } else {
            return mClient;
        }
    }

    @Override
    @NonNull
    public MQASignInInnerConfig getOptions() {
        return mOptions;
    }
}
