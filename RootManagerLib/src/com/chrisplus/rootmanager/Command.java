package com.chrisplus.rootmanager;

public abstract class Command {

  private String[] commands;
  private boolean isFinished;
  private int exitCode;
  private long timeout;
  private int id;

  public Command(String... commands) {
    this(Const.TIMEOUT, commands);
  }

  public Command(int timeout, String... commands) {
    this.id = RootManagerUtils.generateCommandID();
    this.timeout = timeout;
    this.commands = commands;
  }
  
  public 

}
