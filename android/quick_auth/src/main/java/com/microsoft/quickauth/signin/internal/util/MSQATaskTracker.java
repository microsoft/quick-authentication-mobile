//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.internal.util;

import static com.microsoft.quickauth.signin.internal.logger.LogLevel.ERROR;
import static com.microsoft.quickauth.signin.internal.logger.LogLevel.INFO;
import static com.microsoft.quickauth.signin.internal.logger.LogLevel.VERBOSE;
import static com.microsoft.quickauth.signin.internal.logger.LogLevel.WARN;

import android.content.Context;
import androidx.annotation.NonNull;
import com.microsoft.quickauth.signin.R;
import com.microsoft.quickauth.signin.internal.logger.LogLevel;
import com.microsoft.quickauth.signin.internal.logger.MSQALogger;

public class MSQATaskTracker {
  private static final String TAG = "MSQATracker";
  private final @NonNull String mFrom;
  private final String mTrackerFormatString;

  public MSQATaskTracker(@NonNull Context context, @NonNull String from) {
    mFrom = from;
    mTrackerFormatString = context.getString(R.string.msqa_tracker_format_string);
  }

  public void track(String tag, @LogLevel int level, String message, Throwable throwable) {
    String generateTag = String.format(mTrackerFormatString, TAG, mFrom, tag);
    switch (level) {
      case ERROR:
        MSQALogger.getInstance().error(generateTag, message, throwable);
        break;
      case WARN:
        MSQALogger.getInstance().warn(generateTag, message);
        break;
      case INFO:
        MSQALogger.getInstance().info(generateTag, message);
        break;
      case VERBOSE:
        MSQALogger.getInstance().verbose(generateTag, message);
        break;
    }
  }
}
