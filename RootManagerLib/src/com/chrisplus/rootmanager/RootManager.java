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
  private Context context;

  private static RootManager instance;

  private RootManager() {

  }

  public static synchronized RootManager getInstance() {
    if (instance == null) {
      instance = new RootManager();
    }
    return instance;
  }




}
