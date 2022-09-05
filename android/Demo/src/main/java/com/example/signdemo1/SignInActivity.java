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
package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.signdemo1.view.SignInButtonSettingPop;
import com.microsoft.quickauth.signin.AccountInfo;
import com.microsoft.quickauth.signin.ClientCreatedListener;
import com.microsoft.quickauth.signin.ISignInClient;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.MSQASignInOptions;
import com.microsoft.quickauth.signin.TokenResult;
import com.microsoft.quickauth.signin.error.MSQASignInException;
import com.microsoft.quickauth.signin.error.MSQAUiRequiredException;
import com.microsoft.quickauth.signin.logger.LogLevel;
import com.microsoft.quickauth.signin.view.MSQASignInButton;

public class SignInActivity extends Activity {

  private MSQASignInButton mSignInButton;
  private TextView mStatus;
  private TextView mUserInfoResult;
  private ImageView mUserPhoto;
  private View mSignButtonSetting;
  private TextView mTokenResult;
  private View mSignOutButton;
  private View msAcquireTokenButton;
  private View msAcquireTokenSilentButton;
  private ViewGroup mRootView;

  private ISignInClient mSignInClient;
  private SignInButtonSettingPop pop;
  private String[] scops;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);
    mSignInButton = findViewById(R.id.ms_sign_button);
    mRootView = findViewById(R.id.root_view);
    mStatus = findViewById(R.id.status);
    mUserInfoResult = findViewById(R.id.userInfoResult);
    mUserPhoto = findViewById(R.id.userPhoto);
    mSignButtonSetting = findViewById(R.id.ms_sign_button_setting);
    mSignOutButton = findViewById(R.id.ms_sign_out_button);
    mTokenResult = findViewById(R.id.tv_token_result);
    msAcquireTokenButton = findViewById(R.id.ms_acquire_token_button);
    msAcquireTokenSilentButton = findViewById(R.id.ms_acquire_token_silent_button);
    scops = new String[] {"user.read"};

    MSQASignInClient.create(
        this,
        new MSQASignInOptions.Builder()
            .setConfigResourceId(R.raw.auth_config_single_account)
            .setEnableLogcatLog(true)
            .setLogLevel(LogLevel.VERBOSE)
            .setExternalLogger(
                (logLevel, message) -> {
                  // get log message in this
                })
            .build(),
        new ClientCreatedListener() {
          @Override
          public void onCreated(@NonNull MSQASignInClient client) {
            mSignInClient = client;
            getCurrentAccount();
            mStatus.setText("sign in client created successfully.");
            mSignInButton.setSignInCallback(
                SignInActivity.this,
                client,
                (accountInfo, error) -> {
                  if (accountInfo != null) {
                    uploadSignInfo(accountInfo, null);
                  } else {
                    uploadSignInfo(null, error);
                  }
                });
          }

          @Override
          public void onError(@NonNull MSQASignInException error) {
            mStatus.setText("create sign in client error:" + error.getMessage());
          }
        });
    mSignOutButton.setOnClickListener(
        v -> {
          checkClient();
          mSignInClient.signOut((aBoolean, error) -> uploadSignInfo(null, error));
          updateTokenResult(null, null);
        });
    mSignButtonSetting.setOnClickListener(
        v -> {
          if (pop != null && pop.isShowing()) return;
          if (pop == null) {
            pop = new SignInButtonSettingPop(this, mSignInButton);
          }
          pop.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        });
    msAcquireTokenButton.setOnClickListener(
        v -> {
          mTokenResult.setText("");
          acquireToken();
        });
    msAcquireTokenSilentButton.setOnClickListener(
        v -> {
          checkClient();
          mSignInClient.acquireTokenSilent(
              scops,
              (iTokenResult, error) -> {
                /**
                 * If acquireTokenSilent() returns an error that requires an interaction
                 * (MsalUiRequiredException), invoke acquireToken() to have the user resolve the
                 * interrupt interactively.
                 *
                 * <p>Some example scenarios are - password change - the resource you're acquiring a
                 * token for has a stricter set of requirement than your Single Sign-On refresh
                 * token. - you're introducing a new scope which the user has never consented for.
                 */
                mTokenResult.setText("");
                if (error instanceof MSQAUiRequiredException) {
                  acquireToken();
                } else {
                  updateTokenResult(iTokenResult, error);
                }
              });
        });
  }

  private void acquireToken() {
    checkClient();
    mSignInClient.acquireToken(
        this, scops, (tokenResult, error) -> updateTokenResult(tokenResult, error));
  }

  private void uploadSignInfo(AccountInfo accountInfo, Exception error) {
    if (accountInfo != null) {
      //      mUserPhoto.setImageBitmap(ByteCodeUtil.base642Bitmap(accountInfo.getBase64Photo()));
      mUserPhoto.setImageBitmap(accountInfo.getBitmapPhoto());
      String userInfo =
          "MicrosoftAccountInfo{"
              + ", fullName='"
              + accountInfo.getFullName()
              + '\''
              + ", userName='"
              + accountInfo.getUserName()
              + '\''
              + ", id='"
              + accountInfo.getId()
              + '\''
              + '}';
      mUserInfoResult.setText(userInfo);
    } else {
      mUserPhoto.setImageBitmap(null);
      mUserInfoResult.setText(error != null ? "login error: " + error.getMessage() : "");
    }
    updateStatus(accountInfo != null);
  }

  private void getCurrentAccount() {
    checkClient();
    mSignInClient.getCurrentSignInAccount(
        this, (accountInfo, error) -> uploadSignInfo(accountInfo, error));
  }

  private void updateStatus(boolean signIn) {
    mStatus.setText(signIn ? "signed in" : "signed out");

    mSignInButton.setVisibility(signIn ? View.GONE : View.VISIBLE);
    mSignOutButton.setVisibility(signIn ? View.VISIBLE : View.GONE);
  }

  private void updateTokenResult(TokenResult tokenResult, Exception error) {
    mTokenResult.setText(
        tokenResult != null
            ? tokenResult.getAccessToken()
            : error != null ? "error:" + error.getMessage() : "");
  }

  private void checkClient() {
    if (mSignInClient == null) {
      mUserInfoResult.setText("MSQASignInClient Not yet initialized");
      return;
    }
  }
}
