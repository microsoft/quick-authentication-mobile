package com.azuresamples.quickauth.sign.test;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;
import com.microsoft.quickauth.signin.ClientCreatedListener;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.MSQASignInOptions;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.test.R;
import com.microsoft.quickauth.signin.view.MSQASignInButton;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;

public class MSQABaseTest {
  @Rule public ActivityTestRule activityTestRule = new ActivityTestRule(TestActivity.class);

  public MSQASignInClient mClient;
  public MSQATestSingleSignInClient mSignInClientInternal;
  public Activity mActivity;
  public MSQASignInButton mSignInButton;

  @Before
  public void setup() {
    mActivity = activityTestRule.getActivity();
    mSignInButton = mActivity.findViewById(com.microsoft.quickauth.signin.test.R.id.signInButton);
    final CountDownLatch latch = new CountDownLatch(1);
    MSQASignInClient.create(
        mActivity,
        new MSQASignInOptions.Builder()
            .setConfigResourceId(R.raw.msqa_test_config_single_account)
            .setEnableLogcatLog(true)
            .build()
            .setTestSingleClientProvider(
                signInClientApplication -> {
                  mSignInClientInternal =
                      new MSQATestSingleSignInClient(signInClientApplication, mActivity);
                  return mSignInClientInternal;
                }),
        new ClientCreatedListener() {
          @Override
          public void onCreated(@NonNull MSQASignInClient client) {
            mClient = client;
            latch.countDown();
          }

          @Override
          public void onError(@NonNull MSQAException error) {
            latch.countDown();
          }
        });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Assert.assertNotNull(mClient);
  }
}
