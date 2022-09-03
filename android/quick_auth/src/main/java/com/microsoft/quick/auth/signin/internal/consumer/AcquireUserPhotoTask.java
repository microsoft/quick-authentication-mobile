package com.microsoft.quick.auth.signin.internal.consumer;

import android.util.Base64;
import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quick.auth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quick.auth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quick.auth.signin.internal.http.MSQAHttpMethod;
import com.microsoft.quick.auth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quick.auth.signin.internal.task.MSQAConsumer;
import com.microsoft.quick.auth.signin.internal.task.MSQADirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.MSQASwitchers;
import com.microsoft.quick.auth.signin.internal.task.MSQATask;
import com.microsoft.quick.auth.signin.internal.task.MSQATaskFunction;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class AcquireUserPhotoTask
    implements MSQATaskFunction<MSQAAccountInfoInternal, MSQATask<MSQAAccountInfoInternal>> {
  private static final String TAG = "AcquireUserPhotoTask";
  private @NonNull final MSQATracker mTracker;

  public AcquireUserPhotoTask(@NonNull MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public MSQATask<MSQAAccountInfoInternal> apply(
      @NonNull final MSQAAccountInfoInternal accountInfo) {
    mTracker.track(TAG, LogLevel.VERBOSE, "start request graph api to get user photo", null);
    return new MSQATask<MSQAAccountInfoInternal>() {
      @Override
      protected void subscribeActual(
          @NonNull MSQAConsumer<? super MSQAAccountInfoInternal> consumer) {
        InputStream responseStream = null;
        try {
          HttpURLConnection conn =
              MSQAHttpConnectionClient.createHttpURLConnection(createRequest(accountInfo));
          if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            responseStream = conn.getInputStream();
            byte[] bytes = readAllBytes(responseStream);
            accountInfo.setUserPhoto(Base64.encodeToString(bytes, Base64.NO_WRAP));
            mTracker.track(
                TAG, LogLevel.VERBOSE, "request graph api to get user photo success", null);
          }
        } catch (Exception e) {
          mTracker.track(TAG, LogLevel.ERROR, "request photo api error", e);
        } finally {
          MSQAHttpConnectionClient.safeCloseStream(responseStream);
        }
        consumer.onSuccess(accountInfo);
      }
    }.upStreamScheduleOn(MSQADirectThreadSwitcher.directToIOWhenCreateInMain())
        .downStreamSchedulerOn(MSQASwitchers.mainThread());
  }

  private static MSQAHttpRequest createRequest(MSQAAccountInfoInternal microsoftAccountInfo) {
    return new MSQAHttpRequest.Builder()
        .setUrl(MSQAAPIConstant.MS_GRAPH_USER_PHOTO_LARGEST)
        .setHttpMethod(MSQAHttpMethod.GET)
        .addHeader("Content-Type", "image/jpg")
        .addHeader(
            "Authorization",
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
