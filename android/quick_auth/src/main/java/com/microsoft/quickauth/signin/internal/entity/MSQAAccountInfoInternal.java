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
package com.microsoft.quickauth.signin.internal.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.quickauth.signin.AccountInfo;

public class MSQAAccountInfoInternal implements AccountInfo {
  private String mFullName;
  private String mUserName;
  private String mId;
  private Bitmap mBitmapPhoto;
  private String mBase64Photo;
  private byte[] mUserPhotoBytes;

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

  public void setFullName(String fullName) {
    this.mFullName = fullName;
  }

  public void setUserName(String userName) {
    this.mUserName = userName;
  }

  public void setId(String id) {
    this.mId = id;
  }

  public static MSQAAccountInfoInternal getAccount(IAuthenticationResult authenticationResult) {
    MSQAAccountInfoInternal account = new MSQAAccountInfoInternal();
    IAccount iAccount = authenticationResult.getAccount();
    if (iAccount.getClaims() != null && iAccount.getClaims().containsKey("name")) {
      Object name = iAccount.getClaims().get("name");
      account.setFullName(name != null ? name.toString() : null);
    }
    account.setUserName(iAccount.getUsername());
    return account;
  }
}
