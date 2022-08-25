package com.microsoft.quick.auth.signin;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.microsoft.quick.auth.signin.entity.MQASignInOptions;
import com.microsoft.quick.auth.signin.entity.MSQAAccountMode;
import com.microsoft.quick.auth.signin.entity.MSQASignInScope;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientHolder;
import com.microsoft.quick.auth.signin.signapplicationclient.MultipleApplicationHolder;
import com.microsoft.quick.auth.signin.signapplicationclient.SingleApplicationHolder;
import com.microsoft.quick.auth.signin.util.SystemUtil;

public class MSQAApplicationManager {
    private MSQAApplicationManager() {
    }

    private static class SingletonHolder {
        private static final MSQAApplicationManager sInstance =
                new MSQAApplicationManager();
    }

    public static MSQAApplicationManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private IAccountClientHolder mSingleApplication;
    private IAccountClientHolder mMultipleApplication;
    private String[] READ_SCOPE = new String[]{MSQASignInScope.READ};
    private String mClientId;
    private String mRedirectUri;
    private String CLIENT_ID_KEY = "com.microsoft.quick.auth.signin.clientId";
    private String REDIRECT_URI_KEY = "com.microsoft.quick.auth.signin.redirectUri";

    public void init(Context context) {
        mSingleApplication = new SingleApplicationHolder(context,
                new MQASignInOptions.Builder()
                        .setScopes(READ_SCOPE)
                        .setConfigRes(R.raw.msqa_basic_config)
                        .setClientId(getClientId(context))
                        .setRedirectUri(getRedirectUrl(context))
                        .setAccountMode(MSQAAccountMode.SINGLE)
                        .build());
        mMultipleApplication = new MultipleApplicationHolder(context,
                new MQASignInOptions.Builder()
                        .setScopes(READ_SCOPE)
                        .setConfigRes(R.raw.msqa_basic_config)
                        .setClientId(getClientId(context))
                        .setRedirectUri(getRedirectUrl(context))
                        .setAccountMode(MSQAAccountMode.MULTIPLE)
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
