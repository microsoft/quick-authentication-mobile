package com.microsoft.quick.auth.signin.consumer;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MSQAAPI;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class AcquireUserPhotoTask implements Convert<MSQAAccountInfo,
        MSQAAccountInfo> {
    private static final String TAG = AcquireUserPhotoTask.class.getSimpleName();
    private @NonNull
    final MSQATrackerUtil mTracker;

    public AcquireUserPhotoTask(@NonNull MSQATrackerUtil tracker) {
        mTracker = tracker;
    }

    @Override
    public MSQAAccountInfo convert(@NonNull MSQAAccountInfo microsoftAccountInfo) {
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
            e.printStackTrace();
            MSQALogger.getInstance().error(TAG, "acquire photo api error", e);
        } finally {
            HttpConnectionClient.safeCloseStream(responseStream);
        }
        return microsoftAccountInfo;
    }

    private static HttpRequest createRequest(MSQAAccountInfo microsoftAccountInfo) {
        return new HttpRequest.Builder()
                .setUrl(MSQAAPI.MS_GRAPH_USER_PHOTO_LARGEST)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "image/jpg")
                .addHeader("Authorization",
                        MSQAAPI.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccountInfo.getAccessToken())
                .builder();
    }
}
