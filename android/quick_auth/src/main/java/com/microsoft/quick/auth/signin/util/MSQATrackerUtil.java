package com.microsoft.quick.auth.signin.util;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.logger.MSQALogger;

public class MSQATrackerUtil {
    private static final String TAG = MSQATrackerUtil.class.getSimpleName();
    private final @NonNull
    String mFrom;
    private final long mCurrentTime;

    public MSQATrackerUtil(String from) {
        mFrom = from;
        mCurrentTime = System.currentTimeMillis();
    }

    public void track(String tag, String message) {
        MSQALogger.getInstance().verbose(TAG + "-mFrom=" + mFrom + "," + tag, message);
    }
}
