package com.azuresamples.quickauth.sign.test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.annotation.Nullable;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.microsoft.quickauth.signin.AccountInfo;
import com.microsoft.quickauth.signin.TokenResult;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetric;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricMessage;
import com.microsoft.quickauth.signin.internal.metric.MSQASignInMetricListener;
import com.microsoft.quickauth.signin.test.R;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

// MSQA telemetry test class.
@RunWith(AndroidJUnit4.class)
public class MSQATelemetryTest extends MSQABaseTest {

  private String[] mScopes = new String[] {"user.read"};

  @Test
  public void testSignInSuccessTelemetry() {
    Assert.assertNotNull(mClient);
    mSignInClientInternal.setFlag(MSQATestFlag.SUCCESS);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.signIn(
        mActivity,
        new MSQASignInMetricListener<AccountInfo>(
            new MSQAMetricController(MSQAMetricEvent.SIGN_IN), null, false) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_IN));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));

            List<MSQAMetric.MetricEvent> extEvent = mController.getExtEvent();
            Assert.assertTrue(extEvent != null && extEvent.size() > 0);
            MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
            Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_SUCCESS));
            Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.START_SIGN_IN_API));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSignInFailureTelemetry() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    mSignInClientInternal.setFlag(MSQATestFlag.ERROR);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.signIn(
        mActivity,
        new MSQASignInMetricListener<AccountInfo>(
            new MSQAMetricController(MSQAMetricEvent.SIGN_IN), null, false) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_IN));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.FAILURE));

            List<MSQAMetric.MetricEvent> extEvent = mController.getExtEvent();
            Assert.assertTrue(extEvent != null && extEvent.size() > 0);
            MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
            Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_FAILURE));
            Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.START_SIGN_IN_API));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSignInCancelTelemetry() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    mSignInClientInternal.setFlag(MSQATestFlag.CANCEL);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.signIn(
        mActivity,
        new MSQASignInMetricListener<AccountInfo>(
            new MSQAMetricController(MSQAMetricEvent.SIGN_IN), null, false) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_IN));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.CANCELED));

            List<MSQAMetric.MetricEvent> extEvent = mController.getExtEvent();
            Assert.assertTrue(extEvent != null && extEvent.size() > 0);
            MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
            Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_FAILURE));
            Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.START_SIGN_IN_API));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testButtonSignInTelemetry() {
    Assert.assertNotNull(mClient);
    Assert.assertNotNull(mSignInButton);
    mSignInClientInternal.setFlag(MSQATestFlag.SUCCESS);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mSignInButton.setSignInCallback(
        mActivity,
        mClient,
        new MSQASignInMetricListener<AccountInfo>(
            new MSQAMetricController(MSQAMetricEvent.BUTTON_SIGN_IN), null, true) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.BUTTON_SIGN_IN));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));

            List<MSQAMetric.MetricEvent> extEvent = mController.getExtEvent();
            Assert.assertTrue(extEvent != null && extEvent.size() > 0);
            MSQAMetric.MetricEvent signInEvent = extEvent.get(0);
            Assert.assertTrue(signInEvent.getEventName().equals(MSQAMetricEvent.SIGN_IN_SUCCESS));
            Assert.assertTrue(signInEvent.getMessage().equals(MSQAMetricMessage.SIGN_IN_BUTTON));
            latch.countDown();
          }
        });
    Espresso.onView(withId(R.id.signInButton)).perform(click());
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSignOutTelemetry() {
    Assert.assertNotNull(mClient);
    mSignInClientInternal.setFlag(MSQATestFlag.SUCCESS);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.signOut(
        new MSQATestMetricListener<Boolean>(
            new MSQAMetricController(MSQAMetricEvent.SIGN_OUT), null) {
          @Override
          public void onComplete(@Nullable Boolean aBoolean, @Nullable MSQAException error) {
            super.onComplete(aBoolean, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.SIGN_OUT));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetCurrentAccountTelemetry() {
    Assert.assertNotNull(mClient);
    mSignInClientInternal.setFlag(MSQATestFlag.SUCCESS);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.getCurrentAccount(
        mActivity,
        new MSQATestMetricListener<AccountInfo>(
            new MSQAMetricController(MSQAMetricEvent.GET_CURRENT_ACCOUNT), null) {
          @Override
          public void onComplete(@Nullable AccountInfo accountInfo, @Nullable MSQAException error) {
            super.onComplete(accountInfo, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.GET_CURRENT_ACCOUNT));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAcquireTokenTelemetry() {
    Assert.assertNotNull(mClient);
    mSignInClientInternal.setFlag(MSQATestFlag.SUCCESS);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);

    mClient.acquireToken(
        mActivity,
        mScopes,
        new MSQATestMetricListener<TokenResult>(
            new MSQAMetricController(MSQAMetricEvent.ACQUIRE_TOKEN), null) {
          @Override
          public void onComplete(@Nullable TokenResult tokenResult, @Nullable MSQAException error) {
            super.onComplete(tokenResult, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAcquireTokenSilentTelemetry() {
    Assert.assertNotNull(mClient);
    mSignInClientInternal.setFlag(MSQATestFlag.SUCCESS);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.acquireTokenSilent(
        mScopes,
        new MSQATestMetricListener<TokenResult>(
            new MSQAMetricController(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT), null) {
          @Override
          public void onComplete(@Nullable TokenResult tokenResult, @Nullable MSQAException error) {
            super.onComplete(tokenResult, error);
            MSQAMetric.MetricEvent event = mController.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.ACQUIRE_TOKEN_SILENT));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
