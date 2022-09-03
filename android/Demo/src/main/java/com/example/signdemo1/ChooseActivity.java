package com.example.signdemo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class ChooseActivity extends Activity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.sign_in_activity)
        .setOnClickListener(
            v -> startActivity(new Intent(ChooseActivity.this, SignInActivity.class)));
    findViewById(R.id.token_activity)
        .setOnClickListener(
            v -> startActivity(new Intent(ChooseActivity.this, IdTokenActivity.class)));
  }
}
