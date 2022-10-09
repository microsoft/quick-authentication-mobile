package com.azuresamples.quickauth.sign.test;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

// MSQA Api test class.
@RunWith(AndroidJUnit4.class)
public class MSQAAPITest extends MSQABaseTest {

  private String[] mScopes = new String[] {"user.read"};

  @Test
  public void testSignInApi() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);

    mClient.signIn(
        mActivity,
        new MSQATestMetricListener<>(
            (accountInfo, error) -> {
              Assert.assertNotNull(accountInfo);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSignOutApiWithAccount() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.signOut(
        new MSQATestMetricListener<>(
            (aBoolean, error) -> {
              Assert.assertEquals(aBoolean, true);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSignOutApiWithoutAccount() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.removeAllAccount(mActivity);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.signOut(
        new MSQATestMetricListener<>(
            (aBoolean, error) -> {
              Assert.assertNotNull(error);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetCurrentAccountBeforeSignInApi() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.removeAllAccount(mActivity);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.getCurrentAccount(
        mActivity,
        new MSQATestMetricListener<>(
            (accountInfo, error) -> {
              Assert.assertEquals(true, accountInfo == null && error == null);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetCurrentAccountAfterSignInApi() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.getCurrentAccount(
        mActivity,
        new MSQATestMetricListener<>(
            (accountInfo, error) -> {
              Assert.assertNotNull(accountInfo);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAcquireTokenApi() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.acquireToken(
        mActivity,
        mScopes,
        new MSQATestMetricListener<>(
            (tokenResult, error) -> {
              Assert.assertNotNull(tokenResult);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAcquireTokenSilentlyApi() {
    Assert.assertNotNull(mClient);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mClient.acquireTokenSilent(
        mScopes,
        new MSQATestMetricListener<>(
            (tokenResult, error) -> {
              Assert.assertNotNull(tokenResult);
              latch.countDown();
            }));
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
