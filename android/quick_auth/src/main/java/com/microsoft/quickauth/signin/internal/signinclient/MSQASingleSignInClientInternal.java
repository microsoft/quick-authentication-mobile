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
package com.microsoft.quickauth.signin.internal.signinclient;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quickauth.signin.AccountInfo;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.error.MSQAUiRequiredException;
import com.microsoft.quickauth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quickauth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpMethod;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quickauth.signin.internal.logger.MSQALogger;
import com.microsoft.quickauth.signin.internal.util.MSQATaskExecutor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.json.JSONObject;

public class MSQASingleSignInClientInternal extends MSALSingleClientWrapper {

  private static final String TAG = "MSQASingleSignInClientInternal";

  public MSQASingleSignInClientInternal(ISingleAccountPublicClientApplication application) {
    super(application);
  }

  public void signIn(
      @NonNull final Activity activity,
      @Nullable final IAccount iAccount,
      @NonNull String[] scopes,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    // If no account in cache, start request sign-in api.
    if (iAccount == null) {
      signIn(
          activity,
          null,
          scopes,
          new AuthenticationCallback() {
            @Override
            public void onCancel() {
              MSQALogger.getInstance().warn(TAG, "sign in canceled");
              completeListener.onComplete(null, MSQACancelException.create());
            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
              getUserInfo(authenticationResult, completeListener);
            }

            @Override
            public void onError(MsalException exception) {
              completeListener.onComplete(null, MSQAException.create(exception));
            }
          });
    } else {
      // If has account in cache, request current account directly
      getCurrentSignInAccount(activity, iAccount, scopes, true, completeListener);
    }
  }

  public void getCurrentSignInAccount(
      @NonNull final Activity activity,
      @Nullable final IAccount iAccount,
      @NonNull final String[] scopes,
      final boolean silentTokenErrorRetry,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    // If no account in cache return error.
    if (iAccount == null) {
      completeListener.onComplete(null, MSQAException.createNoAccountException());
    } else {
      // Start to request token silent.
      acquireTokenSilent(
          iAccount,
          scopes,
          new OnCompleteListener<IAuthenticationResult>() {
            @Override
            public void onComplete(
                @Nullable IAuthenticationResult tokenResult, @Nullable MSQAException error) {
              if (tokenResult != null) {
                getUserInfo(tokenResult, completeListener);
              } else if (silentTokenErrorRetry && error instanceof MSQAUiRequiredException) {
                acquireToken(
                    activity,
                    scopes,
                    iAccount,
                    new OnCompleteListener<IAuthenticationResult>() {
                      @Override
                      public void onComplete(
                          @Nullable IAuthenticationResult iAuthenticationResult,
                          @Nullable MSQAException error) {
                        if (iAuthenticationResult != null) {
                          getUserInfo(iAuthenticationResult, completeListener);
                        } else {
                          completeListener.onComplete(null, error);
                        }
                      }
                    });
              } else {
                completeListener.onComplete(null, error);
              }
            }
          });
    }
  }

  public void acquireToken(
      @NonNull Activity activity,
      @NonNull String[] scopes,
      @Nullable IAccount iAccount,
      @NonNull final OnCompleteListener<IAuthenticationResult> completeListener) {
    // If no account in cache return error.
    if (iAccount == null) {
      completeListener.onComplete(null, MSQAException.createNoAccountException());
    } else {
      acquireToken(
          activity,
          scopes,
          new AuthenticationCallback() {
            @Override
            public void onCancel() {
              MSQALogger.getInstance().warn(TAG, "get token canceled");
              completeListener.onComplete(null, MSQAException.createNoAccountException());
            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
              completeListener.onComplete(authenticationResult, null);
            }

            @Override
            public void onError(MsalException exception) {
              MSQALogger.getInstance().error(TAG, "get token error", exception);
              completeListener.onComplete(null, MSQAException.create(exception));
            }
          });
    }
  }

