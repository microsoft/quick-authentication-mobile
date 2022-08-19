package com.microsoft.quick.auth.signin.entity;

import android.graphics.Bitmap;

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
    @Nullable
    String getId();

    /**
     * @return User photo bitmap
     */
    @Nullable
    Bitmap getPhoto();
}
