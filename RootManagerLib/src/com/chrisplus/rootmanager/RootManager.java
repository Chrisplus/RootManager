package com.chrisplus.rootmanager;

import android.content.Context;
import android.os.Looper;

/**
 * 
 * This class is the main interface of RootManager.
 * 
 * @author Chris Jiang
 * 
 */
public class RootManager {

  private static RootManager instance;

  private RootManager() {

  }

  public static synchronized RootManager getInstance() {
    if (instance == null) {
      instance = new RootManager();
    }
    return instance;
  }

  public boolean hasRooted() {
    return false;
  }

  public boolean grantPermission() {
    return false;
  }

  public OperationResult installPackage(String apkPath) {
    return null;
  }

  public OperationResult installPackage(String apkPath, String installLocation) {
    return null;
  }

  public OperationResult uninstallPackage(String packageName) {
    return null;
  }

  public OperationResult uninstallSystemApp(String packageName) {
    return null;
  }

  public OperationResult installBinary(String filePath) {
    return null;
  }

  public OperationResult removeBinary(String binaryName) {
    return null;
  }

  public boolean copyFiles(String source, String destination, boolean withRootPermission) {
    return false;
  }

  public OperationResult runBinary(String binaryName) {
    return null;
  }

  public OperationResult runBinary(String binaryName, String params) {
    return null;
  }

  public OperationResult runCommand(String command) {
    return null;
  }

  public OperationResult runCommand(Command command) {
    return null;
  }

  public boolean isProcessRunning(String processName) {
    return false;
  }

  public boolean killProcess(String processName) {
    return false;
  }

  public void restartDevice() {

  }



}
