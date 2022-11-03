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

import androidx.annotation.Nullable;
import com.microsoft.quickauth.signin.error.MSQAException;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetric;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricController;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricEvent;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricListener;
import com.microsoft.quickauth.signin.internal.metric.MSQAMetricMessage;
import com.microsoft.quickauth.signin.internal.metric.MSQASignOutMessageMapper;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MSQATelemetryTest {

  @Test
  public void checkMetricSuccessValue() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    MSQAMetricController controller =
        new MSQAMetricController(MSQAMetricEvent.TEST, new MSQASignOutMessageMapper());
    MSQAMetricListener<Object> listener =
        new MSQAMetricListener<Object>(controller, null) {
          @Override
          public void onComplete(@Nullable Object o, @Nullable MSQAException error) {
            super.onComplete(o, error);
            MSQAMetric.MetricEvent event = controller.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.TEST));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.SUCCESS));
            latch.countDown();
          }
        };
    listener.onComplete(true, null);
    latch.await();
  }

  @Test
  public void checkMetricFailureValue() {
    final MSQATestSafeCountDownLatch latch = new MSQATestSafeCountDownLatch(1);
    MSQAMetricController controller =
        new MSQAMetricController(MSQAMetricEvent.TEST, new MSQASignOutMessageMapper());
    MSQAMetricListener<Object> listener =
        new MSQAMetricListener<Object>(controller, null) {
          @Override
          public void onComplete(@Nullable Object o, @Nullable MSQAException error) {
            super.onComplete(o, error);
            MSQAMetric.MetricEvent event = controller.getEvent();
            Assert.assertTrue(event.getEventName().equals(MSQAMetricEvent.TEST));
            Assert.assertTrue(event.getMessage().equals(MSQAMetricMessage.FAILURE));
            latch.countDown();
          }
        };
    listener.onComplete(null, new MSQAException());
    latch.await();
  }
}
