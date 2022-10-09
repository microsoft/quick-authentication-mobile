package com.azuresamples.quickauth.sign.test;

import androidx.annotation.Nullable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.microsoft.quickauth.signin.MSQASignInClient;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import com.microsoft.quickauth.signin.logger.ILogger;
import com.microsoft.quickauth.signin.logger.LogLevel;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

// MSQA log test class.
@RunWith(AndroidJUnit4.class)
public class MSQALoggingTest {

  private final String TAG = "MSQALoggingTest";

  @Before
  public void setup() {
    MSQASignInClient.setEnableLogcatLog(true);
  }

  @Test
  public void testVerboseLevel() {
    testLevel(LogLevel.VERBOSE);
  }

  @Test
  public void testInfoLevel() {
    testLevel(LogLevel.INFO);
  }

  @Test
  public void testWarnLevel() {
    testLevel(LogLevel.WARN);
  }

  @Test
  public void testErrorLevel() {
    testLevel(LogLevel.ERROR);
  }

  public void testLevel(int level) {
    MSQASignInClient.setLogLevel(level);
    final CountDownLatch latch = new CountDownLatch(1);
    MSQASignInClient.setExternalLogger(
        new ILogger() {
          @Override
          public void log(int logLevel, @Nullable String message) {
            Assert.assertEquals(level, logLevel);
            latch.countDown();
          }
        });
    String testLog = "testLevel";
    switch (level) {
      case LogLevel.VERBOSE:
        MSQALogger.getInstance().verbose(TAG, testLog);
        break;
      case LogLevel.INFO:
        MSQALogger.getInstance().info(TAG, testLog);
        break;
      case LogLevel.WARN:
        MSQALogger.getInstance().warn(TAG, testLog);
        break;
      case LogLevel.ERROR:
        MSQALogger.getInstance().error(TAG, testLog, null);
        break;
      default:
        Assert.assertNotNull(level);
        break;
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
