package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.signdemo1.view.SignInButtonSettingPop;
import com.microsoft.quick.auth.signin.MQASignInClient;
import com.microsoft.quick.auth.signin.SignInClient;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.view.ButtonText;
import com.microsoft.quick.auth.signin.view.MQASignInButton;

public class SignInActivity extends Activity {

    private MQASignInButton mSignInButton;
    private TextView mStatus;
    private TextView mUserInfoResult;
    private ImageView mUserPhoto;
    private View mSignButtonSetting;
    private ViewGroup mRootView;

    private SignInClient mSignInClient;
    private boolean mSignedIn;
    private SignInButtonSettingPop pop;

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

        mSignInClient = new MQASignInClient(this);
        mSignInButton.setOnClickListener(v -> {
            if (!mSignedIn) {
                mSignInClient.signIn(this, (accountInfo, error) -> {
                    if (accountInfo != null) {
                        uploadSignInfo(accountInfo, null);
                    } else {
                        uploadSignInfo(null, error);
                    }
                });
            } else {
                mSignInClient.signOut((aBoolean, error) -> uploadSignInfo(null, error));
            }
        });
        mSignButtonSetting.setOnClickListener(v -> {
            if (pop != null && pop.isShowing()) return;
            if (pop == null) {
                pop = new SignInButtonSettingPop(this, mSignInButton);
            }
            pop.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        });
        getCurrentAccount();
    }

    private void uploadSignInfo(AccountInfo accountInfo, Exception error) {
        if (accountInfo != null) {
            mUserPhoto.setImageBitmap(accountInfo.getPhoto());
            String userInfo = "MicrosoftAccountInfo{" +
                    ", fullName='" + accountInfo.getFullName() + '\'' +
                    ", userName='" + accountInfo.getUserName() + '\'' +
                    ", id='" + accountInfo.getId() + '\'' +
                    '}';
            mUserInfoResult.setText(userInfo);
        } else {
            mUserPhoto.setImageBitmap(null);
            mUserInfoResult.setText(error != null ? "login error: " + error.getMessage() : "");
        }
        updateStatus(accountInfo != null);
    }

    private void getCurrentAccount() {
        mSignInClient.getCurrentSignInAccount(this,
                (accountInfo, error) -> uploadSignInfo(accountInfo, error));
    }

    private void updateStatus(boolean signIn) {
        mSignedIn = signIn;
        mStatus.setText(signIn ? "signed in" : "signed out");
        mSignInButton.setButtonText(signIn ? ButtonText.SIGN_OUT : ButtonText.SIGN_IN_WITH);
    }

}
