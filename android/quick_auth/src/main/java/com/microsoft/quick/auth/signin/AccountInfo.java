package com.microsoft.quick.auth.signin;

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
   * @return User photo base64 encoded data.
   */
  @Nullable
  String getPhoto();
}
