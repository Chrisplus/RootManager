
package com.chrisplus.rootmanager;

import java.io.File;

/**
 * This class is the main interface of RootManager.
 * 
 * @author Chris Jiang
 */
public class RootManager {

    private static RootManager instance;

    /* The useful members */
    private Boolean hasRooted = null;
    private boolean hasGivenPermission = false;
    private long lastPermissionCheck = -1;

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
     * Generally speaking, a device which has been rooted successfully must have
     * a binary file named SU. However, SU may not work well for those devices
     * rooted unfinished.
     * </p>
     * 
     * @return the result whether this device has been rooted.
     */
    public boolean hasRooted() {
        if (hasRooted == null) {
            for (String path : Constants.SU_BINARY_DIRS) {
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

    /**
     * Try to grant root permission.
     * <p>
     * This function may result in poping a prompt for users, wait for the
     * user's choice and operation, return result then.
     * </p>
     * <p>
     * For performance, the grant process will not be called and just return
     * true if last try was successful in
     * {@link Constants#PERMISSION_EXPIRE_TIME}.
     * </p>
     * 
     * @return whether your app has been given the root permission access by
     *         user.
     */
    public boolean grantPermission() {
        if (!hasGivenPermission) {
            hasGivenPermission = accessRoot();
            lastPermissionCheck = System.currentTimeMillis();
        } else {
            if (lastPermissionCheck < 0
                    || System.currentTimeMillis() - lastPermissionCheck > Constants.PERMISSION_EXPIRE_TIME) {
                hasGivenPermission = accessRoot();
                lastPermissionCheck = System.currentTimeMillis();
            }
        }

        return hasGivenPermission;
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

    private boolean accessRoot() {
        return false;
    }

}
