package com.microsoft.quick.auth.signin;

import java.util.HashSet;
import java.util.Set;

public class TaskDisposable implements Disposable {
    private final Object mLock;
    private volatile boolean mIsCanceled;
    private final Set<Disposable> disposables;

    public TaskDisposable() {
        mLock = new Object();
        disposables = new HashSet<>();
    }

    @Override
    public void dispose() {
        synchronized (this.mLock) {
            mIsCanceled = true;
            for (Disposable d : disposables) {
                d.dispose();
            }
        }
    }

    @Override
    public boolean isDisposed() {
        return mIsCanceled;
    }

    public void add(Disposable disposable) {
        if (disposable == null || isDisposed()) return;
        synchronized (this.mLock) {
            disposables.add(disposable);
        }
    }

    public void remove(Disposable disposable) {
        if (disposable == null || isDisposed()) return;
        synchronized (this.mLock) {
            disposables.remove(disposable);
        }
    }

}
