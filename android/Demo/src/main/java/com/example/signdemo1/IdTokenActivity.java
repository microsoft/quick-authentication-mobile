package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.SignInClient;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.TokenResult;

import java.util.ArrayList;
import java.util.List;

public class IdTokenActivity extends Activity {

    private RadioGroup mRadioGroup;
    private TextView mTokenResult;
    private boolean mSilentToken;
    private Button mSignInButton;

    private SignInClient mSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_token);
        mRadioGroup = findViewById(R.id.sign_button_type_radio_group);
        mSignInButton = findViewById(R.id.ms_sign_button);
        mTokenResult = findViewById(R.id.token_result);
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> mSilentToken =
                checkedId != R.id.sign_button_type_radio_group);
        List<String> scopes = new ArrayList<>();
        scopes.add("user.read");
        mSignInButton.setOnClickListener(v -> {
            mTokenResult.setText("");
            if (!mSilentToken) {
                mSignInClient.acquireToken(IdTokenActivity.this, scopes,
                        (iTokenResult, error) -> uploadSignInfo(iTokenResult, error));
            } else {
                mSignInClient.acquireTokenSilent(scopes,
                        (iTokenResult, error) -> uploadSignInfo(iTokenResult, error));
            }
        });
        mSignInClient = MSQASignInClient.sharedInstance();
        getCurrentInfo();
    }

    private void uploadSignInfo(TokenResult tokenResult, Exception error) {
        if (tokenResult != null) {
            mTokenResult.setText("request success, token= " + tokenResult.getAccessToken());
        } else {
            mTokenResult.setText(error != null ? "request error:" + error.getMessage() : "");
        }
        getCurrentInfo();
    }

    private void getCurrentInfo() {
        mSignInClient.getCurrentSignInAccount(this,
                (accountInfo, error) -> {
                });
    }
}
