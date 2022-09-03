package com.microsoft.quick.auth.signin.internal.consumer;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quick.auth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quick.auth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quick.auth.signin.internal.http.MSQAHttpMethod;
import com.microsoft.quick.auth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import org.json.JSONObject;

public class AcquireUserIdTask
    implements MSQATaskFunction<MSQAAccountInfoInternal, MSQATask<MSQAAccountInfoInternal>> {

  private static final String TAG = "AcquireUserIdTask";
  private @NonNull final MSQATracker mTracker;

  public AcquireUserIdTask(@NonNull MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final MSQAAccountInfoInternal accountInfo) {
    return new MSQATask<MSQAAccountInfoInternal>() {
      @Override
      protected void subscribeActual(
          @NonNull MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
        try {
          mTracker.track(
              TAG, LogLevel.VERBOSE, "start request graph api to get account info", null);
          MSQAHttpRequest httpRequest = getHttpRequest(accountInfo);
          String result = MSQAHttpConnectionClient.request(httpRequest);
          if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            accountInfo.setId(jsonObject.optString("id"));
            mTracker.track(
                TAG, LogLevel.VERBOSE, "request graph api to get account info success", null);
          } else {
            mTracker.track(
                TAG,
                LogLevel.VERBOSE,
                "request graph api to get account info error: return empty result error",
                null);
            throw new MSQASignInException(
                MSQAErrorString.HTTP_ACCOUNT_REQUEST_ERROR,
                MSQAErrorString.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
          }
          consumer.onSuccess(accountInfo);
        } catch (Exception e) {
          consumer.onError(e);
        }
      }
    }.upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain());
  }

  private MSQAHttpRequest getHttpRequest(MSQAAccountInfoInternal microsoftAccount) {
    return new MSQAHttpRequest.Builder()
        .setUrl(MSQAAPIConstant.MS_GRAPH_USER_INFO_PATH)
        .setHttpMethod(MSQAHttpMethod.GET)
        .addHeader("Content-Type", "application/json")
        .addHeader(
            "Authorization",
            MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
        .builder();
  }
}
