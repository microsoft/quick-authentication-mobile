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
import com.microsoft.quickauth.signin.MSQAAccountInfo;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.error.MSQANoAccountException;
import com.microsoft.quickauth.signin.error.MSQAUiRequiredException;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.internal.entity.MSQAAccountInfoInternal;
import com.microsoft.quickauth.signin.internal.http.MSQAAPIConstant;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpConnectionClient;
import com.microsoft.quickauth.signin.internal.http.MSQAHttpRequest;
import com.microsoft.quickauth.signin.internal.util.MSQATaskExecutor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;
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
      @NonNull final OnCompleteListener<MSQAAccountInfo> completeListener) {
    // If no account in cache, start request sign-in api.
    if (iAccount == null) {
      MSQALogger.getInstance().verbose(TAG, "sign in started with no account in cache");
      signIn(
          activity,
          null,
          scopes,
          new AuthenticationCallback() {
            @Override
            public void onCancel() {
              MSQALogger.getInstance().warn(TAG, "sign in canceled");
              completeListener.onComplete(null, MSQACancelException.create(null));
            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
              MSQALogger.getInstance().verbose(TAG, "get sign in authentication result success");
              getUserInfo(authenticationResult, completeListener);
            }

            @Override
            public void onError(MsalException exception) {
              MSQALogger.getInstance().error(TAG, "sign in error", exception);
              completeListener.onComplete(null, MSQAException.mapToMSQAException(exception));
            }
          });
    } else {
      // If has account in cache, request token directly, otherwise, calling MSAL signIn api will
      // error.
      MSQALogger.getInstance()
          .verbose(
              TAG,
              "sign in started, has account in cache and will start request get current sign in account api");
      acquireTokenSilent(
          iAccount,
          scopes,
          new OnCompleteListener<IAuthenticationResult>() {
            @Override
            public void onComplete(
                @Nullable IAuthenticationResult tokenResult, @Nullable MSQAException error) {
              if (tokenResult != null) {
                getUserInfo(tokenResult, completeListener);
              } else if (error instanceof MSQAUiRequiredException) {
                MSQALogger.getInstance()
                    .verbose(
                        TAG,
                        "get current sign in account started, has account in cache and will start request token silent api");
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

  public void getCurrentAccount(
      @Nullable final IAccount iAccount,
      @NonNull final String[] scopes,
      @NonNull final OnCompleteListener<MSQAAccountInfo> completeListener) {
    // If no account in cache return null.
    if (iAccount == null) {
      completeListener.onComplete(null, MSQANoAccountException.create());
    } else {
      // Start to request token silent.
      MSQALogger.getInstance()
          .verbose(
              TAG,
              "get current sign in account started, has account in cache and will start request token silent api");
      acquireTokenSilent(
          iAccount,
          scopes,
          new OnCompleteListener<IAuthenticationResult>() {
            @Override
            public void onComplete(
                @Nullable IAuthenticationResult tokenResult, @Nullable MSQAException error) {
              if (tokenResult != null) {
                getUserInfo(tokenResult, completeListener);
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
      completeListener.onComplete(null, MSQANoAccountException.create());
    } else {
      MSQALogger.getInstance()
          .verbose(
              TAG, "acquire token started, has account in cache and will start request token api");
      try {
        acquireToken(
            activity,
            scopes,
            new AuthenticationCallback() {
              @Override
              public void onCancel() {
                MSQALogger.getInstance().warn(TAG, "get token canceled");
                completeListener.onComplete(null, MSQACancelException.create(null));
              }

              @Override
              public void onSuccess(IAuthenticationResult authenticationResult) {
                MSQALogger.getInstance().verbose(TAG, "get token success");
                completeListener.onComplete(authenticationResult, null);
              }

              @Override
              public void onError(MsalException exception) {
                MSQALogger.getInstance().error(TAG, "get token error", exception);
                completeListener.onComplete(null, MSQAException.mapToMSQAException(exception));
              }
            });

      } catch (Exception e) {
        completeListener.onComplete(null, MSQAException.mapToMSQAException(e));
      }
    }
  }

  public void acquireTokenSilent(
      @Nullable IAccount iAccount,
      @NonNull String[] scopes,
      @NonNull final OnCompleteListener<IAuthenticationResult> completeListener) {
    // If no account in cache return error.
    if (iAccount == null) {
      completeListener.onComplete(null, MSQANoAccountException.create());
    } else {
      MSQALogger.getInstance()
          .verbose(
              TAG,
              "acquire token silent started, has account in cache and will start request token silent api");
      try {
        acquireTokenSilentAsync(
            iAccount,
            scopes,
            new SilentAuthenticationCallback() {
              @Override
              public void onSuccess(IAuthenticationResult authenticationResult) {
                MSQALogger.getInstance().verbose(TAG, "acquire token silent success");
                completeListener.onComplete(authenticationResult, null);
              }

              @Override
              public void onError(MsalException exception) {
                completeListener.onComplete(null, MSQAException.mapToMSQAException(exception));
              }
            });

      } catch (Exception e) {
        completeListener.onComplete(null, MSQAException.mapToMSQAException(e));
      }
    }
  }

  public void getUserInfo(
      @NonNull final IAuthenticationResult tokenResult,
      @NonNull final OnCompleteListener<MSQAAccountInfo> completeListener) {
    // Post user info task in background thread.
    MSQATaskExecutor.background()
        .execute(
            new Runnable() {
              @Override
              public void run() {
                MSQALogger.getInstance().verbose(TAG, "get user info started");
                CountDownLatch countDownLatch = new CountDownLatch(2);
                // Generate account info from token result.
                final MSQAAccountInfoInternal account =
                    MSQAAccountInfoInternal.getAccount(tokenResult);

                // Start get user photo from graph api.
                MSQATaskExecutor.background()
                    .execute(
                        new Runnable() {
                          @Override
                          public void run() {
                            account.setUserPhoto(getUserPhoto(tokenResult));
                            countDownLatch.countDown();
                          }
                        });

                // Start update user info with graph api
                updateUserInfoWithGraph(account, tokenResult);
                countDownLatch.countDown();
                try {
                  countDownLatch.await();
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                // Post the callback running in the main thread.
                MSQATaskExecutor.main()
                    .execute(
                        new Runnable() {
                          @Override
                          public void run() {
                            MSQALogger.getInstance().verbose(TAG, "get user info finished");
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
      MSQALogger.getInstance().verbose(TAG, "get user photo started");
      HttpURLConnection conn =
          MSQAHttpConnectionClient.get(
              new MSQAHttpRequest.Builder(MSQAAPIConstant.MS_GRAPH_USER_PHOTO_LARGEST_PATH)
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
    MSQALogger.getInstance().verbose(TAG, "get user photo finished");
    return base64Photo;
  }

  @WorkerThread
  public void updateUserInfoWithGraph(
      MSQAAccountInfoInternal account, @NonNull IAuthenticationResult tokenResult) {
    MSQALogger.getInstance().verbose(TAG, "update user info with graph api started");
    MSQAHttpRequest httpRequest =
        new MSQAHttpRequest.Builder(MSQAAPIConstant.MS_GRAPH_USER_INFO_PATH)
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                MSQAAPIConstant.MS_GRAPH_TK_REQUEST_PREFIX + tokenResult.getAccessToken())
            .builder();
    try {
      HttpURLConnection conn = MSQAHttpConnectionClient.get(httpRequest);
      String result = MSQAHttpConnectionClient.getStringResponse(conn);
      if (!TextUtils.isEmpty(result)) {
        JSONObject jsonObject = new JSONObject(result);
        account.setId(jsonObject.optString("id"));
        account.setGivenName(jsonObject.optString("givenName"));
        account.setSurname(jsonObject.optString("surname"));
        String userPrincipalName = jsonObject.optString("userPrincipalName");
        if (canBeEmail(userPrincipalName)) {
          account.setEmail(userPrincipalName);
        }
      }
    } catch (Exception e) {
      MSQALogger.getInstance().error(TAG, "update user info with graph api error", e);
    }
    MSQALogger.getInstance().verbose(TAG, "update user info with graph api finished");
  }

  private boolean canBeEmail(String str) {
    // Very simple check. Just check for existence of '@'.
    // This check is intentionally simple, since the usage scenarios are simple.
    return !TextUtils.isEmpty(str) && str.indexOf('@') != -1;
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
