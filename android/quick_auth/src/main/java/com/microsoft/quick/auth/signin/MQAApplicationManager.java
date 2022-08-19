package com.microsoft.quick.auth.signin;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.entity.MQASignInOptions;
import com.microsoft.quick.auth.signin.entity.MQASignInScope;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.signapplicationclient.MultipleApplicationHolder;
import com.microsoft.quick.auth.signin.signapplicationclient.SingleApplicationHolder;
import com.microsoft.quick.auth.signin.util.SystemUtil;

public class MQAApplicationManager {
    private MQAApplicationManager() {
    }

    private static class SingletonHolder {
        private static final MQAApplicationManager sInstance =
                new MQAApplicationManager();
    }

    public static MQAApplicationManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private IAccountClientHolder mSingleApplication;
    private IAccountClientHolder mMultipleApplication;
    private String[] READ_SCOPE = new String[]{MQASignInScope.READ};
    private String mClientId;
    private String mRedirectUri;
    private String CLIENT_ID_KEY = "com.microsoft.quick.auth.signin.clientId";
    private String REDIRECT_URI_KEY = "com.microsoft.quick.auth.signin.redirectUri";

    public void init(Context context) {
        mSingleApplication = new SingleApplicationHolder(context,
                new MQASignInOptions.Builder()
                        .setScopes(READ_SCOPE)
                        .setConfigRes(R.raw.auth_config_single_account)
                        .setClientId(getClientId(context))
                        .setRedirectUri(getRedirectUrl(context))
                        .build());
        mMultipleApplication = new MultipleApplicationHolder(context,
                new MQASignInOptions.Builder()
                        .setScopes(READ_SCOPE)
                        .setConfigRes(R.raw.auth_config_multiple_account)
                        .setClientId(getClientId(context))
                        .setRedirectUri(getRedirectUrl(context))
                        .build());
    }

    @Nullable
    public String getClientId(Context context) {
        if (!TextUtils.isEmpty(mClientId)) return mClientId;
        mClientId = SystemUtil.getAppMetaDataString(context, CLIENT_ID_KEY, null);
        return mClientId;
    }

    @Nullable
    public String getRedirectUrl(Context context) {
        if (!TextUtils.isEmpty(mRedirectUri)) return mRedirectUri;
        mRedirectUri = SystemUtil.getAppMetaDataString(context, REDIRECT_URI_KEY, null);
        return mRedirectUri;
    }

    public IAccountClientHolder getSignInApplication(boolean isSingle) {
        if (isSingle) {
            return mSingleApplication;
        } else {
            return mMultipleApplication;
        }
    }
}
