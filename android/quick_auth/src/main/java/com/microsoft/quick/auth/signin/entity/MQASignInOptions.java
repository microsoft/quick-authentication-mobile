package com.microsoft.quick.auth.signin.entity;

public class MQASignInOptions {
    private final String mClientId;

    private final String mRedirectUri;

    private String[] mScopes;
    private String mLoginHint;
    private String mAccountMode;

    private int mConfigRes;

    public MQASignInOptions(MQASignInOptions.Builder builder) {
        mClientId = builder.mClientId;
        mRedirectUri = builder.mRedirectUri;
        mConfigRes = builder.mConfigRes;
        mScopes = builder.mScopes;
        mAccountMode = builder.mAccountMode;
        mLoginHint = builder.mLoginHint;
        if (mScopes == null || mScopes.length == 0) {
            mScopes = new String[]{MSQASignInScope.READ};
        }
    }

    public String getClientId() {
        return mClientId;
    }

    public String[] getScopes() {
        return mScopes;
    }

    public String getRedirectUri() {
        return mRedirectUri;
    }

    public String getAccountMode() {
        return mAccountMode;
    }

    public int getConfigRes() {
        return mConfigRes;
    }

    public String getLoginHint() {
        return mLoginHint;
    }

    public static final class Builder {
        private String mClientId;
        private String mRedirectUri;
        private int mConfigRes;
        private String[] mScopes;
        private String mLoginHint;
        private String mAccountMode;

        public Builder() {
            mAccountMode = MSQAAccountMode.SINGLE;
        }

        public Builder setClientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public Builder setRedirectUri(String redirectUri) {
            mRedirectUri = redirectUri;
            return this;
        }

        public Builder setLoginHint(String loginHint) {
            mLoginHint = loginHint;
            return this;
        }

        public Builder setConfigRes(int configRes) {
            mConfigRes = configRes;
            return this;
        }

        public Builder setAccountMode(String accountMode) {
            mAccountMode = accountMode;
            return this;
        }

        public Builder setScopes(String[] scopes) {
            mScopes = scopes;
            return this;
        }

        public MQASignInOptions build() {
            return new MQASignInOptions(this);
        }
    }
}
