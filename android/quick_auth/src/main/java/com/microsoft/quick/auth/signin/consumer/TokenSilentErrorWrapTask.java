package com.microsoft.quick.auth.signin.consumer;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.error.MSQAUiRequiredException;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

public class TokenSilentErrorWrapTask implements Function<Exception, Task<TokenResult>> {
    private static final String TAG = TokenSilentErrorWrapTask.class.getSimpleName();
    private @NonNull
    final MSQATrackerUtil mTracker;

    public TokenSilentErrorWrapTask(@NonNull final MSQATrackerUtil tracker) {
        mTracker = tracker;
    }

    @Override
    public Task<TokenResult> apply(@NonNull final Exception exception) throws Exception {
        return Task.create(new Task.OnSubscribe<TokenResult>() {

            @Override
            public void subscribe(@NonNull Consumer<? super TokenResult> consumer) {
                Exception silentException = exception;
                if (exception instanceof MsalUiRequiredException) {
                    mTracker.track(TAG, "token silent error instanceof MsalUiRequiredException, will return wrap " +
                            "error");
                    silentException =
                            new MSQAUiRequiredException(((MsalUiRequiredException) exception).getErrorCode(),
                                    exception.getMessage());
                }
                consumer.onError(silentException);
            }
        });
    }
}
