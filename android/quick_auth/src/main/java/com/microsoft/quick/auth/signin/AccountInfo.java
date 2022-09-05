package com.microsoft.quick.auth.signin;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface AccountInfo {
  /**
   * @return User full name.
   */
  @Nullable
  String getFullName();

  /**
   * @return Email address or phone.
   */
  @Nullable
  String getUserName();

  /**
   * @return CID for MSA.
   */
  @NonNull
  String getId();

  /**
   * @return User photo bitmap data.
   */
  @Nullable
  Bitmap getBitmapPhoto();

  /**
   * @return User photo base64 encode data, recommend low memory to use this method.
   */
  @Nullable
  String getBase64Photo();
}
