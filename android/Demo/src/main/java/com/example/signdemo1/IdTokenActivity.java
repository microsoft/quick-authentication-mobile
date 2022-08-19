package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.Disposable;
import com.microsoft.quick.auth.signin.MQASignInClient;
import com.microsoft.quick.auth.signin.SignInClient;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;

public class IdTokenActivity extends Activity {

    private RadioGroup mRadioGroup;
    private TextView mTokenResult;
    private boolean mSilentToken;
    private Button mSignInButton;

    private SignInClient mSignInClient;
    private AccountInfo mAccountInfo;
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_token);
        mRadioGroup = findViewById(R.id.sign_button_type_radio_group);
        mSignInButton = findViewById(R.id.ms_sign_button);
        mTokenResult = findViewById(R.id.token_result);
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> mSilentToken =
                checkedId != R.id.sign_button_type_radio_group);
        mSignInButton.setOnClickListener(v -> {
            mTokenResult.setText("");
            if (!mSilentToken) {
                mSignInClient.acquireToken(IdTokenActivity.this, new String[]{"user.read"}, null,
                        (iTokenResult, error) -> uploadSignInfo(iTokenResult, error));
            } else {
                if (mAccountInfo == null) return;
                mSignInClient.acquireTokenSilent(mAccountInfo, new String[]{"user.read"},
                        (iTokenResult, error) -> uploadSignInfo(iTokenResult, error));
            }
        });
        mSignInClient = new MQASignInClient(this);
        getCurrentInfo();
    }

    private void uploadSignInfo(ITokenResult iTokenResult, Exception error) {
        if (iTokenResult != null) {
            mTokenResult.setText("request success, token= " + iTokenResult.getAccessToken());
        } else {
            mTokenResult.setText(error != null ? "request error:" + error.getMessage() : "");
        }
        getCurrentInfo();
    }

    private void getCurrentInfo() {
        if (mDisposable != null) mDisposable.dispose();
        mDisposable = mSignInClient.getCurrentSignInAccount(this,
                (accountInfo, error) -> mAccountInfo = accountInfo);
    }
}
