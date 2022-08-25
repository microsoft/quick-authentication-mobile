package com.microsoft.quick.auth.signin;

public class MQASignInOptions {
    private final String mClientId;

    private final String mRedirectUri;

    public MQASignInOptions(MQASignInOptions.Builder builder) {
        mClientId = builder.mClientId;
        mRedirectUri = builder.mRedirectUri;
    }

    public String getClientId() {
        return mClientId;
    }


    public String getRedirectUri() {
        return mRedirectUri;
    }

    public static final class Builder {
        private String mClientId;
        private String mRedirectUri;

        public Builder setClientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public Builder setRedirectUri(String redirectUri) {
            mRedirectUri = redirectUri;
            return this;
        }

        public MQASignInOptions build() {
            return new MQASignInOptions(this);
        }
    }
}
