//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.internal.http;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.common.java.AuthenticationConstants;
import com.microsoft.quickauth.signin.error.MSQAErrorString;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class MSQAHttpConnectionClient {

  private static final String TAG = "HttpConnectionClient";

  @WorkerThread
  public static HttpURLConnection post(@NonNull MSQAHttpRequest request)
      throws IOException, JSONException {
    URL url = new URL(request.getUrl());
    HttpURLConnection conn = createConnection(url, request);
    conn.setRequestMethod(MSQAHttpMethod.POST);
    // add params
    if (request.getParams() != null && !request.getParams().isEmpty()) {
      JSONObject jsonObject = new JSONObject();
      for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
        jsonObject.putOpt(entry.getKey(), entry.getValue());
      }
      OutputStream out = conn.getOutputStream();
      out.write(jsonObject.toString().getBytes());
      out.flush();
      out.close();
    }
    return conn;
  }

  @WorkerThread
  public static HttpURLConnection get(@NonNull MSQAHttpRequest request) throws IOException {
    StringBuilder urlBuilder = new StringBuilder(request.getUrl());
    // add params
    if (request.getParams() != null && !request.getParams().isEmpty()) {
      urlBuilder.append("?");
      int index = 0;
      for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
        urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        if (index != request.getParams().size() - 1) {
          urlBuilder.append("&");
        }
      }
    }
    URL url = new URL(urlBuilder.toString());
    HttpURLConnection conn = createConnection(url, request);
    conn.setRequestMethod(MSQAHttpMethod.GET);
    return conn;
  }

  private static HttpURLConnection createConnection(URL url, @NonNull MSQAHttpRequest request)
      throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(request.getConnectTimeout());
    conn.setReadTimeout(request.getReadTimeout());
    for (Map.Entry<String, String> entry : request.getHeader().entrySet()) {
      conn.setRequestProperty(entry.getKey(), entry.getValue());
    }
    return conn;
  }

  public static String getStringResponse(HttpURLConnection conn) throws IOException, MSQAException {
    InputStream responseStream = null;
    try {
      responseStream = conn.getInputStream();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        return responseStream == null
            ? ""
            : MSQAHttpConnectionClient.convertStreamToString(responseStream);
      } else {
        responseStream = conn.getErrorStream();
        throw new MSQAException(
            MSQAErrorString.HTTP_REQUEST_ERROR,
            MSQAHttpConnectionClient.convertStreamToString(responseStream));
      }
    } finally {
      MSQAHttpConnectionClient.safeCloseStream(responseStream);
    }
  }

  private static String convertStreamToString(InputStream inputStream) throws IOException {
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
