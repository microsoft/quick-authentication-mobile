package com.microsoft.quick.auth.signin.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.microsoft.quick.auth.signin.entity.MQASignInOptions;
import com.microsoft.quick.auth.signin.logger.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private static final String SIGN_CONFIG_FILE_DIR = "";

    public static @NonNull
    String getSignInConfigsDir(Context context) {
        File signInDir =
                new File(context.getApplicationContext().getFilesDir(), SIGN_CONFIG_FILE_DIR);
        if (!signInDir.exists()) {
            try {
                signInDir.mkdir();
            } catch (SecurityException e) {
                LogUtil.error(TAG, "Unable to create sign in directory: ", e);
            }
        }
        return signInDir.getAbsolutePath();
    }

    public static File getSignInConfigFile(Context context, String fileName) {
        String configDir = getSignInConfigsDir(context);
        return new File(configDir, fileName);
    }

    public static boolean removeSignInConfigFile(Context context, String fileName) {
        File file = getSignInConfigFile(context, fileName);
        if (file != null && file.exists()) return file.delete();
        return false;
    }

    public static boolean isEmptyFile(File file) {
        return !(file != null && file.exists() && (file.length() > 0));
    }

    @WorkerThread
    public static File reWriteConfig(Context context, String configFileName,
                                     MQASignInOptions options) {
        File configFile = getSignInConfigFile(context, configFileName);
        if (configFile != null && configFile.exists()) return configFile;

        final InputStream configStream =
                context.getResources().openRawResource(options.getConfigRes());
        byte[] buffer;

        try {
            buffer = new byte[configStream.available()];
            configStream.read(buffer);
            final String config = new String(buffer);
            JSONObject jsonObject = new JSONObject(config);
            jsonObject.put("client_id", options.getClientId());
            jsonObject.put("redirect_uri", options.getRedirectUri());
            jsonObject.put("account_mode", options.getAccountMode());
            writeToFile(configFile, jsonObject.toString());
        } catch (IOException exception) {
            exception.printStackTrace();
            LogUtil.error(TAG, "reWriteConfig io exception.", exception);
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.error(TAG, "reWriteConfig json exception.", e);
        } finally {
            try {
                configStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
                LogUtil.error(TAG, "reWriteConfig config stream close exception.", exception);
            }
        }
        return configFile;
    }

    private static void writeToFile(File file, String content) {
        if (TextUtils.isEmpty(content)) return;

        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(content);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.error(TAG, "writeToFile write file not found exception.", e);
        } catch (IOException exception) {
            exception.printStackTrace();
            LogUtil.error(TAG, "writeToFile write content exception.", exception);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                    LogUtil.error(TAG, "writeToFile close stream exception.", exception);
                }
            }
        }
    }
}
