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
import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.SignInClient;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.error.MSQAUiRequiredException;
import com.microsoft.quick.auth.signin.view.MSQASignInButton;

import java.util.ArrayList;
import java.util.List;

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

    private SignInClient mSignInClient;
    private SignInButtonSettingPop pop;
    private List<String> scops;

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
        scops = new ArrayList<>();
        scops.add("user.read");

        mSignInClient = MSQASignInClient.sharedInstance();
        mSignInButton.setSignInCallback(this, (accountInfo, error) -> {
            if (accountInfo != null) {
                uploadSignInfo(accountInfo, null);
            } else {
                uploadSignInfo(null, error);
            }
        });
        mSignOutButton.setOnClickListener(v -> {
            mSignInClient.signOut((aBoolean, error) -> uploadSignInfo(null, error));
            updateTokenResult(null, null);
        });
        mSignButtonSetting.setOnClickListener(v -> {
            if (pop != null && pop.isShowing()) return;
            if (pop == null) {
                pop = new SignInButtonSettingPop(this, mSignInButton);
            }
            pop.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        });
        msAcquireTokenButton.setOnClickListener(v -> {
            mTokenResult.setText("");
            mSignInClient.acquireTokenSilent(scops, (iTokenResult, error) -> acquireToken());
        });
        msAcquireTokenSilentButton.setOnClickListener(v -> mSignInClient.acquireTokenSilent(scops, (iTokenResult,
                                                                                                    error) -> {
            /**
             * If acquireTokenSilent() returns an error that requires an interaction
             * (MsalUiRequiredException),
             * invoke acquireToken() to have the user resolve the interrupt interactively.
             *
             * Some example scenarios are
             *  - password change
             *  - the resource you're acquiring a token for has a stricter set of requirement than
             *  your Single Sign-On refresh token.
             *  - you're introducing a new scope which the user has never consented for.
             */
            mTokenResult.setText("");
            if (error instanceof MSQAUiRequiredException) {
                acquireToken();
            } else {
                updateTokenResult(iTokenResult, error);
            }
        }));
        getCurrentAccount();
    }

    private void acquireToken() {
        mSignInClient.acquireToken(this, scops, this::updateTokenResult);
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
        mStatus.setText(signIn ? "signed in" : "signed out");

        mSignInButton.setVisibility(signIn ? View.GONE : View.VISIBLE);
        mSignOutButton.setVisibility(signIn ? View.VISIBLE : View.GONE);
    }

    private void updateTokenResult(TokenResult tokenResult, Exception error) {
        mTokenResult.setText(tokenResult != null ? tokenResult.getAccessToken() : error != null ?
                "error:" + error.getMessage() : "");
    }
}
