package com.example.signdemo1.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.signdemo1.R;

public class TestActivity extends Activity {
  private TextView textView;
  private View button;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    textView = findViewById(R.id.helloWorldTextView);
    button = findViewById(R.id.text_click_button);
    button.setOnClickListener(v -> textView.setText("clickMeBtn"));
  }
}
