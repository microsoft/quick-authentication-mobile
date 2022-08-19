package com.microsoft.quick.auth.signin.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;

public class MQAAccountInfo implements AccountInfo {
    private String mAuthority;
    private String mAccessToken;
    private String mFullName;
    private String mUserName;
    private String mId;
    private Bitmap mUserPhoto;
    private IAccount mIAccount;

    @NonNull
    protected String getAuthority() {
        return mAuthority;
    }

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
    public Bitmap getPhoto() {
        return mUserPhoto;
    }

    public void setUserPhoto(Bitmap userPhoto) {
        this.mUserPhoto = userPhoto;
    }

    public void setAuthority(String authority) {
        this.mAuthority = authority;
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

    public static MQAAccountInfo getAccount(IAuthenticationResult authenticationResult) {
        MQAAccountInfo account = new MQAAccountInfo();
        account.setAccessToken(authenticationResult.getAccessToken());
        IAccount iAccount = authenticationResult.getAccount();
        account.setIAccount(iAccount);
        account.setAuthority(iAccount.getAuthority());
        account.setId(account.getId());
        account.setUserName(iAccount.getUsername());
        return account;
    }
}
