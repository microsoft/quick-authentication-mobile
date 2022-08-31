package com.microsoft.quick.auth.signin.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;

public class MSQAAccountInfo implements AccountInfo {
    private String mAccessToken;
    private String mFullName;
    private String mUserName;
    private String mId;
    private String mUserPhoto;
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

    @Nullable
    @Override
    public String getId() {
        return mId;
    }

    @Nullable
    @Override
    public String getPhoto() {
        return mUserPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.mUserPhoto = userPhoto;
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

    public static MSQAAccountInfo getAccount(IAuthenticationResult authenticationResult) {
        MSQAAccountInfo account = new MSQAAccountInfo();
        account.setAccessToken(authenticationResult.getAccessToken());
        IAccount iAccount = authenticationResult.getAccount();
        account.setIAccount(iAccount);
        account.setId(account.getId());
        if (iAccount.getClaims() != null && iAccount.getClaims().containsKey("name")) {
            Object name = iAccount.getClaims().get("name");
            account.setFullName(name != null ? name.toString() : null);
        }
        account.setUserName(iAccount.getUsername());
        return account;
    }
}
