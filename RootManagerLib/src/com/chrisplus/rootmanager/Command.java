package com.chrisplus.rootmanager;

import java.io.IOException;

import com.wandoujia.phoenix2.rootkit.RootKitController;
import com.wandoujia.phoenix2.rootkit.execution.Shell;

public abstract class Command implements CommandListner {

  private String[] commands;
  private boolean isFinished;
  private int exitCode;
  private long timeout;
  private int id;

  /* Abstract function should be implemented by caller */
  public abstract void onUpdate(int id, String message);

  public abstract void onFinished(int id);

  public abstract void onFailed(int id, int errorCode);

  public Command(String... commands) {
    this(Const.TIMEOUT, commands);
  }

  public Command(int timeout, String... commands) {
    this.id = RootManagerUtils.generateCommandID();
    this.timeout = timeout;
    this.commands = commands;
  }

  public void setExitCode(int code) {
    synchronized (this) {
      exitCode = code;
      isFinished = true;
      onFinished(id);
      this.notifyAll();
    }
  }
  
  public void terminate(String reason) {
    try {
      Shell.closeAll();
      RootKitController.log("Terminating all shells.");
      terminated(reason);
    } catch (IOException e) {}
  }

  public void terminated(String reason) {
    setExitCode(-1);
    RootKitController.log("Command " + id + " did not finish.");
  }

  // waits for this command to finish
  public void waitForFinish(int timeout) throws InterruptedException {
    synchronized (this) {
      while (!finished) {
        this.wait(timeout);

        if (!finished) {
          finished = true;
          RootKitController.log("Timeout Exception has occurred.");
          terminate("Timeout Exception");
        }
      }
    }
  }

  // waits for this command to finish and returns the exit code
  public int exitCode(int timeout) throws InterruptedException {
    synchronized (this) {
      waitForFinish(timeout);
    }
    return exitCode;
  }

  // waits for this command to finish
  public void waitForFinish() throws InterruptedException {
    synchronized (this) {
      waitForFinish(timeout);
    }
  }

  // waits for this command to finish and returns the exit code
  public int exitCode() throws InterruptedException {
    synchronized (this) {
      return exitCode(timeout);
    }
  }


}
