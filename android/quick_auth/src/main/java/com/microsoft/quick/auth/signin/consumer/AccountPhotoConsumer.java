package com.microsoft.quick.auth.signin.consumer;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MicrosoftAPI;
import com.microsoft.quick.auth.signin.task.Function;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class AccountPhotoConsumer implements Function<MQAAccountInfo,
        MQAAccountInfo> {

    @Override
    public MQAAccountInfo apply(@NonNull MQAAccountInfo microsoftAccountInfo) {
        return updateAccount(microsoftAccountInfo);
    }

    private static HttpRequest createRequest(MQAAccountInfo microsoftAccountInfo) {
        return new HttpRequest.Builder()
                .setUrl(MicrosoftAPI.MS_GRAPH_USER_PHOTO_LARGEST)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "image/jpg")
                .addHeader("Authorization",
                        MicrosoftAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccountInfo.getAccessToken())
                .builder();
    }

    @WorkerThread
    public static MQAAccountInfo updateAccount(MQAAccountInfo microsoftAccountInfo) {
        InputStream responseStream = null;
        try {
            HttpURLConnection conn =
                    HttpConnectionClient.createHttpURLConnection(createRequest(microsoftAccountInfo));
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseStream = conn.getInputStream();
                microsoftAccountInfo.setUserPhoto(BitmapFactory.decodeStream(responseStream));
            }
            return microsoftAccountInfo;
        } catch (Exception e) {
            e.getMessage();
        } finally {
            HttpConnectionClient.safeCloseStream(responseStream);
        }
        return microsoftAccountInfo;
    }
}
