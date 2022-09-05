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
package com.microsoft.quickauth.signin.internal.task;

import androidx.annotation.NonNull;

public abstract class MSQATask<T> {

  public static <T> MSQATask<T> create(@NonNull ConsumerHolder<T> consumerHolder) {
    return new MSQATaskCreate<>(consumerHolder);
  }

  public MSQADisposable subscribe(@NonNull MSQAConsumer<? super T> consumer) {
    MSQACancelableConsumer<T> cancelableConsumer = new MSQACancelableConsumer<>(consumer);
    subscribeActual(cancelableConsumer);
    return cancelableConsumer;
  }

  protected abstract void subscribeActual(@NonNull MSQAConsumer<? super T> consumer);

  public static <T> MSQATask<T> with(@NonNull final T value) {
    return MSQATask.create(
        new ConsumerHolder<T>() {
          @Override
          public void start(@NonNull MSQAConsumer<? super T> consumer) {
            consumer.onSuccess(value);
          }
        });
  }

  public <R> MSQATask<R> then(
      @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> mapper) {
    return new MSQATaskThen<>(this, mapper);
  }

  public MSQATask<T> upStreamScheduleOn(MSQAThreadSwitcher scheduler) {
    return new MSQAUSTaskScheduleOn<>(this, scheduler);
  }

  public MSQATask<T> downStreamSchedulerOn(MSQAThreadSwitcher scheduler) {
    return new MSQADSTScheduleOn<>(this, scheduler);
  }

  public interface ConsumerHolder<T> {
    void start(@NonNull MSQAConsumer<? super T> consumer);
  }
}
