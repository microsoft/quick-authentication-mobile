package com.azuresamples.quickauth.sign.test;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.test.R;
import com.microsoft.quickauth.signin.view.MSQASignInButton;

public class TestActivity extends Activity {

  private MSQASignInButton mSignInButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.msqa_test_activity);
    mSignInButton = findViewById(R.id.signInButton);
  }
}
