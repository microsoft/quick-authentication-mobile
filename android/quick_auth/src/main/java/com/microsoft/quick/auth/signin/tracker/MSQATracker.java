package com.microsoft.quick.auth.signin.tracker;

import androidx.annotation.NonNull;

import com.microsoft.quick.auth.signin.logger.LogUtil;

public class MSQATracker {
    private final @NonNull
    String mFrom;
    private final long mCurrentTime;

    public MSQATracker(String from) {
        mFrom = from;
        mCurrentTime = System.currentTimeMillis();
    }

    public void track(String tag, String message) {
        LogUtil.verbose("MQATracker-mFrom=" + mFrom + "," + tag, message);
    }
}
