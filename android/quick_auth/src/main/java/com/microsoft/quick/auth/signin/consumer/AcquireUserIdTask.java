package com.microsoft.quick.auth.signin.consumer;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MSQAAPI;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

import org.json.JSONObject;

public class AcquireUserIdTask implements Convert<MSQAAccountInfo,
        Task<MSQAAccountInfo>> {

    private static final String TAG = AcquireUserIdTask.class.getSimpleName();
    private @NonNull
    final MSQATracker mTracker;

    public AcquireUserIdTask(@NonNull MSQATracker tracker) {
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> convert(@NonNull final MSQAAccountInfo msqaAccountInfo) throws Exception {
        return new Task<MSQAAccountInfo>() {
            @Override
            protected void startActual(@NonNull Consumer<? super MSQAAccountInfo> consumer) {
                try {
                    mTracker.track(TAG, "start request graph api to get account info");
                    HttpRequest httpRequest = getHttpRequest(msqaAccountInfo);
                    String result = HttpConnectionClient.request(httpRequest);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject = new JSONObject(result);
                        msqaAccountInfo.setId(jsonObject.optString("id"));
                        mTracker.track(TAG, "request graph api to get account info success");
                    } else {
                        MSQALogger.getInstance().error(TAG, "request account with graph api return empty " +
                                "result error", null);
                        mTracker.track(TAG, "request graph api to get account info error: return empty result" +
                                " error");
                        throw new MSQASignInError(MSQAErrorString.HTTP_ACCOUNT_REQUEST_ERROR,
                                MSQAErrorString.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
                    }
                    consumer.onSuccess(msqaAccountInfo);
                } catch (Exception e) {
                    consumer.onError(e);
                }
            }
        }
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain());
    }

    private HttpRequest getHttpRequest(MSQAAccountInfo microsoftAccount) {
        return new HttpRequest.Builder()
                .setUrl(MSQAAPI.MS_GRAPH_USER_INFO_PATH)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization",
                        MSQAAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
                .builder();
    }
}