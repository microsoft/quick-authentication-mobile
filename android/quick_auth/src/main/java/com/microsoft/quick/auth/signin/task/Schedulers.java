package com.microsoft.quick.auth.signin.task;

public class Schedulers {

    static final Scheduler IO;
    static final Scheduler MAIN;

    static {
        IO = new IOScheduler();
        MAIN = new MainScheduler();
    }

    public static Scheduler io() {
        return IO;
    }

    public static Scheduler mainThread() {
        return MAIN;
    }
}

