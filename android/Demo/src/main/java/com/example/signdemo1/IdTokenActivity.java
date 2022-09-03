package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quick.auth.signin.ClientCreatedListener;
import com.microsoft.quick.auth.signin.ISignInClient;
import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.MSQASignInOptions;
import com.microsoft.quick.auth.signin.MSQATokenResult;
import com.microsoft.quick.auth.signin.error.MSQASignInException;
import com.microsoft.quick.auth.signin.logger.LogLevel;

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
          public void onError(@NonNull MSQASignInException error) {
            mTokenResult.setText("create sign in client error:" + error.getMessage());
          }
        });
  }

  private void uploadSignInfo(MSQATokenResult tokenResult, Exception error) {
    if (tokenResult != null) {
      mTokenResult.setText("request success, token= " + tokenResult.getAccessToken());
    } else {
      mTokenResult.setText(error != null ? "request error:" + error.getMessage() : "");
    }
  }

  private void getCurrentInfo() {
    mSignInClient.getCurrentSignInAccount(this, (accountInfo, error) -> {});
  }
}
