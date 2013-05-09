
package com.chrisplus.rootmanager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.chrisplus.rootmanager.exception.PermissionException;
import com.chrisplus.rootmanager.execution.Shell;

import android.text.TextUtils;

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

    /**
     * Install a user app on the device.
     * <p>
     * For performance, do NOT call this function on UI thread,
     * {@link IllegalStateException} will be thrown if you do so.
     * </p>
     * 
     * @param apkPath the file path of apk, do not start with <I>file://</I>.
     * @return The result of run command operation or install operation.
     */
    public OperationResult installPackage(String apkPath) {
        return installPackage(apkPath, "a");
    }

    /**
     * Install a user app on the device.
     * <p>
     * For performance, do NOT call this function on UI thread,
     * {@link IllegalStateException} will be thrown if you do so.
     * </p>
     * 
     * @param apkPath the file path of apk, do not start with <I>file://</I>.
     * @param installLocation the location of install.
     *            <ul>
     *            <li>auto means chooing the install location automatic.</li>
     *            <li>ex means install the app on sdcard.</li>
     *            <li>in means install the app in phone ram</li>
     *            </ul>
     * @return The result of run command operation or install operation.
     */
    public OperationResult installPackage(String apkPath, String installLocation) {

        if (TextUtils.isEmpty(apkPath)) {
            return OperationResult.INSTALL_FIALED;
        }

        RootUtils.checkUIThread();

        String command = Constants.COMMAND_INSTALL;
        if (RootUtils.isNeedPathSDK()) {
            command = Constants.COMMAND_INSTALL_PATCH + command;
        }

        command = command + apkPath;

        if (TextUtils.isEmpty(installLocation)) {
            if (installLocation.equalsIgnoreCase("ex")) {
                command = command + Constants.COMMAND_INSTALL_LOCATION_EXTERNAL;
            } else if (installLocation.equalsIgnoreCase("in")) {
                command = command + Constants.COMMAND_INSTALL_LOCATION_INTERNAL;
            }
        }

        OperationResult result = null;
        // TODO fill the install command call back.
        Command commandImpl = new Command(command) {

            @Override
            public void onUpdate(int id, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinished(int id) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailed(int id, int errorCode) {
                // TODO Auto-generated method stub

            }

        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_INTERRUPTED;
        } catch (IOException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED;
        } catch (TimeoutException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_TIMEOUT;
        } catch (PermissionException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_DENIED;
        }

        return result;

    }

    /**
     * Uninstall a user app using its package name.
     * <p>
     * For performance, do NOT call this function on UI thread,
     * {@link IllegalStateException} will be thrown if you do so.
     * </p>
     * 
     * @param packageName the app's package name you want to uninstall.
     * @return The result of run command operation or uninstall operation.
     */
    public OperationResult uninstallPackage(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        RootUtils.checkUIThread();

        String command = Constants.COMMAND_UNINSTALL + packageName;
        OperationResult result = null;
        // TODO fill in uninstall function.
        Command commandImpl = new Command(command) {

            @Override
            public void onUpdate(int id, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinished(int id) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailed(int id, int errorCode) {
                // TODO Auto-generated method stub

            }

        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_INTERRUPTED;
        } catch (IOException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED;
        } catch (TimeoutException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_TIMEOUT;
        } catch (PermissionException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_DENIED;
        }

        return result;
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
        if (TextUtils.isEmpty(command)) {
            return null;
        }

        OperationResult result = null;
        Command commandImpl = new Command(command) {

            @Override
            public void onUpdate(int id, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinished(int id) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailed(int id, int errorCode) {
                // TODO Auto-generated method stub

            }

        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_INTERRUPTED;
        } catch (IOException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED;
        } catch (TimeoutException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_TIMEOUT;
        } catch (PermissionException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_DENIED;
        }

        return result;
    }

    public OperationResult runCommand(Command command) {
        if (command == null) {
            return null;
        }

        OperationResult result = null;

        try {
            Shell.startRootShell().add(command).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_INTERRUPTED;
        } catch (IOException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED;
        } catch (TimeoutException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_TIMEOUT;
        } catch (PermissionException e) {
            e.printStackTrace();
            result = OperationResult.RUNCOMMAND_FAILED_DENIED;
        }

        return result;
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
