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
package com.azuresamples.quickauth.sign.test.apitest;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.azuresamples.quickauth.sign.test.MSQATestSafeCountDownLatch;
import com.azuresamples.quickauth.sign.test.mock.MSQATestMockUtil;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.error.MSQANoAccountException;
import com.microsoft.quickauth.signin.error.MSQANoScopeException;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetric;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricMessage;
import com.microsoft.quickauth.signin.test.R;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// MSQA Api test class.
@RunWith(AndroidJUnit4.class)
public class MSQAAPIActivityTest {

  @Rule public ActivityTestRule activityTestRule = new ActivityTestRule(MSQAApiTestActivity.class);

  private MSQAApiTestActivity mActivity;

  @Before
  public void setup() {
    mActivity = (MSQAApiTestActivity) activityTestRule.getActivity();
  }

  @Test
  public void testSignInApiSuccess() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(o);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_IN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));

          List<MSQAMetric.MetricEvent> extEvent = controller.getExtEvent();
          Assert.assertTrue(extEvent != null && extEvent.size() > 0);
          MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
          Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_SUCCESS));
          Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.START_SIGN_IN_API));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_sign_in_api_success)).perform(click());
    latch.await();
  }

  @Test
  public void testSignInApiCancel() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertEquals(true, error instanceof MSQACancelException);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_IN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.CANCELED));

          List<MSQAMetric.MetricEvent> extEvent = controller.getExtEvent();
          Assert.assertTrue(extEvent != null && extEvent.size() > 0);
          MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
          Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_FAILURE));
          Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.START_SIGN_IN_API));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_sign_in_api_cancel)).perform(click());
    latch.await();
  }

  @Test
  public void testSignInApiFailure() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(error);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_IN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.FAILURE));

          List<MSQAMetric.MetricEvent> extEvent = controller.getExtEvent();
          Assert.assertTrue(extEvent != null && extEvent.size() > 0);
          MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
          Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_FAILURE));
          Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.START_SIGN_IN_API));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_sign_in_api_failure)).perform(click());
    latch.await();
  }

  @Test
  public void testSignOutApiWithAccount() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertEquals(o, true);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_OUT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));

          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_sign_out_api_with_account)).perform(click());
    latch.await();
  }

  @Test
  public void testSignOutApiWithoutAccount() {
    MSQATestMockUtil.removeAccount(mActivity);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(error);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_OUT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.FAILURE));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_sign_out_api_without_account)).perform(click());
    latch.await();
  }

  @Test
  public void testGetCurrentAccountWithAccount() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(o);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.GET_CURRENT_ACCOUNT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_get_account_api_with_account)).perform(click());
    latch.await();
  }

  @Test
  public void testGetCurrentAccountWithoutAccount() {
    MSQATestMockUtil.removeAccount(mActivity);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertEquals(true, error instanceof MSQANoAccountException);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.GET_CURRENT_ACCOUNT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.NO_ACCOUNT_PRESENT));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_get_account_api_without_account)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenApiSuccess() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(o);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_api_success)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenApiNoAccountFailure() {
    MSQATestMockUtil.removeAccount(mActivity);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(error);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.NO_ACCOUNT_PRESENT));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_api_failure)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenApiCancel() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertEquals(true, error instanceof MSQACancelException);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.CANCELED));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_api_cancel)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenApiNoScope() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertEquals(true, error instanceof MSQANoScopeException);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.NO_SCOPES));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_api_no_scope)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenSilentApiSuccess() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(o);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_silent_api_success)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenSilentApiNoAccountFailure() {
    MSQATestMockUtil.removeAccount(mActivity);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertNotNull(error);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.NO_ACCOUNT_PRESENT));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_silent_api_failure)).perform(click());
    latch.await();
  }

  @Test
  public void testAcquireTokenSilentApiCancel() {
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    mActivity.setTestResultListener(
        (controller, o, error) -> {
          // assert value
          Assert.assertEquals(true, error instanceof MSQACancelException);
          // assert metric
          MSQAMetric.MetricEvent event = controller.getEvent();
          Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT));
          Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.CANCELED));
          latch.countDown();
        });
    Espresso.onView(withId(R.id.test_acquire_token_silent_api_cancel)).perform(click());
    latch.await();
  }
}
