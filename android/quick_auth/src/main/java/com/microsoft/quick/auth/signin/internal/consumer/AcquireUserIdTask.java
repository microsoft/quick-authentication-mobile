package com.microsoft.quick.auth.signin.internal.consumer;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.internal.entity.MSQAInnerAccountInfo;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.internal.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.internal.http.HttpMethod;
import com.microsoft.quick.auth.signin.internal.http.HttpRequest;
import com.microsoft.quick.auth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.internal.task.Consumer;
import com.microsoft.quick.auth.signin.internal.task.Convert;
import com.microsoft.quick.auth.signin.internal.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.Task;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import org.json.JSONObject;

public class AcquireUserIdTask implements Convert<MSQAInnerAccountInfo, Task<MSQAInnerAccountInfo>> {

  private static final String TAG = "AcquireUserIdTask";
  private @NonNull final MSQATracker mTracker;

  public AcquireUserIdTask(@NonNull MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public Task<MSQAInnerAccountInfo> convert(@NonNull final MSQAInnerAccountInfo msqaAccountInfo)
      throws Exception {
    return new Task<MSQAInnerAccountInfo>() {
      @Override
      protected void startActual(@NonNull Consumer<? super MSQAInnerAccountInfo> consumer) {
        try {
          mTracker.track(
              TAG, LogLevel.VERBOSE, "start request graph api to get account info", null);
          HttpRequest httpRequest = getHttpRequest(msqaAccountInfo);
          String result = HttpConnectionClient.request(httpRequest);
          if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            msqaAccountInfo.setId(jsonObject.optString("id"));
            mTracker.track(
                TAG, LogLevel.VERBOSE, "request graph api to get account info success", null);
          } else {
            mTracker.track(
                TAG,
                LogLevel.VERBOSE,
                "request graph api to get account info error: return " + "empty result error",
                null);
            throw new MSQASignInError(
                MSQAErrorString.HTTP_ACCOUNT_REQUEST_ERROR,
                MSQAErrorString.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
          }
          consumer.onSuccess(msqaAccountInfo);
        } catch (Exception e) {
          consumer.onError(e);
        }
      }
    }.taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
  }

  private HttpRequest getHttpRequest(MSQAInnerAccountInfo microsoftAccount) {
    return new HttpRequest.Builder()
        .setUrl(MSQAAPIConstant.MS_GRAPH_USER_INFO_PATH)
        .setHttpMethod(HttpMethod.GET)
        .addHeader("Content-Type", "application/json")
        .addHeader(
            "Authorization",
            MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
        .builder();
  }
}
