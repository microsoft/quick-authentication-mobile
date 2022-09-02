package com.microsoft.quick.auth.signin.consumer;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.entity.MSQAAccountInfo;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MSQAAPIConstant;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.Convert;
import com.microsoft.quick.auth.signin.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.task.Switchers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class AcquireUserPhotoTask implements Convert<MSQAAccountInfo,
        Task<MSQAAccountInfo>> {
    private static final String TAG = "AcquireUserPhotoTask";
    private @NonNull
    final MSQATracker mTracker;

    public AcquireUserPhotoTask(@NonNull MSQATracker tracker) {
        mTracker = tracker;
    }

    @Override
    public Task<MSQAAccountInfo> convert(@NonNull final MSQAAccountInfo msqaAccountInfo) throws Exception {
        mTracker.track(TAG, "start request graph api to get user photo");
        return new Task<MSQAAccountInfo>() {
            @Override
            protected void startActual(@NonNull Consumer<? super MSQAAccountInfo> consumer) {
                InputStream responseStream = null;
                try {
                    HttpURLConnection conn =
                            HttpConnectionClient.createHttpURLConnection(createRequest(msqaAccountInfo));
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        responseStream = conn.getInputStream();
                        byte[] bytes = readAllBytes(responseStream);
                        msqaAccountInfo.setUserPhoto(Base64.encodeToString(bytes, Base64.NO_WRAP));
                        mTracker.track(TAG, "request graph api to get user photo success");
                    }
                } catch (Exception e) {
                    mTracker.track(TAG, "request photo api error:" + e.getMessage());
                } finally {
                    HttpConnectionClient.safeCloseStream(responseStream);
                }
                consumer.onSuccess(msqaAccountInfo);
            }
        }
                .taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain())
                .nextTaskSchedulerOn(Switchers.mainThread());
    }

    private static HttpRequest createRequest(MSQAAccountInfo microsoftAccountInfo) {
        return new HttpRequest.Builder()
                .setUrl(MSQAAPIConstant.MS_GRAPH_USER_PHOTO_LARGEST)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "image/jpg")
                .addHeader("Authorization",
                        MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + microsoftAccountInfo.getAccessToken())
                .builder();
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int numRead;
        int BUFFER_SIZE = 16384;
        byte[] data = new byte[BUFFER_SIZE];
        while ((numRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, numRead);
        }
        return buffer.toByteArray();
    }
}
