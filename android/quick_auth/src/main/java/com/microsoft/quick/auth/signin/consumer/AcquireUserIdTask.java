package com.microsoft.quick.auth.signin.consumer;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MSQAAPI;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import org.json.JSONObject;

public class AcquireUserIdTask implements Convert<MSQAAccountInfo,
        MSQAAccountInfo> {

    private static final String TAG = AcquireUserIdTask.class.getSimpleName();
    private @NonNull
    final MSQATrackerUtil mTracker;

    public AcquireUserIdTask(@NonNull MSQATrackerUtil tracker) {
        mTracker = tracker;
    }

    @Override
    public MSQAAccountInfo convert(@NonNull MSQAAccountInfo microsoftAccount) throws Exception {
        mTracker.track(TAG, "start request graph api to get account info");
        HttpRequest httpRequest = getHttpRequest(microsoftAccount);
        String result = HttpConnectionClient.request(httpRequest);
        if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            microsoftAccount.setId(jsonObject.optString("id"));
            mTracker.track(TAG, "request graph api to get account info success");
        } else {
            MSQALogger.getInstance().error(TAG, "request account with graph api return empty " +
                    "result error", null);
            mTracker.track(TAG, "request graph api to get account info error: return empty result" +
                    " error");
            throw new MSQASignInException(MSQAErrorString.HTTP_ACCOUNT_REQUEST_ERROR,
                    MSQAErrorString.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
        }
        return microsoftAccount;
    }

    private static HttpRequest getHttpRequest(MSQAAccountInfo microsoftAccount) {
        return new HttpRequest.Builder()
                .setUrl(MSQAAPI.MS_GRAPH_USER_INFO_PATH)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization",
                        MSQAAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
                .builder();
    }
}
