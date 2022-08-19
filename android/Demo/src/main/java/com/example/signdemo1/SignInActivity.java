package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.MQASignInClient;
import com.microsoft.quick.auth.signin.SignInClient;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.view.ButtonLogoAlignment;
import com.microsoft.quick.auth.signin.view.ButtonShape;
import com.microsoft.quick.auth.signin.view.ButtonSize;
import com.microsoft.quick.auth.signin.view.ButtonText;
import com.microsoft.quick.auth.signin.view.ButtonTheme;
import com.microsoft.quick.auth.signin.view.ButtonType;
import com.microsoft.quick.auth.signin.view.MQASignInButton;

public class SignInActivity extends Activity {

    private MQASignInButton mSignInButton;
    private RadioGroup mTypeRadioGroup;
    private RadioGroup mThemeRadioGroup;
    private RadioGroup mSizeRadioGroup;
    private RadioGroup mTextRadioGroup;
    private RadioGroup mShapeRadioGroup;
    private RadioGroup mAlignmentRadioGroup;
    private TextView mStatus;
    private TextView mUserInfoResult;
    private ImageView mUserPhoto;

    private SignInClient mSignInClient;
    private boolean mSignedIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSignInButton = findViewById(R.id.ms_sign_button);
        mStatus = findViewById(R.id.status);
        mUserInfoResult = findViewById(R.id.userInfoResult);
        mUserPhoto = findViewById(R.id.userPhoto);
        mTypeRadioGroup = findViewById(R.id.sign_button_type_radio_group);
        mThemeRadioGroup = findViewById(R.id.sign_button_theme_radio_group);
        mSizeRadioGroup = findViewById(R.id.sign_button_size_radio_group);
        mTextRadioGroup = findViewById(R.id.sign_button_text_radio_group);
        mShapeRadioGroup = findViewById(R.id.sign_button_shape_radio_group);
        mAlignmentRadioGroup = findViewById(R.id.sign_button_logo_alignment_radio_group);

        initRadioGroup();

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

    private void initRadioGroup() {
        mTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mSignInButton.setButtonType(checkedId == R.id.standard ? ButtonType.STANDARD :
                    ButtonType.ICON);
        });
        mThemeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mSignInButton.setButtonTheme(checkedId == R.id.filled_light ? ButtonTheme.FILLED_BLUE :
                    ButtonTheme.FILLED_BLACK);
        });
        mSizeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.size_small:
                    mSignInButton.setButtonSize(ButtonSize.SMALL);
                    break;
                case R.id.size_medium:
                    mSignInButton.setButtonSize(ButtonSize.MEDIUM);
                    break;
                default:
                    mSignInButton.setButtonSize(ButtonSize.LARGE);
                    break;
            }
        });
        mTextRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mSignInButton.setButtonText(checkedId == R.id.signin_with ? ButtonText.SIGN_IN_WITH :
                    ButtonText.SIGN_OUT);
        });
        mShapeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rounded:
                    mSignInButton.setButtonShape(ButtonShape.ROUNDED);
                    break;
                case R.id.pill:
                    mSignInButton.setButtonShape(ButtonShape.PILL);
                    break;
                default:
                    mSignInButton.setButtonShape(ButtonShape.RECTANGULAR);
                    break;
            }
        });
        mAlignmentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.icon_left_text_center:
                    mSignInButton.setButtonLogoAlignment(ButtonLogoAlignment.ICON_LEFT_TEXT_CENTER);
                    break;
                case R.id.center:
                    mSignInButton.setButtonLogoAlignment(ButtonLogoAlignment.CENTER);
                    break;
                default:
                    mSignInButton.setButtonLogoAlignment(ButtonLogoAlignment.LEFT);
                    break;
            }
        });
    }
}
