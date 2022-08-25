package com.microsoft.quick.auth.signin.consumer;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MicrosoftAPI;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.tracker.MSQATracker;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class AccountPhotoConsumer implements Function<MSQAAccountInfo,
        MSQAAccountInfo> {
    private static final String TAG = AccountPhotoConsumer.class.getSimpleName();
    private final @NonNull
    MSQATracker mTracker;

    public AccountPhotoConsumer(@NonNull MSQATracker tracker) {
        mTracker = tracker;
    }

    @Override
    public MSQAAccountInfo apply(@NonNull MSQAAccountInfo microsoftAccountInfo) {
        mTracker.track(TAG, "start request graph api to get user photo");
        InputStream responseStream = null;
        try {
            HttpURLConnection conn =
                    HttpConnectionClient.createHttpURLConnection(createRequest(microsoftAccountInfo));
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseStream = conn.getInputStream();
                microsoftAccountInfo.setUserPhoto(BitmapFactory.decodeStream(responseStream));
                mTracker.track(TAG, "request graph api to get user photo success");
            }
            return microsoftAccountInfo;
        } catch (Exception e) {
            e.getMessage();
        } finally {
            HttpConnectionClient.safeCloseStream(responseStream);
        }
        return microsoftAccountInfo;
    }

    private static HttpRequest createRequest(MSQAAccountInfo microsoftAccountInfo) {
        return new HttpRequest.Builder()
                .setUrl(MicrosoftAPI.MS_GRAPH_USER_PHOTO_LARGEST)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "image/jpg")
                .addHeader("Authorization",
                        MicrosoftAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccountInfo.getAccessToken())
                .builder();
    }
}
