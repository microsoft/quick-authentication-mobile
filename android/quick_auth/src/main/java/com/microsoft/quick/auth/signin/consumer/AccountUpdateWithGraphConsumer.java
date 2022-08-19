package com.microsoft.quick.auth.signin.consumer;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInError;
import com.microsoft.quick.auth.signin.error.MicrosoftSignInErrorHelper;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MicrosoftAPI;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.logger.LogUtil;

import org.json.JSONObject;

public class AccountUpdateWithGraphConsumer implements Function<MQAAccountInfo,
        MQAAccountInfo> {

    private static final String TAG = AccountUpdateWithGraphConsumer.class.getSimpleName();

    @Override
    public MQAAccountInfo apply(@NonNull MQAAccountInfo microsoftAccount) throws Exception {
        return updateAccount(microsoftAccount);
    }

    @WorkerThread
    public static MQAAccountInfo updateAccount(MQAAccountInfo microsoftAccount) throws Exception {
        HttpRequest httpRequest = getHttpRequest(microsoftAccount);
        String result = HttpConnectionClient.requestAccountInfo(httpRequest);
        if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            microsoftAccount.setFullName(jsonObject.optString("displayName"));
            microsoftAccount.setId(jsonObject.optString("id"));
        } else {
            LogUtil.error(TAG, "request account with graph api return empty result error");
            throw new MicrosoftSignInError(MicrosoftSignInErrorHelper.HTTP_ACCOUNT_REQUEST_ERROR,
                    MicrosoftSignInErrorHelper.HTTP_REQUEST_ACCOUNT_INFO_ERROR_MESSAGE);
        }
        return microsoftAccount;
    }


    private static HttpRequest getHttpRequest(MQAAccountInfo microsoftAccount) {
        return new HttpRequest.Builder()
                .setUrl(MicrosoftAPI.MS_GRAPH_USER_INFO_PATH)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization",
                        MicrosoftAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccount.getAccessToken())
                .builder();
    }
}
