package com.example.signdemo1.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.signdemo1.R;
import com.microsoft.quick.auth.signin.view.ButtonLogoAlignment;
import com.microsoft.quick.auth.signin.view.ButtonShape;
import com.microsoft.quick.auth.signin.view.ButtonSize;
import com.microsoft.quick.auth.signin.view.ButtonText;
import com.microsoft.quick.auth.signin.view.ButtonTheme;
import com.microsoft.quick.auth.signin.view.ButtonType;
import com.microsoft.quick.auth.signin.view.MQASignInButton;

public class SignInButtonSettingPop extends PopupWindow {

    private final @NonNull
    Context mContext;
    private final @NonNull
    MQASignInButton mSignInButton;
    private RadioGroup mTypeRadioGroup;
    private RadioGroup mThemeRadioGroup;
    private RadioGroup mSizeRadioGroup;
    private RadioGroup mTextRadioGroup;
    private RadioGroup mShapeRadioGroup;
    private RadioGroup mAlignmentRadioGroup;

    public SignInButtonSettingPop(@NonNull Context context, @NonNull MQASignInButton signInButton) {
        super(context);
        mContext = context;
        mSignInButton = signInButton;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_sign_in_button_setting, null);
        init(view);

        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.pw_bottom_anim_style);
    }

    private void init(View rootView) {
        initRadioGroup(rootView);
        rootView.setOnClickListener(v -> dismiss());
        rootView.findViewById(R.id.setting_container).setOnClickListener(v -> {
        });
    }

    private void initRadioGroup(View rootView) {
        mTypeRadioGroup = rootView.findViewById(R.id.sign_button_type_radio_group);
        mThemeRadioGroup = rootView.findViewById(R.id.sign_button_theme_radio_group);
        mSizeRadioGroup = rootView.findViewById(R.id.sign_button_size_radio_group);
        mTextRadioGroup = rootView.findViewById(R.id.sign_button_text_radio_group);
        mShapeRadioGroup = rootView.findViewById(R.id.sign_button_shape_radio_group);
        mAlignmentRadioGroup = rootView.findViewById(R.id.sign_button_logo_alignment_radio_group);
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
