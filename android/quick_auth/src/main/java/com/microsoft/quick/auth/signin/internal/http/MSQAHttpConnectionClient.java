package com.microsoft.quick.auth.signin.internal.http;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.common.java.AuthenticationConstants;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MSQAHttpConnectionClient {

  private static final String TAG = "HttpConnectionClient";

  @WorkerThread
  public static String request(@NonNull MSQAHttpRequest request)
      throws IOException, MSQASignInException {
    InputStream responseStream = null;
    try {
      HttpURLConnection conn = MSQAHttpConnectionClient.createHttpURLConnection(request);
      responseStream = conn.getInputStream();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        return responseStream == null
            ? ""
            : MSQAHttpConnectionClient.convertStreamToString(responseStream);
      } else {
        responseStream = conn.getErrorStream();
        throw new MSQASignInException(
            MSQAErrorString.HTTP_REQUEST_ERROR,
            MSQAHttpConnectionClient.convertStreamToString(responseStream));
      }
    } finally {
      MSQAHttpConnectionClient.safeCloseStream(responseStream);
    }
  }

  public static HttpURLConnection createHttpURLConnection(@NonNull MSQAHttpRequest request)
      throws IOException {
    URL url = new URL(request.getUrl());
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(request.getConnectTimeout());
    conn.setReadTimeout(request.getReadTimeout());
    conn.setRequestMethod(request.getHttpMethod());
    for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
      conn.setRequestProperty(entry.getKey(), entry.getValue());
    }
    return conn;
  }

  public static String convertStreamToString(InputStream inputStream) throws IOException {
    if (inputStream == null) return null;
    try {
      BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(inputStream, AuthenticationConstants.CHARSET_UTF8));
      char[] buffer = new char[1024];
      StringBuilder stringBuilder = new StringBuilder();

      int charsRead;
      while ((charsRead = reader.read(buffer)) > -1) {
        stringBuilder.append(buffer, 0, charsRead);
      }
      return stringBuilder.toString();
    } finally {
      safeCloseStream(inputStream);
    }
  }

  public static void safeCloseStream(Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (IOException var3) {
        MSQALogger.getInstance().error(TAG, ":safe close stream error", var3);
      }
    }
  }
}
