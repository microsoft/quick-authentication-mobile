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
package com.microsoft.quick.auth.signin.internal.task;

import androidx.annotation.NonNull;

public class MSQATaskThen<T, R> extends MSQATask<R> {

  private final @NonNull MSQATask<T> mSource;
  private final @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> mFunction;

  public MSQATaskThen(
      @NonNull MSQATask<T> source,
      @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> function) {
    this.mSource = source;
    this.mFunction = function;
  }

  @Override
  protected void subscribeActual(@NonNull MSQAConsumer<? super R> consumer) {
    TaskThenConsumer<T, R> parent = new TaskThenConsumer<>(consumer, mFunction);
    mSource.subscribe(parent);
  }

  static class TaskThenConsumer<T, R> implements MSQAConsumer<T> {
    private final @NonNull MSQAConsumer<? super R> mDownStream;
    private final @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> mFunction;
    private MSQADisposable mDisposable;

    public TaskThenConsumer(
        @NonNull MSQAConsumer<? super R> consumer,
        @NonNull MSQATaskFunction<? super T, ? extends MSQATask<? extends R>> function) {
      this.mDownStream = consumer;
      this.mFunction = function;
    }

    @Override
    public void onSuccess(T t) {
      try {
        MSQATask<? extends R> task = mFunction.apply(t);
        mDisposable = task.subscribe(mDownStream);
      } catch (Exception e) {
        onError(e);
      }
    }

    @Override
    public void onError(Exception t) {
      mDownStream.onError(t);
    }

    @Override
    public void onCancel() {
      if (mDisposable != null) {
        mDisposable.dispose();
      }
      mDownStream.onCancel();
    }
  }
}
