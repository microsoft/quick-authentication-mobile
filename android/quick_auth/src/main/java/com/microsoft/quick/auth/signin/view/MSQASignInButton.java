package com.microsoft.quick.auth.signin.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.quick.auth.signin.AccountInfo;
import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.R;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;

public class MSQASignInButton extends LinearLayout {

  private @ButtonTheme int mButtonTheme;
  private @ButtonLogoAlignment int mButtonLogoAlignment;
  private @ButtonShape int mButtonShape;
  private @ButtonSize int mButtonSize;
  private @ButtonText int mButtonText;
  private @ButtonType int mButtonType;
  private LinearLayout mSignInContainer;
  private ImageView mSignInIcon;
  private TextView mSignInText;
  private int mDefaultWidth;
  private Activity mActivity;
  private MSQASignInClient mClient;
  private OnCompleteListener<AccountInfo> mListener;

  public MSQASignInButton(@NonNull Context context) {
    this(context, null);
  }

  public MSQASignInButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MSQASignInButton(
      @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(context, attrs, defStyleAttr);
    init(context);
    updateButtonView();
  }

  private void init(Context context) {
    setGravity(Gravity.CENTER_VERTICAL);
    setOrientation(HORIZONTAL);
    mSignInIcon = new ImageView(context);
    addView(mSignInIcon, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    mSignInIcon.setScaleType(ImageView.ScaleType.FIT_XY);
    mSignInIcon.setImageDrawable(getResources().getDrawable(R.drawable.msqa_sign_in_button_icon));

    mSignInText = new TextView(context);
    addView(mSignInText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    mSignInContainer = this;

    mDefaultWidth = getResources().getDimensionPixelSize(R.dimen.msqa_sign_in_button_width);
    setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            onButtonClick();
          }
        });
  }

  private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MSQASignInButton);
    mButtonTheme =
        typedArray.getInt(R.styleable.MSQASignInButton_msqa_button_theme, ButtonTheme.DARK);
    mButtonLogoAlignment =
        typedArray.getInt(
            R.styleable.MSQASignInButton_msqa_button_logo_alignment, ButtonLogoAlignment.LEFT);
    mButtonShape =
        typedArray.getInt(R.styleable.MSQASignInButton_msqa_button_shape, ButtonShape.RECTANGULAR);
    mButtonSize =
        typedArray.getInt(R.styleable.MSQASignInButton_msqa_button_size, ButtonSize.LARGE);
    mButtonText =
        typedArray.getInt(R.styleable.MSQASignInButton_msqa_button_text, ButtonText.SIGN_IN_WITH);
    mButtonType =
        typedArray.getInt(R.styleable.MSQASignInButton_msqa_button_type, ButtonType.STANDARD);
    typedArray.recycle();
  }

  /**
   * @param activity Activity that is used as the parent activity for launching sign in page.
   * @param onCompleteListener A callback to be invoked when sign in success and will return sign in
   *     account info.
   */
  public void setSignInCallback(
      @NonNull Activity activity,
      @NonNull MSQASignInClient client,
      @NonNull OnCompleteListener<AccountInfo> onCompleteListener) {
    mActivity = activity;
    mClient = client;
    mListener = onCompleteListener;
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
    requestLayout();
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
    requestLayout();
    return this;
  }

  private void updateButtonView() {
    if (mSignInIcon == null || mSignInText == null) return;
    if (mButtonType == ButtonType.ICON) {
      mSignInText.setVisibility(View.GONE);
    } else {
      mSignInText.setVisibility(View.VISIBLE);
    }
    // set container
    mSignInContainer.setBackground(getContainerBackground());
    // set icon
    ViewGroup.LayoutParams iconLayoutParams = mSignInIcon.getLayoutParams();
    if (iconLayoutParams != null) {
      iconLayoutParams.width = getIconSize();
      iconLayoutParams.height = getIconSize();
      mSignInIcon.setLayoutParams(iconLayoutParams);
    }
    // set text
    mSignInText.setTextAppearance(getContext(), getButtonTextTextAppearance());
    mSignInText.setTextColor(getButtonTextColor());
    mSignInText.setText(getButtonText());

    // set alignment
    LinearLayout.MarginLayoutParams buttonTextLayoutParams =
        (MarginLayoutParams) mSignInText.getLayoutParams();
    ViewGroup.MarginLayoutParams iconViewLayoutParams =
        (MarginLayoutParams) mSignInIcon.getLayoutParams();
    if (mButtonType != ButtonType.ICON) {
      if (mButtonLogoAlignment == ButtonLogoAlignment.CENTER) {
        buttonTextLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        buttonTextLayoutParams.setMargins(
            getResources().getDimensionPixelSize(R.dimen.msqa_sign_in_button_text_padding),
            0,
            0,
            0);
        iconViewLayoutParams.setMargins(0, 0, 0, 0);
        mSignInText.setGravity(Gravity.CENTER);
        mSignInContainer.setGravity(Gravity.CENTER);
      } else {
        buttonTextLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        buttonTextLayoutParams.setMargins(
            getResources().getDimensionPixelSize(R.dimen.msqa_sign_in_button_text_padding),
            0,
            0,
            0);
        iconViewLayoutParams.setMargins(
            getResources().getDimensionPixelSize(R.dimen.msqa_sign_in_button_icon_padding),
            0,
            0,
            0);
        mSignInText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        mSignInContainer.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
      }
    } else {
      iconViewLayoutParams.setMargins(0, 0, 0, 0);
      mSignInContainer.setGravity(Gravity.CENTER);
    }
  }

  private void onButtonClick() {
    if (mActivity != null && mClient != null && mListener != null) {
      mClient.signIn(mActivity, mListener);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int defaultHeight = getDefaultHeight();
    if (mButtonType == ButtonType.ICON) {
      setMeasuredDimension(defaultHeight, defaultHeight);
    } else {
      setMeasuredDimension(
          measureDimension(mDefaultWidth, widthMeasureSpec),
          measureDimension(defaultHeight, heightMeasureSpec));
    }
  }

  private int measureDimension(int defaultSize, int measureSpec) {
    int result = defaultSize;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    switch (specMode) {
      case MeasureSpec.UNSPECIFIED:
        result = specSize;
        break;
      case MeasureSpec.EXACTLY:
        result = specSize;
        break;
      case MeasureSpec.AT_MOST:
        result = defaultSize;
        break;
    }
    return result;
  }

  private Drawable getContainerBackground() {
    int radios = getBackgroundRadios();
    int backgroundRes = R.drawable.msqa_sign_in_button_background;
    Drawable drawable = getResources().getDrawable(backgroundRes);
    if (drawable instanceof GradientDrawable) {
      GradientDrawable gradientDrawable = (GradientDrawable) drawable;
      if (mButtonTheme == ButtonTheme.DARK) {
        gradientDrawable.setColor(getResources().getColor(R.color.msqa_sign_in_button_color_dark));
        gradientDrawable.setStroke(0, Color.TRANSPARENT);
      } else {
        gradientDrawable.setColor(getResources().getColor(R.color.msqa_sign_in_button_color_light));
        gradientDrawable.setStroke(
            getResources()
                .getDimensionPixelSize(R.dimen.msqa_sign_in_button_background_border_width),
            getResources().getColor(R.color.msqa_sign_in_button_border_light));
      }
      gradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(radios));
    }
    return drawable;
  }

  private int getBackgroundRadios() {
    if (mButtonShape == ButtonShape.ROUNDED) {
      return R.dimen.msqa_sign_in_button_radios_round;
    } else if (mButtonShape == ButtonShape.PILL) {
      return R.dimen.msqa_sign_in_button_radios_pill;
    }
    return R.dimen.msqa_sign_in_button_radios_square;
  }

  private int getIconSize() {
    int sizeResource = R.dimen.msqa_sign_in_button_icon_size_large;
    if (ButtonSize.SMALL == mButtonSize) {
      sizeResource = R.dimen.msqa_sign_in_button_icon_size_small;
    } else if (ButtonSize.MEDIUM == mButtonSize) {
      sizeResource = R.dimen.msqa_sign_in_button_icon_size_medium;
    }
    return getResources().getDimensionPixelSize(sizeResource);
  }

  private int getDefaultHeight() {
    int heightResource = R.dimen.msqa_sign_in_button_height_large;
    if (mButtonSize == ButtonSize.SMALL) {
      heightResource = R.dimen.msqa_sign_in_button_height_small;
    } else if (mButtonSize == ButtonSize.MEDIUM) {
      heightResource = R.dimen.msqa_sign_in_button_height_medium;
    }
    return getResources().getDimensionPixelSize(heightResource);
  }

  private int getButtonTextTextAppearance() {
    if (mButtonSize == ButtonSize.SMALL) {
      return R.style.MSQATextAppearance_SignInButton_Small;
    } else if (mButtonSize == ButtonSize.MEDIUM) {
      return R.style.MSQATextAppearance_SignInButton_Medium;
    }
    return R.style.MSQATextAppearance_SignInButton_Large;
  }

  private int getButtonTextColor() {
    int color = R.color.msqa_sign_in_button_text_color_light;
    if (ButtonTheme.DARK == mButtonTheme) {
      color = R.color.msqa_sign_in_button_text_color_dark;
    }
    return getResources().getColor(color);
  }

  private int getButtonText() {
    if (mButtonText == ButtonText.SIGNUP_WITH) {
      return R.string.msqa_signup_with_text;
    } else if (mButtonText == ButtonText.SIGNIN) {
      return R.string.msqa_signin_text;
    } else if (mButtonText == ButtonText.CONTINUE_WITH) {
      return R.string.msqa_continue_with_text;
    } else {
      return R.string.msqa_signin_with_text;
    }
  }
}
