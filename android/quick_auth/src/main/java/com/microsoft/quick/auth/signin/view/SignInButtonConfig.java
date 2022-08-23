package com.microsoft.quick.auth.signin.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.microsoft.quick.auth.signin.R;

public class SignInButtonConfig {
    private Context mContext;
    private int mIconSize;
    private int mContainerHeight;
    private int mContainerWidth;
    private Drawable mBackground;
    private int mButtonTextAppearance;
    private int mButtonTextColor;
    private String mButtonText;

    public SignInButtonConfig(Context context, @ButtonShape int shape, @ButtonSize int size,
                              @ButtonText int text,
                              @ButtonTheme int theme, @ButtonType int type,
                              @ButtonLogoAlignment int alignment) {
        mContext = context;

        mIconSize = mContext.getResources().getDimensionPixelSize(getIconSize(size));
        mContainerHeight = mContext.getResources().getDimensionPixelSize(getContainerHeight(size));
        mContainerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.mqa_sign_in_button_width);
        mBackground = getBackground(theme, shape);
        mButtonTextAppearance = getButtonTextTextAppearance(size);
        mButtonTextColor = mContext.getResources().getColor(getButtonTextColor(theme));
        mButtonText = mContext.getString(getButtonText(text));
    }

    private Drawable getBackground(@ButtonTheme int theme, @ButtonShape int shape) {
        int radios = getBackgroundRadios(shape);
        int backgroundRes = R.drawable.mqa_sign_in_button_background;
        Drawable drawable = mContext.getResources().getDrawable(backgroundRes);
        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            if (theme == ButtonTheme.FILLED_BLACK) {
                gradientDrawable.setColor(mContext.getResources().getColor(R.color.mqa_sign_in_button_color_dark));
                gradientDrawable.setStroke(0, Color.TRANSPARENT);
            } else {
                gradientDrawable.setColor(mContext.getResources().getColor(R.color.mqa_sign_in_button_color_light));
                gradientDrawable.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen
                                .mqa_sign_in_button_background_border_width),
                        mContext.getResources().getColor(R.color
                                .mqa_sign_in_button_border_light));
            }
            gradientDrawable.setCornerRadius(mContext.getResources().getDimensionPixelSize(radios));
        }
        return drawable;
    }

    private int getBackgroundRadios(@ButtonShape int shape) {
        switch (shape) {
            case ButtonShape.ROUNDED:
                return R.dimen.mqa_sign_in_button_radios_round;
            case ButtonShape.PILL:
                return R.dimen.mqa_sign_in_button_radios_pill;
            default:
                return R.dimen.mqa_sign_in_button_radios_square;
        }
    }

    private int getIconSize(@ButtonSize int size) {
        switch (size) {
            case ButtonSize.SMALL:
                return R.dimen.mqa_sign_in_button_icon_size_small;
            case ButtonSize.MEDIUM:
                return R.dimen.mqa_sign_in_button_icon_size_medium;
            default:
                return R.dimen.mqa_sign_in_button_icon_size_large;
        }
    }

    private int getContainerHeight(@ButtonSize int size) {
        switch (size) {
            case ButtonSize.SMALL:
                return R.dimen.mqa_sign_in_button_height_small;
            case ButtonSize.MEDIUM:
                return R.dimen.mqa_sign_in_button_height_medium;
            default:
                return R.dimen.mqa_sign_in_button_height_large;
        }
    }

    private int getButtonTextSize(@ButtonSize int size) {
        switch (size) {
            case ButtonSize.SMALL:
                return R.dimen.mqa_sign_in_button_height_small;
            case ButtonSize.MEDIUM:
                return R.dimen.mqa_sign_in_button_height_medium;
            default:
                return R.dimen.mqa_sign_in_button_height_large;
        }
    }

    private int getButtonTextTextAppearance(@ButtonSize int size) {
        switch (size) {
            case ButtonSize.SMALL:
                return R.style.MQATextAppearance_SignInButton_Small;
            case ButtonSize.MEDIUM:
                return R.style.MQATextAppearance_SignInButton_Medium;
            default:
                return R.style.MQATextAppearance_SignInButton_Large;
        }
    }

    private int getButtonTextColor(@ButtonTheme int theme) {
        switch (theme) {
            case ButtonTheme.FILLED_BLACK:
                return R.color.mqa_sign_in_button_text_color_dark;
            default:
                return R.color.mqa_sign_in_button_text_color_light;
        }
    }

    private int getButtonText(@ButtonText int text) {
        switch (text) {
            case ButtonText.SIGN_OUT:
                return R.string.mqa_sign_up_text;
            default:
                return R.string.mqa_sign_in_text;
        }
    }

    public int getIconSize() {
        return mIconSize;
    }

    public int getContainerHeight() {
        return mContainerHeight;
    }

    public int getContainerWidth() {
        return mContainerWidth;
    }

    public Drawable getBackground() {
        return mBackground;
    }

    public int getButtonTextAppearance() {
        return mButtonTextAppearance;
    }

    public int getButtonTextColor() {
        return mButtonTextColor;
    }

    public String getButtonText() {
        return mButtonText;
    }
}
