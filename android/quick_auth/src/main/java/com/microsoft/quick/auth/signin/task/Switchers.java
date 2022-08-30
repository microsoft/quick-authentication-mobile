package com.microsoft.quick.auth.signin.task;

public class Switchers {

    static final ThreadSwitcher IO;
    static final ThreadSwitcher MAIN;

    static {
        IO = new IOThreadSwitcher();
        MAIN = new MainThreadSwitcher();
    }

    public static ThreadSwitcher io() {
        return IO;
    }

    public static ThreadSwitcher mainThread() {
        return MAIN;
    }
}

