package com.azuresamples.quickauth.sign.test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.microsoft.identity.common.java.net.HttpUrlConnectionFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import javax.net.ssl.HttpsURLConnection;
import org.mockito.Mockito;

// Util class for mocking.
public class MSQATestMockUtil {
  private static final String MOCK_ACCOUNT_INFO_PREFERENCE = "quickauth.signin.test.account";
  private static final String CURRENT_ACCOUNT_KEY = "current_account_key";
  private static final String CURRENT_TOKEN_KEY = "current_token_key";
  public static final String CURRENT_USER_NAME = "current_user_name_key";
  public static final String CURRENT_FULL_NAME = "current_full_name";
  public static final String EMPTY_STRING = "";
  public static final String DEFAULT_TEST_ACCOUNT = "default_test_account";

  // private constructor for Util class.
  private MSQATestMockUtil() {}

  static void removeAllAccount(final Context appContext) {
    getAccountSharedPreference(appContext).edit().clear().apply();
  }

  static String getCurrentToken(final Context appContext) {
    return getAccountSharedPreference(appContext).getString(CURRENT_TOKEN_KEY, null);
  }

  static String getCurrentAccount(final Context appContext) {
    return getAccountSharedPreference(appContext).getString(CURRENT_ACCOUNT_KEY, null);
  }

  static void setCurrentAccount(final Context appContext, String account) {
    getAccountSharedPreference(appContext).edit().putString(CURRENT_ACCOUNT_KEY, account).apply();
  }

  static void setCurrentToken(final Context appContext, String token) {
    getAccountSharedPreference(appContext).edit().putString(CURRENT_TOKEN_KEY, token).apply();
  }

  static SharedPreferences getAccountSharedPreference(final Context appContext) {
    return appContext.getSharedPreferences(MOCK_ACCOUNT_INFO_PREFERENCE, Activity.MODE_PRIVATE);
  }

  static HttpsURLConnection getMockedConnectionWithSuccessResponse(final String message)
      throws IOException {
    final HttpsURLConnection mockedHttpUrlConnection = getCommonHttpUrlConnection();

    Mockito.when(mockedHttpUrlConnection.getInputStream()).thenReturn(createInputStream(message));
    Mockito.when(mockedHttpUrlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

    return mockedHttpUrlConnection;
  }

  static HttpsURLConnection getMockedConnectionWithFailureResponse(
      final int statusCode, final String errorMessage) throws IOException {
    final HttpsURLConnection mockedHttpUrlConnection = getCommonHttpUrlConnection();

    Mockito.when(mockedHttpUrlConnection.getInputStream()).thenThrow(IOException.class);
    Mockito.when(mockedHttpUrlConnection.getErrorStream())
        .thenReturn(createInputStream(errorMessage));
    Mockito.when(mockedHttpUrlConnection.getResponseCode()).thenReturn(statusCode);

    return mockedHttpUrlConnection;
  }

  static HttpsURLConnection getMockedConnectionWithSocketTimeout() throws IOException {
    final HttpsURLConnection mockedUrlConnection = getCommonHttpUrlConnection();

    Mockito.when(mockedUrlConnection.getInputStream()).thenThrow(SocketTimeoutException.class);
    return mockedUrlConnection;
  }

  static HttpsURLConnection getCommonHttpUrlConnection() throws IOException {
    final HttpsURLConnection mockedConnection = Mockito.mock(HttpsURLConnection.class);
    Mockito.doNothing().when(mockedConnection).setConnectTimeout(Mockito.anyInt());
    Mockito.doNothing().when(mockedConnection).setDoInput(Mockito.anyBoolean());
    return mockedConnection;
  }

  static void mockFailedGetRequest(int statusCode, final String errorResponse) throws IOException {
    final HttpsURLConnection mockedConnection =
        getMockedConnectionWithFailureResponse(statusCode, errorResponse);
    HttpUrlConnectionFactory.addMockedConnection(mockedConnection);
  }

  static InputStream createInputStream(final String input) {
    return input == null ? null : new ByteArrayInputStream(input.getBytes());
  }
}
