package com.microsoft.quick.auth.signin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.error.MSQASignInErrorHelper;
import com.microsoft.quick.auth.signin.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.http.HttpMethod;
import com.microsoft.quick.auth.signin.http.HttpRequest;
import com.microsoft.quick.auth.signin.http.MicrosoftAPI;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.logger.LogUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class GraphAccountPhotoTask implements Task.OnSubscribe<Bitmap> {

    private final @NonNull
    String mUserToken;
    private static final String TAG = GraphAccountPhotoTask.class.getSimpleName();

    public GraphAccountPhotoTask(@NonNull String userToken) {
        mUserToken = userToken;
    }

    @Override
    public void subscribe(@NonNull Consumer<? super Bitmap> consumer) {
        InputStream responseStream = null;
        try {
            HttpURLConnection conn = HttpConnectionClient.createHttpURLConnection(createRequest());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                responseStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(responseStream);
                consumer.onSuccess(bitmap);
            } else {
                responseStream = conn.getErrorStream();
                MSQASignInError error =
                        new MSQASignInError(MSQASignInErrorHelper.HTTP_ACCOUNT_PHOTO_REQUEST_ERROR,
                                HttpConnectionClient.convertStreamToString(responseStream));
                consumer.onError(error);
                LogUtil.error(TAG, "request account photo with graph api return error", error);
            }
        } catch (Exception e) {
            e.getMessage();
            consumer.onError(e);
            LogUtil.error(TAG, "request account photo catch an unexpected error", e);
        } finally {
            HttpConnectionClient.safeCloseStream(responseStream);
        }
    }

    private HttpRequest createRequest() {
        return new HttpRequest.Builder()
                .setUrl(MicrosoftAPI.MS_GRAPH_USER_PHOTO_LARGEST)
                .setHttpMethod(HttpMethod.GET)
                .addHeader("Content-Type", "image/jpg")
                .addHeader("Authorization", MicrosoftAPI.MS_GRAPH_TK_REQUEST_PREFIX + mUserToken)
                .builder();
    }

    public static Task<Bitmap> getUserPhotoObservable(@NonNull String userToken) {
        return Task.create(new GraphAccountPhotoTask(userToken))
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain())
                .nextConsumerOn(DirectToScheduler.directToMainWhenCreateInMain());
    }
}
