package com.azuresamples.quickauth.sign.test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.microsoft.quickauth.signin.error.MSQACancelException;
import com.microsoft.quickauth.signin.test.R;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestActivityTest extends MSQABaseTest {

  @Test
  public void testSignInWithSignInButtonSuccess() {
    testSignInWithSignInButton(MSQATestFlag.SUCCESS);
  }

  @Test
  public void testSignInWithSignInButtonError() {
    testSignInWithSignInButton(MSQATestFlag.ERROR);
  }

  @Test
  public void testSignInWithSignInButtonCancel() {
    testSignInWithSignInButton(MSQATestFlag.CANCEL);
  }

  private void testSignInWithSignInButton(@MSQATestFlag String flag) {
    Assert.assertNotNull(mClient);
    Assert.assertNotNull(mSignInButton);
    mSignInClientInternal.setFlag(flag);
    MSQATestMockUtil.setCurrentAccount(mActivity, MSQATestMockUtil.DEFAULT_TEST_ACCOUNT);
    final CountDownLatch latch = new CountDownLatch(1);
    mSignInButton.setSignInCallback(
        mActivity,
        mClient,
        new MSQATestMetricListener<>(
            (accountInfo, error) -> {
              if (MSQATestFlag.ERROR.equals(flag)) {
                Assert.assertNotNull(error);
              } else if (MSQATestFlag.CANCEL.equals(flag)) {
                Assert.assertEquals(true, error instanceof MSQACancelException);
              } else {
                Assert.assertNotNull(accountInfo);
              }

              latch.countDown();
            }));
    Espresso.onView(withId(R.id.signInButton)).perform(click());
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
