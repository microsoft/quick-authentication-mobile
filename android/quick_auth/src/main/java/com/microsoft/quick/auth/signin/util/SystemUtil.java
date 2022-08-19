package com.microsoft.quick.auth.signin.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class SystemUtil {
    /**
     * Get meta data string
     *
     * @param metaName     meta-data key.
     * @param defaultValue If get meta-data failed will return default value.
     * @return meta-data info
     */
    public static String getAppMetaDataString(Context context, String metaName,
                                              String defaultValue) {
        try {
            return context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString(metaName, defaultValue);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
