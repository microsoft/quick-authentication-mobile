package com.microsoft.quick.auth.signin.internal.consumer;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.internal.signinclient.IClientApplication;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;

public class AcquireCurrentAccountTask
    implements MSQATaskFunction<IClientApplication, MSQATask<Pair<IClientApplication, IAccount>>> {

  private @NonNull final MSQATracker mTracker;
  private static final String TAG = "AcquireCurrentAccountTask";

  public AcquireCurrentAccountTask(@NonNull final MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public MSQATask<Pair<IClientApplication, IAccount>> apply(
      @NonNull final IClientApplication iClientApplication) throws Exception {
    return MSQATask.create(
        new MSQATask.ConsumerHolder<Pair<IClientApplication, IAccount>>() {
          @Override
          public void start(
              @NonNull final MSQAConsumer<? super Pair<IClientApplication, IAccount>> consumer) {
            mTracker.track(TAG, LogLevel.VERBOSE, "start request MSAL api getCurrentAccount", null);
            iClientApplication.getCurrentAccountAsync(
                new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
                  @Override
                  public void onAccountLoaded(@Nullable IAccount activeAccount) {
                    consumer.onSuccess(new Pair<>(iClientApplication, activeAccount));
                  }

                  @Override
                  public void onAccountChanged(
                      @Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                    consumer.onSuccess(new Pair<>(iClientApplication, currentAccount));
                  }

                  @Override
                  public void onError(@NonNull MsalException exception) {
                    consumer.onSuccess(new Pair<>(iClientApplication, (IAccount) null));
                  }
                });
          }
        });
  }
}