  public void acquireTokenSilent(
      @Nullable IAccount iAccount,
      @NonNull String[] scopes,
      @NonNull final OnCompleteListener<IAuthenticationResult> completeListener) {
    // If no account in cache return error.
    if (iAccount == null) {
      completeListener.onComplete(null, MSQAException.createNoAccountException());
    } else {
      acquireTokenSilentAsync(
          iAccount,
          scopes,
          new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
              completeListener.onComplete(authenticationResult, null);
            }

            @Override
            public void onError(MsalException exception) {
              Exception silentException = exception;
              // wrapper silent MSAL UI thread and expose new thread for developers
              if (silentException instanceof MsalUiRequiredException) {
                silentException =
                    new MSQAUiRequiredException(exception.getErrorCode(), exception.getMessage());
              }
              MSQALogger.getInstance().error(TAG, "get token silent error", exception);
              completeListener.onComplete(null, MSQAException.create(silentException));
            }
          });
    }
  }

  public void getUserInfo(
      @NonNull final IAuthenticationResult tokenResult,
      @NonNull final OnCompleteListener<AccountInfo> completeListener) {
    // Post user info task in background thread.
    MSQATaskExecutor.io()
        .execute(
            new Runnable() {
              @Override
              public void run() {
                // Generate account info from token result.
                final MSQAAccountInfoInternal account =
                    MSQAAccountInfoInternal.getAccount(tokenResult);
                // Start get user id from graph api
                try {
                  String id = getUserId(tokenResult);
                  account.setId(id);
                } catch (final Exception e) {
                  MSQALogger.getInstance().error(TAG, "get user id error", e);
                  // Post the callback running in the main thread.
                  MSQATaskExecutor.main()
                      .execute(
                          new Runnable() {
                            @Override
                            public void run() {
                              completeListener.onComplete(null, MSQAException.create(e));
                            }
                          });
                  return;
                }
                // Start get user photo from graph api.

                account.setUserPhoto(getUserPhoto(tokenResult));
                // Post the callback running in the main thread.
                MSQATaskExecutor.main()
                    .execute(
                        new Runnable() {
                          @Override
                          public void run() {
                            completeListener.onComplete(account, null);
                          }
                        });
              }
            });
  }

  @WorkerThread
  public String getUserPhoto(@NonNull IAuthenticationResult tokenResult) {
    InputStream responseStream = null;
    String base64Photo = null;
    try {
      HttpURLConnection conn =
          MSQAHttpConnectionClient.createHttpURLConnection(
              new MSQAHttpRequest.Builder()
                  .setUrl(MSQAAPIConstant.MS_GRAPH_USER_PHOTO_LARGEST)
                  .setHttpMethod(MSQAHttpMethod.GET)
                  .addHeader("Content-Type", "image/jpg")
                  .addHeader(
                      "Authorization",
                      MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + tokenResult.getAccessToken())
                  .builder());
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        responseStream = conn.getInputStream();
        base64Photo = Base64.encodeToString(readAllBytes(responseStream), Base64.NO_WRAP);
      }
    } catch (Exception e) {
      MSQALogger.getInstance().error(TAG, "get user photo error", e);
    } finally {
      MSQAHttpConnectionClient.safeCloseStream(responseStream);
    }

    return base64Photo;
  }

  @WorkerThread
  public String getUserId(@NonNull IAuthenticationResult tokenResult) throws Exception {
    MSQAHttpRequest httpRequest =
        new MSQAHttpRequest.Builder()
            .setUrl(MSQAAPIConstant.MS_GRAPH_USER_INFO_PATH)
            .setHttpMethod(MSQAHttpMethod.GET)
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + tokenResult.getAccessToken())
            .builder();
    String id = null;
    String result = MSQAHttpConnectionClient.request(httpRequest);
    if (!TextUtils.isEmpty(result)) {
      JSONObject jsonObject = new JSONObject(result);
      id = jsonObject.optString("id");
    }
    return id;
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
