package com.chrisplus.rootmanager;

import java.io.IOException;
import java.io.OutputStream;

import com.chrisplus.rootmanager.execution.Shell;

public abstract class Command {

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
    this.id = RootUtils.generateCommandID();
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
      RootUtils.Log("Terminate all shells with reason " + reason);
      Shell.closeAll();
      setExitCode(-1);
    } catch (IOException e) {
      e.printStackTrace();
      RootUtils.Log("Terminate all shells and io exception happens");
    }
  }

  public int waitForFinish(long timeout) throws InterruptedException {
    synchronized (this) {
      while (!isFinished) {
        this.wait(timeout);
        if (!isFinished) {
          isFinished = true;
          RootUtils.Log("Timeout Exception has occurred.");
          terminate("Timeout Exception");
        }
      }
    }
    return exitCode;
  }

  public int exitCode(int timeout) throws InterruptedException {
    synchronized (this) {
      waitForFinish(timeout);
    }
    return exitCode;
  }

  public int waitForFinish() throws InterruptedException {
    synchronized (this) {
      waitForFinish(timeout);
    }
    return exitCode;
  }

  public String getCommand() {
    if (commands == null || commands.length == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < commands.length; i++) {
      sb.append(commands[i]);
      sb.append('\n');
    }
    String command = sb.toString();
    RootUtils.Log("Sending command(s): " + command);
    return command;
  }

  public void writeCommand(OutputStream out) throws IOException {
    out.write(getCommand().getBytes());
  }

}
