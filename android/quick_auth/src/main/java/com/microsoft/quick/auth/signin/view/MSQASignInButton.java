package com.microsoft.quick.auth.signin.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.R;
import com.microsoft.quick.auth.signin.entity.SignInParameter;

public class MSQASignInButton extends FrameLayout {

    private SignInParameter mSignInParameter;
    private int mButtonTheme;
    private int mButtonLogoAlignment;
    private int mButtonShape;
    private int mButtonSize;
    private int mButtonText;
    private int mButtonType;
    private LinearLayout mSignInContainer;
    private ImageView mSignInIcon;
    private TextView mSignInText;

    public MSQASignInButton(@NonNull Context context) {
        this(context, null);
    }

    public MSQASignInButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MSQASignInButton(@NonNull Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        init(context);
        updateButtonView();
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.msqa_view_sign_in_button, this, true);
        mSignInContainer = findViewById(R.id.ms_sign_in_button_container);
        mSignInIcon = findViewById(R.id.ms_sign_in_icon);
        mSignInText = findViewById(R.id.ms_sign_in_text);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MQASignInButton);
        mButtonTheme = typedArray.getInt(R.styleable.MQASignInButton_button_theme,
                ButtonTheme.FILLED_BLACK);
        mButtonLogoAlignment =
                typedArray.getInt(R.styleable.MQASignInButton_button_logo_alignment,
                        ButtonLogoAlignment.LEFT);
        mButtonShape = typedArray.getInt(R.styleable.MQASignInButton_button_shape,
                ButtonShape.RECTANGULAR);
        mButtonSize = typedArray.getInt(R.styleable.MQASignInButton_button_size,
                ButtonSize.LARGE);
        mButtonText = typedArray.getInt(R.styleable.MQASignInButton_button_text,
                ButtonText.SIGN_IN_WITH);
        mButtonType = typedArray.getInt(R.styleable.MQASignInButton_button_type,
                ButtonType.STANDARD);
        typedArray.recycle();
    }

    public MSQASignInButton setSignInParameters(SignInParameter parameter) {
        mSignInParameter = parameter;
        return this;
    }

    public MSQASignInButton setButtonTheme(@ButtonTheme int colorTheme) {
        if (mButtonTheme == colorTheme) return this;
        mButtonTheme = colorTheme;
        updateButtonView();
        return this;
    }

    public MSQASignInButton setButtonLogoAlignment(@ButtonLogoAlignment int logoAlignment) {
        if (mButtonLogoAlignment == logoAlignment) return this;
        mButtonLogoAlignment = logoAlignment;
        updateButtonView();
        return this;
    }

    public MSQASignInButton setButtonShape(@ButtonShape int shape) {
        if (mButtonShape == shape) return this;
        mButtonShape = shape;
        updateButtonView();
        return this;
    }

    public MSQASignInButton setButtonSize(@ButtonSize int size) {
        if (mButtonSize == size) return this;
        mButtonSize = size;
        updateButtonView();
        return this;
    }

    public MSQASignInButton setButtonText(@ButtonText int text) {
        if (mButtonText == text) return this;
        mButtonText = text;
        updateButtonView();
        return this;
    }

    public MSQASignInButton setButtonType(@ButtonType int type) {
        if (mButtonType == type) return this;
        mButtonType = type;
        updateButtonView();
        return this;
    }

    @SuppressLint("RtlHardcoded")
    private void updateButtonView() {
        if (mSignInIcon == null || mSignInText == null) return;
        SignInButtonConfig config = new SignInButtonConfig(getContext(), mButtonShape, mButtonSize,
                mButtonText,
                mButtonTheme, mButtonType, mButtonLogoAlignment);
        if (mButtonType == ButtonType.ICON) {
            mSignInText.setVisibility(View.GONE);
        } else {
            mSignInText.setVisibility(View.VISIBLE);
        }
        // set container
        mSignInContainer.setBackground(config.getBackground());
        ViewGroup.LayoutParams containerLayoutParams = mSignInContainer.getLayoutParams();
        if (containerLayoutParams != null) {
            int height = getButtonViewHeight(config);
            int width = getButtonViewWidth(config);
            containerLayoutParams.height = height;
            containerLayoutParams.width = mButtonType == ButtonType.ICON ?
                    Math.min(width, height) :
                    width;
            mSignInContainer.setLayoutParams(containerLayoutParams);
        }
        // set icon
        ViewGroup.LayoutParams iconLayoutParams = mSignInIcon.getLayoutParams();
        if (iconLayoutParams != null) {
            iconLayoutParams.width = config.getIconSize();
            iconLayoutParams.height = config.getIconSize();
            mSignInIcon.setLayoutParams(iconLayoutParams);
        }
        // set text
        mSignInText.setTextAppearance(getContext(), config.getButtonTextAppearance());
        mSignInText.setTextColor(config.getButtonTextColor());
        mSignInText.setText(config.getButtonText());

        // set alignment
        LinearLayout.MarginLayoutParams buttonTextLayoutParams =
                (MarginLayoutParams) mSignInText.getLayoutParams();
        ViewGroup.MarginLayoutParams iconViewLayoutParams =
                (MarginLayoutParams) mSignInIcon.getLayoutParams();
        if (mButtonType != ButtonType.ICON) {
            switch (mButtonLogoAlignment) {
                case ButtonLogoAlignment.CENTER:
                    buttonTextLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    buttonTextLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.mqa_sign_in_button_text_padding), 0, 0, 0);
                    iconViewLayoutParams.setMargins(0, 0, 0, 0);
                    mSignInText.setGravity(Gravity.CENTER);
                    mSignInContainer.setGravity(Gravity.CENTER);
                    break;
                case ButtonLogoAlignment.ICON_LEFT_TEXT_CENTER:
                    buttonTextLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    buttonTextLayoutParams.setMargins(0, 0, 0, 0);
                    iconViewLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.mqa_sign_in_button_icon_padding), 0, 0, 0);
                    mSignInText.setGravity(Gravity.CENTER);
                    mSignInContainer.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    break;
                default:
                    buttonTextLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    buttonTextLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.mqa_sign_in_button_text_padding), 0, 0, 0);
                    iconViewLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.mqa_sign_in_button_icon_padding), 0, 0, 0);
                    mSignInText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    mSignInContainer.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    break;
            }
        } else {
            iconViewLayoutParams.setMargins(0, 0, 0, 0);
            mSignInContainer.setGravity(Gravity.CENTER);
        }
    }

    private int getButtonViewHeight(SignInButtonConfig config) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) return config.getContainerHeight();
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return config.getContainerHeight();
        } else if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            return config.getContainerHeight();
        }
    }

    private int getButtonViewWidth(SignInButtonConfig config) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) return config.getContainerWidth();
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return config.getContainerWidth();
        } else if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            return config.getContainerWidth();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        updateButtonView();
    }
}
