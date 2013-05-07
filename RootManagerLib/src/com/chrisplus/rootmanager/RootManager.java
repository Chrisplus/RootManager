package com.chrisplus.rootmanager;

import java.io.File;


/**
 * 
 * This class is the main interface of RootManager.
 * 
 * @author Chris Jiang
 * 
 */
public class RootManager {

  private static RootManager instance;

  /* constants */
  private static final String[] SU_BINARY = {"/system/bin", "/system/sbin", "/system/xbin",
      "/vendor/bin", "/sbin"};
  
  /* The uesful members */
  private Boolean hasRooted = null;
  private RootManager() {

  }

  public static synchronized RootManager getInstance() {
    if (instance == null) {
      instance = new RootManager();
    }
    return instance;
  }

  /**
   * Try to check if the device has been rooted.
   * <p>
   * Generally speaking, a device which has been rooted successfully must have a binary file named
   * SU. However, SU may not work well for those devices rooted unfinished.
   * </p>
   * 
   * @return the result whether this device has been rooted.
   */
  public boolean hasRooted() {
    if (hasRooted == null) {
      for (String path : Const.SU_BINARY) {
        File su = new File(path + "/su");
        if (su != null && su.exists()) {
          hasRooted = true;
        } else {
          hasRooted = false;
        }
      }
    }

    return hasRooted;
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

  public boolean remount(String path, String mountType) {
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
