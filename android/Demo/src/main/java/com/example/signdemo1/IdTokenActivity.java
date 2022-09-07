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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.ClientCreatedListener;
import com.microsoft.quickauth.signin.ISignInClient;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.MSQASignInOptions;
import com.microsoft.quickauth.signin.TokenResult;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;

public class IdTokenActivity extends Activity {

  private RadioGroup mRadioGroup;
  private TextView mTokenResult;
  private boolean mSilentToken;
  private Button mSignInButton;

  private ISignInClient mSignInClient;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_id_token);
    mRadioGroup = findViewById(R.id.sign_button_type_radio_group);
    mSignInButton = findViewById(R.id.ms_sign_button);
    mTokenResult = findViewById(R.id.token_result);
    mRadioGroup.setOnCheckedChangeListener(
        (group, checkedId) -> mSilentToken = checkedId != R.id.sign_button_type_radio_group);
    String[] scopes = new String[] {"user.read"};
    mSignInButton.setOnClickListener(
        v -> {
          mTokenResult.setText("");
          if (!mSilentToken) {
            mSignInClient.acquireToken(
                IdTokenActivity.this,
                scopes,
                (iTokenResult, error) -> uploadSignInfo(iTokenResult, error));
          } else {
            mSignInClient.acquireTokenSilent(
                scopes, (iTokenResult, error) -> uploadSignInfo(iTokenResult, error));
          }
        });
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
            getCurrentInfo();
          }

          @Override
          public void onError(@NonNull MSQAException error) {
            mTokenResult.setText("create sign in client error:" + error.getMessage());
          }
        });
  }

  private void uploadSignInfo(TokenResult tokenResult, Exception error) {
    if (tokenResult != null) {
      mTokenResult.setText("request success, token= " + tokenResult.getAccessToken());
    } else {
      mTokenResult.setText(error != null ? "request error:" + error.getMessage() : "");
    }
  }

  private void getCurrentInfo() {
    mSignInClient.getCurrentAccount(this, (accountInfo, error) -> {});
  }
}
