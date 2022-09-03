package com.microsoft.quick.auth.signin.internal.http;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.common.java.AuthenticationConstants;
import com.microsoft.quick.auth.signin.error.MSQAErrorString;
import com.microsoft.quick.auth.signin.error.MSQASignInError;
import com.microsoft.quick.auth.signin.logger.MSQALogger;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpConnectionClient {

  private static final String TAG = "HttpConnectionClient";

  @WorkerThread
  public static String request(@NonNull HttpRequest request) throws IOException, MSQASignInError {
    InputStream responseStream = null;
    try {
      HttpURLConnection conn = HttpConnectionClient.createHttpURLConnection(request);
      responseStream = conn.getInputStream();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        return responseStream == null
            ? ""
            : HttpConnectionClient.convertStreamToString(responseStream);
      } else {
        responseStream = conn.getErrorStream();
        throw new MSQASignInError(
            MSQAErrorString.HTTP_REQUEST_ERROR,
            HttpConnectionClient.convertStreamToString(responseStream));
      }
    } finally {
      HttpConnectionClient.safeCloseStream(responseStream);
    }
  }

  public static HttpURLConnection createHttpURLConnection(@NonNull HttpRequest request)
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
