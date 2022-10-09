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

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.azuresamples.quickauth.sign.test.MSQATestSafeCountDownLatch;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.logger.LogLevel;
import com.microsoft.quickauth.signin.test.R;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// MSQA log test class.
@RunWith(AndroidJUnit4.class)
public class MSQALoggerActivityTest {

  @Rule
  public ActivityTestRule activityTestRule = new ActivityTestRule(MSQALoggerTestActivity.class);

  private MSQALoggerTestActivity mActivity;

  @Before
  public void setup() {
    mActivity = (MSQALoggerTestActivity) activityTestRule.getActivity();
    MSQASignInClient.setEnableLogcatLog(true);
  }

  @Test
  public void testVerboseLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(4);
    mActivity.setTestLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.VERBOSE);
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_verbose_level)).perform(click());
    latch.await();
  }

  @Test
  public void testInfoLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(3);
    mActivity.setTestLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.INFO);
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_info_level)).perform(click());
    latch.await();
  }

  @Test
  public void testWarnLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(2);
    mActivity.setTestLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.WARN);
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_warn_level)).perform(click());
    latch.await();
  }

  @Test
  public void testErrorLevel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestLogger(
        (logLevel, message) -> {
          Assert.assertEquals(true, logLevel >= LogLevel.ERROR);
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_error_level)).perform(click());
    latch.await();
  }
}
