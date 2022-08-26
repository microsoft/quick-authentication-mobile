package com.microsoft.quick.auth.signin.signinclient;

import android.content.Context;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

import java.util.concurrent.CountDownLatch;

public class SingleApplicationHolder implements ISignInClientHolder {
    private volatile ISignInClientApplication mClient;
    private volatile MSQASignInError mClientError;
    private final CountDownLatch mCountDownLatch;
    private static final String TAG = SingleApplicationHolder.class.getSimpleName();

    public SingleApplicationHolder(@NonNull final Context context,
                                   final int configResource) {
        mCountDownLatch = new CountDownLatch(1);

        TaskExecutorUtil.io().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ISingleAccountPublicClientApplication application =
                            PublicClientApplication.createSingleAccountPublicClientApplication(context.getApplicationContext(), configResource);
                    mClient = new SingleClientApplication(application);
                    mCountDownLatch.countDown();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    if (exception instanceof MsalException) {
                        mClientError =
                                new MSQASignInError(((MsalException) exception).getErrorCode(),
                                        exception.getMessage());
                    } else {
                        mClientError =
                                new MSQASignInError(MSQASignInError.INTERRUPTED_ERROR,
                                        exception.getMessage());
                    }
                    mCountDownLatch.countDown();
                    MSQALogger.getInstance().error(TAG, "single application create error", exception);
                }
            }
        });
    }

    @Override
    public ISignInClientApplication getClientApplication() throws Exception {
        this.mCountDownLatch.await();
        if (null != mClientError) {
            throw mClientError;
        } else {
            return mClient;
        }
    }

    @Override
    public boolean isInitializeSuccess() {
        return null != mClient;
    }

}
