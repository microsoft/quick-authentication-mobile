package com.microsoft.quick.auth.signin.internal.task;

public class MSQASwitchers {

  static final MSQAThreadSwitcher IO;
  static final MSQAThreadSwitcher MAIN;

  static {
    IO = new MSQAIOThreadSwitcher();
    MAIN = new MSQAMainThreadSwitcher();
  }

  public static MSQAThreadSwitcher io() {
    return IO;
  }

  public static MSQAThreadSwitcher mainThread() {
    return MAIN;
  }
}
