package com.microsoft.quick.auth.signin.consumer;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.error.MSQASignInErrorHelper;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MicrosoftAPI;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.logger.LogUtil;
import com.microsoft.quick.auth.signin.tracker.MSQATracker;

import org.json.JSONObject;

public class AccountUpdateWithGraphConsumer implements Function<MSQAAccountInfo,
        MSQAAccountInfo> {

    private static final String TAG = AccountUpdateWithGraphConsumer.class.getSimpleName();
    private final @NonNull
    MSQATracker mTracker;

    public AccountUpdateWithGraphConsumer(@NonNull MSQATracker tracker) {
        mTracker = tracker;
    }

    @Override
    public MSQAAccountInfo apply(@NonNull MSQAAccountInfo microsoftAccount) throws Exception {
        mTracker.track(TAG, "start request graph api to get account info");
        HttpRequest httpRequest = getHttpRequest(microsoftAccount);
        String result = HttpConnectionClient.requestAccountInfo(httpRequest);
        if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            microsoftAccount.setFullName(jsonObject.optString("displayName"));
            microsoftAccount.setId(jsonObject.optString("id"));
            mTracker.track(TAG, "request graph api to get account info success");
        } else {
            LogUtil.error(TAG, "request account with graph api return empty result error");
            mTracker.track(TAG, "request graph api to get account info error: return empty result error");
            throw new MSQASignInError(MSQASignInErrorHelper.HTTP_ACCOUNT_REQUEST_ERROR,
                    MSQASignInErrorHelper.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
        }
        return microsoftAccount;
    }

    private static HttpRequest getHttpRequest(MSQAAccountInfo microsoftAccount) {
        return new HttpRequest.Builder()
                .setUrl(MicrosoftAPI.MS_GRAPH_USER_INFO_PATH)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization",
                        MicrosoftAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
                .builder();
    }
}
