package com.microsoft.quick.auth.signin.internal.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quick.auth.signin.AccountInfo;

public class MSQAAccountInfoInternal implements AccountInfo {
  private String mAccessToken;
  private String mFullName;
  private String mUserName;
  private String mId;
  private Bitmap mBitmapPhoto;
  private String mBase64Photo;
  private byte[] mUserPhotoBytes;
  private IAccount mIAccount;

  @NonNull
  public String getAccessToken() {
    return mAccessToken;
  }

  @Nullable
  @Override
  public String getFullName() {
    return mFullName;
  }

  @Nullable
  @Override
  public String getUserName() {
    return mUserName;
  }

  @NonNull
  @Override
  public String getId() {
    return !TextUtils.isEmpty(mId) ? mId : "";
  }

  @Nullable
  @Override
  public Bitmap getBitmapPhoto() {
    if (mBitmapPhoto != null) return mBitmapPhoto;
    if (mUserPhotoBytes == null) return null;
    mBitmapPhoto = BitmapFactory.decodeByteArray(mUserPhotoBytes, 0, mUserPhotoBytes.length);
    return mBitmapPhoto;
  }

  @Nullable
  @Override
  public String getBase64Photo() {
    if (mBase64Photo != null) return mBase64Photo;
    if (mUserPhotoBytes == null) return null;
    mBase64Photo = Base64.encodeToString(mUserPhotoBytes, Base64.NO_WRAP);
    return mBase64Photo;
  }

  public void setUserPhoto(byte[] userPhoto) {
    this.mUserPhotoBytes = userPhoto;
  }

  public void setAccessToken(String accessToken) {
    this.mAccessToken = accessToken;
  }

  public void setFullName(String fullName) {
    this.mFullName = fullName;
  }

  public void setUserName(String userName) {
    this.mUserName = userName;
  }

  public void setId(String id) {
    this.mId = id;
  }

  public IAccount getIAccount() {
    return mIAccount;
  }

  public void setIAccount(IAccount account) {
    this.mIAccount = account;
  }

  public static MSQAAccountInfoInternal getAccount(IAuthenticationResult authenticationResult) {
    MSQAAccountInfoInternal account = new MSQAAccountInfoInternal();
    account.setAccessToken(authenticationResult.getAccessToken());
    IAccount iAccount = authenticationResult.getAccount();
    account.setIAccount(iAccount);
    if (iAccount.getClaims() != null && iAccount.getClaims().containsKey("name")) {
      Object name = iAccount.getClaims().get("name");
      account.setFullName(name != null ? name.toString() : null);
    }
    account.setUserName(iAccount.getUsername());
    return account;
  }
}
