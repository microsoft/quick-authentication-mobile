package com.microsoft.quick.auth.signin.internal.consumer;

import android.util.Base64;
import androidx.annotation.NonNull;
import com.microsoft.quick.auth.signin.internal.entity.MSQAInnerAccountInfo;
import com.microsoft.quick.auth.signin.internal.http.HttpConnectionClient;
import com.microsoft.quick.auth.signin.internal.http.HttpMethod;
import com.microsoft.quick.auth.signin.internal.http.HttpRequest;
import com.microsoft.quick.auth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quick.auth.signin.logger.LogLevel;
import com.microsoft.quick.auth.signin.internal.task.Consumer;
import com.microsoft.quick.auth.signin.internal.task.Convert;
import com.microsoft.quick.auth.signin.internal.task.DirectThreadSwitcher;
import com.microsoft.quick.auth.signin.internal.task.Switchers;
import com.microsoft.quick.auth.signin.internal.task.Task;
import com.microsoft.quick.auth.signin.internal.util.MSQATracker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class AcquireUserPhotoTask implements Convert<MSQAInnerAccountInfo, Task<MSQAInnerAccountInfo>> {
  private static final String TAG = "AcquireUserPhotoTask";
  private @NonNull final MSQATracker mTracker;

  public AcquireUserPhotoTask(@NonNull MSQATracker tracker) {
    mTracker = tracker;
  }

  @Override
  public Task<MSQAInnerAccountInfo> convert(@NonNull final MSQAInnerAccountInfo msqaAccountInfo)
      throws Exception {
    mTracker.track(TAG, LogLevel.VERBOSE, "start request graph api to get user photo", null);
    return new Task<MSQAInnerAccountInfo>() {
      @Override
      protected void startActual(@NonNull Consumer<? super MSQAInnerAccountInfo> consumer) {
        InputStream responseStream = null;
        try {
          HttpURLConnection conn =
              HttpConnectionClient.createHttpURLConnection(createRequest(msqaAccountInfo));
          if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            responseStream = conn.getInputStream();
            byte[] bytes = readAllBytes(responseStream);
            msqaAccountInfo.setUserPhoto(Base64.encodeToString(bytes, Base64.NO_WRAP));
            mTracker.track(
                TAG, LogLevel.VERBOSE, "request graph api to get user photo success", null);
          }
        } catch (Exception e) {
          mTracker.track(TAG, LogLevel.ERROR, "request photo api error", e);
        } finally {
          HttpConnectionClient.safeCloseStream(responseStream);
        }
        consumer.onSuccess(msqaAccountInfo);
      }
    }.taskScheduleOn(DirectThreadSwitcher.directToIOWhenCreateInMain())
        .nextTaskSchedulerOn(Switchers.mainThread());
  }

  private static HttpRequest createRequest(MSQAInnerAccountInfo microsoftAccountInfo) {
    return new HttpRequest.Builder()
        .setUrl(MSQAAPIConstant.MS_GRAPH_USER_PHOTO_LARGEST)
        .setHttpMethod(HttpMethod.GET)
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
