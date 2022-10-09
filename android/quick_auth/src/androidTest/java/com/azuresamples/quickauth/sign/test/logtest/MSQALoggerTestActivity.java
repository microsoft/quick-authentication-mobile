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
package com.azuresamples.quickauth.sign.test.logtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.logger.ILogger;
import com.microsoft.quickauth.signin.logger.LogLevel;
import com.microsoft.quickauth.signin.test.R;

public class MSQALoggerTestActivity extends Activity {
  private final String TAG = "MSQALoggerTestActivity";
  private ILogger mTestLogger;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.msqa_logger_test_activity);
  }

  public void testVerboseLevel(View v) {
    loggerTest(LogLevel.VERBOSE);
  }

  public void testInfoLevel(View v) {
    loggerTest(LogLevel.INFO);
  }

  public void testWarnLevel(View v) {
    loggerTest(LogLevel.WARN);
  }

  public void testErrorLevel(View v) {
    loggerTest(LogLevel.ERROR);
  }

  public void setTestLogger(ILogger logger) {
    mTestLogger = logger;
  }

  private void loggerTest(@LogLevel int level) {
    MSQASignInClient.setLogLevel(level);
    MSQASignInClient.setExternalLogger(
        (logLevel, message) -> {
          if (mTestLogger != null) mTestLogger.log(logLevel, message);
        });
    sendAllLevelLog();
  }

  private void sendAllLevelLog() {
    MSQALogger.getInstance().verbose(TAG, "verbose test");
    MSQALogger.getInstance().info(TAG, "info test");
    MSQALogger.getInstance().warn(TAG, "warn test");
    MSQALogger.getInstance().error(TAG, "error test", null);
  }
}
