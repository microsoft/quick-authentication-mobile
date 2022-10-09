//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.view;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.microsoft.quickauth.signin.MSQAAccountInfo;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.R;
import com.microsoft.quickauth.signin.callback.OnCompleteListener;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricListener;
import com.microsoft.quickauth.signin.internal.metric.MSQASignInMetricListener;

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
  private OnCompleteListener<MSQAAccountInfo> mListener;
  private OnCompleteListener<MSQAAccountInfo> mInternalListener;
  private MSQAMetricController mController;

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
    // add icon view
    mSignInIcon = new ImageView(context);
    addView(mSignInIcon, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    mSignInIcon.setScaleType(ImageView.ScaleType.FIT_XY);
    mSignInIcon.setImageDrawable(getResources().getDrawable(R.drawable.msqa_sign_in_button_icon));
    // add text view
    mSignInText = new TextView(context);
    LayoutParams textLayoutParams =
        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    textLayoutParams.setMargins(
        getResources().getDimensionPixelSize(R.dimen.msqa_sign_in_button_text_padding), 0, 0, 0);
    addView(mSignInText, textLayoutParams);
    mSignInContainer = this;

    mDefaultWidth = getResources().getDimensionPixelSize(R.dimen.msqa_sign_in_button_width);
    setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            onButtonClick();
          }
        });

    regardViewAsButton(this);
    mSignInIcon.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
    mSignInText.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
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
      @NonNull OnCompleteListener<MSQAAccountInfo> onCompleteListener) {
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
    mSignInContainer.setContentDescription(getResources().getString(getButtonText()));
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
    ViewGroup.MarginLayoutParams iconViewLayoutParams =
        (MarginLayoutParams) mSignInIcon.getLayoutParams();
    if (mButtonType != ButtonType.ICON) {
      if (mButtonLogoAlignment == ButtonLogoAlignment.CENTER) {
        iconViewLayoutParams.setMargins(0, 0, 0, 0);
        mSignInText.setGravity(Gravity.CENTER);
        mSignInContainer.setGravity(Gravity.CENTER);
      } else {
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
      mInternalListener =
          MSQAMetricListener.wrapperIfNeeded(
              mListener,
              () -> {
                mController = new MSQAMetricController(MSQAMetricEvent.BUTTON_SIGN_IN);
                return new MSQASignInMetricListener<>(mController, mListener, true);
              });

      mClient.signIn(mActivity, mInternalListener);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int resultWidth;
    int resultHeight;
    int defaultHeight = getDefaultHeight();
    if (mButtonType == ButtonType.ICON) {
      resultWidth = defaultHeight;
      resultHeight = defaultHeight;
    } else {
      resultWidth = measureDimension(mDefaultWidth, widthMeasureSpec);
      resultHeight = measureDimension(defaultHeight, heightMeasureSpec);
    }
    super.onMeasure(
        MeasureSpec.makeMeasureSpec(resultWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(resultHeight, MeasureSpec.EXACTLY));
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
        // ensure view min size
        result = Math.max(specSize, defaultSize);
        break;
      case MeasureSpec.AT_MOST:
        result = defaultSize;
        break;
    }
    return result;
  }

  private Drawable getContainerBackground() {
    int radius = getBackgroundRadius();
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
      gradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(radius));
    }
    return drawable;
  }

  private int getBackgroundRadius() {
    if (mButtonShape == ButtonShape.ROUNDED) {
      return R.dimen.msqa_sign_in_button_radius_round;
    } else if (mButtonShape == ButtonShape.PILL) {
      return R.dimen.msqa_sign_in_button_radius_pill;
    }
    return R.dimen.msqa_sign_in_button_radius_square;
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

  /**
   * This method will set a {@link android.view.View.AccessibilityDelegate} on giving view.
   *
   * @param view the view to override a11y class to a Button, normally an {@link TextView}
   */
  public void regardViewAsButton(@Nullable View view) {
    if (view == null) {
      return;
    }
    ViewCompat.setAccessibilityDelegate(
        view,
        new AccessibilityDelegateCompat() {
          @Override
          public void onInitializeAccessibilityNodeInfo(
              View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClassName(Button.class.getName());
          }
        });
  }
}
