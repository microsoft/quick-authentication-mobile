package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

public class SignInTask implements Convert<IClientApplication, Task<MSQAAccountInfo>> {

    private @NonNull
    final Activity mActivity;
    private @NonNull
    final String[] mScopes;
    private @NonNull
    final MSQATracker mTracker;
    private static final String TAG = "SignInTask";

    public SignInTask(@NonNull final Activity activity, @NonNull String[] scopes,
                      @NonNull final MSQATracker tracker) {
        mActivity = activity;
        mScopes = scopes;
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> convert(@NonNull final IClientApplication iClientApplication) throws Exception {
        return Task.create(new Task.ConsumerHolder<Pair<Boolean, IAccount>>() {
            @Override
            public void start(@NonNull Consumer<? super Pair<Boolean, IAccount>> consumer) {
                IAccount iAccount = null;
                try {
                    mTracker.track(TAG, "start sign in task");
                    iAccount = iClientApplication.getCurrentAccount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                consumer.onSuccess(new Pair<>(iAccount != null, iAccount));
            }
        })
                .then(new Convert<Pair<Boolean, IAccount>, Task<MSQAAccountInfo>>() {
                    @Override
                    public Task<MSQAAccountInfo> convert(@NonNull Pair<Boolean, IAccount> booleanIAccountPair) {
                        if (booleanIAccountPair.first) {
                            return Task.with(iClientApplication)
                                    .then(new AcquireCurrentTokenTask(mActivity, true, mScopes, mTracker));
                        } else {
                            return Task.create(new Task.ConsumerHolder<MSQAAccountInfo>() {
                                @Override
                                public void start(@NonNull final Consumer<? super MSQAAccountInfo> consumer) {
                                    mTracker.track(TAG, "start request msal sign in api");
                                    iClientApplication.signIn(mActivity, null, mScopes,
                                            new AuthenticationCallback() {

                                                @Override
                                                public void onSuccess(final IAuthenticationResult authenticationResult) {
                                                    mTracker.track(TAG, "request msal sign in success");
                                                    MSQAAccountInfo account =
                                                            MSQAAccountInfo.getAccount(authenticationResult);
                                                    consumer.onSuccess(account);
                                                }

                                                @Override
                                                public void onError(final MsalException exception) {
                                                    mTracker.track(TAG,
                                                            "request msal sign in error:" + exception.getMessage());

                                                    consumer.onError(exception);
                                                }

                                                @Override
                                                public void onCancel() {
                                                    mTracker.track(TAG, "request msal sign in cancel");
                                                    consumer.onCancel();
                                                }
                                            });
                                }
                            });
                        }
                    }
                })
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
    }
}
