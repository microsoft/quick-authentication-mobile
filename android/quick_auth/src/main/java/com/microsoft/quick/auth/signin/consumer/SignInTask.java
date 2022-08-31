package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.ThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Switchers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public class SignInTask implements Convert<ISignInClientApplication, Task<MSQAAccountInfo>> {

    private @NonNull
    final Activity mActivity;
    private @NonNull
    final String[] mScopes;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = SignInTask.class.getSimpleName();

    public SignInTask(@NonNull final Activity activity, @NonNull String[] scopes,
                      @NonNull final MSQATrackerUtil tracker) {
        mActivity = activity;
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> convert(@NonNull final ISignInClientApplication iSignInClientApplication) throws Exception {
        IAccount iAccount = null;
        try {
            mTracker.track(TAG, "start sign in task");
            iAccount = iSignInClientApplication.getCurrentAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // If have account, request the current token directly
        if (iAccount != null) {
            return Task.create(new Task.ConsumerHolder<ISignInClientApplication>() {

                @Override
                public void start(@NonNull Consumer<? super ISignInClientApplication> consumer) {
                    mTracker.track(TAG, "has account info, will request the current token directly");
                    consumer.onSuccess(iSignInClientApplication);
                }
            })
                    .taskConvert(new AcquireCurrentTokenTask(mActivity, true, mScopes, mTracker))
                    .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
        } else {
            return Task.create(new Task.ConsumerHolder<MSQAAccountInfo>() {
                @Override
                public void start(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                    final ThreadSwitcher scheduler = Switchers.io();
                    mTracker.track(TAG, "start request msal sign in api");
                    iSignInClientApplication.signIn(mActivity, null, mScopes,
                            new AuthenticationCallback() {

                                @Override
                                public void onSuccess(final IAuthenticationResult authenticationResult) {
                                    scheduler.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTracker.track(TAG, "request msal sign in success");
                                            MSQAAccountInfo account =
                                                    MSQAAccountInfo.getAccount(authenticationResult);
                                            consumer.onSuccess(account);
                                        }
                                    });
                                }

                                @Override
                                public void onError(final MsalException exception) {
                                    scheduler.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTracker.track(TAG,
                                                    "request msal sign in error:" + exception.getMessage());
                                            consumer.onError(exception);
                                        }
                                    });
                                }

                                @Override
                                public void onCancel() {
                                    scheduler.schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTracker.track(TAG, "request msal sign in cancel");
                                            consumer.onCancel();
                                        }
                                    });
                                }
                            });
                }
            })
                    .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
        }
    }
}
