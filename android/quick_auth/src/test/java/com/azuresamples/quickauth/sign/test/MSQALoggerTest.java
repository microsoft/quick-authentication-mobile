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
package com.azuresamples.quickauth.sign.test;

import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.logger.LogLevel;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MSQALoggerTest {
  private final String TAG = MSQALoggerTest.class.getSimpleName();

  @Before
  public void setup() {
    MSQASignInClient.setEnableLogcatLog(true);
  }

  @Test
  public void testVerboseLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(4);
    MSQASignInClient.setLogLevel(LogLevel.VERBOSE);
    MSQASignInClient.setExternalLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.VERBOSE);
          latch.countDown();
        });
    sendAllLevelLog();
    latch.await();
  }

  @Test
  public void testInfoLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(3);
    MSQASignInClient.setLogLevel(LogLevel.INFO);
    MSQASignInClient.setExternalLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.INFO);
          latch.countDown();
        });
    sendAllLevelLog();
    latch.await();
  }

  @Test
  public void testWarnLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(2);
    MSQASignInClient.setLogLevel(LogLevel.WARN);
    MSQASignInClient.setExternalLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.WARN);
          latch.countDown();
        });
    sendAllLevelLog();
    latch.await();
  }

  @Test
  public void testErrorLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    MSQASignInClient.setLogLevel(LogLevel.ERROR);
    MSQASignInClient.setExternalLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.ERROR);
          latch.countDown();
        });
    sendAllLevelLog();
    latch.await();
  }

  private void sendAllLevelLog() {
    MSQALogger.getInstance().verbose(TAG, "verbose test");
    MSQALogger.getInstance().info(TAG, "info test");
    MSQALogger.getInstance().warn(TAG, "warn test");
    MSQALogger.getInstance().error(TAG, "error test", null);
  }
}
