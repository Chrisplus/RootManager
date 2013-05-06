package com.chrisplus.rootmanager;

public interface CommandListner {

  public void onUpdate(int id, String message);

  public void onFinished(int id);

  public void onFailed(int errorCode);
}
