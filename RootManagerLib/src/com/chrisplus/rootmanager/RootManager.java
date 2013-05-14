
package com.chrisplus.rootmanager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.text.TextUtils;

import com.chrisplus.rootmanager.container.Command;
import com.chrisplus.rootmanager.container.OperationResult;
import com.chrisplus.rootmanager.container.Shell;
import com.chrisplus.rootmanager.exception.PermissionException;
import com.chrisplus.rootmanager.utils.Remounter;
import com.chrisplus.rootmanager.utils.RootUtils;

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
            return OperationResult.FAILED;
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

        OperationResult result = OperationResult.INSTALL_FIALED;
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
            return OperationResult.FAILED;
        }

        RootUtils.checkUIThread();

        String command = Constants.COMMAND_UNINSTALL + packageName;
        OperationResult result = OperationResult.UNINSTALL_FAILED;
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

    /**
     * Uninstall a system app.
     * <p>
     * For performance, do NOT call this function on UI thread,
     * {@link IllegalStateException} will be thrown if you do so.
     * </p>
     * 
     * @param apkPath the source apk path of system app.
     * @return The result of run command operation or uninstall operation.
     */
    public OperationResult uninstallSystemApp(String apkPath) {
        RootUtils.checkUIThread();
        if (TextUtils.isEmpty(apkPath)) {
            return OperationResult.FAILED;
        }

        if (remount("/system/", "rw")) {
            File apkFile = new File(apkPath);
            if (apkFile.exists()) {
                return runCommand("rm '" + apkPath + "'");
            }
        }

        return OperationResult.FAILED;
    }

    /**
     * Install a binary file into <I>"/system/bin/"</I>
     * 
     * @param filePath The target of the binary file.
     * @return the operation result.
     */
    public boolean installBinary(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        return copyFile(filePath, "/system/bin/");
    }

    /**
     * Uninstall a binary from <I>"/system/bin/"</I>
     * 
     * @param fileName, the name of target file.
     * @return the operation result.
     */
    public boolean removeBinary(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }

        File file = new File("/system/bin/" + fileName);
        if (!file.exists()) {
            return false;
        }

        if (remount("/system/", "rw")) {
            return runCommand("rm '" + "/system/bin/" + fileName + "'").getResult();
        } else {
            return false;
        }

    }

    /**
     * Copy a file into destination dir.
     * <p>
     * Because Android do not support <I>"cp"</I> command by default,
     * <i>"cat source > destination"</i> will be used.
     * </p>
     * 
     * @param source the source file path.
     * @param destinationDir the destination dir path.
     * @return the operation result.
     */
    public boolean copyFile(String source, String destinationDir) {
        if (TextUtils.isEmpty(destinationDir) || TextUtils.isEmpty(source)) {
            return false;
        }

        File sourceFile = new File(source);
        File desFile = new File(destinationDir);
        if (!sourceFile.exists() || !desFile.isDirectory()) {
            return false;
        }

        if (remount(destinationDir, "rw")) {
            return runCommand("cat '" + source + "' > " + destinationDir).getResult();
        } else {
            return false;
        }

    }

    /**
     * Remount a path file as the type.
     * 
     * @param path the path you want to remount
     * @param mountType the mount type, including, <i>"r", "w", "rw"</i>
     * @return the operation result.
     */
    public boolean remount(String path, String mountType) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(mountType)) {
            return false;
        }

        if (mountType.equalsIgnoreCase("r") || mountType.equalsIgnoreCase("w")
                || mountType.equalsIgnoreCase("rw") || mountType.equalsIgnoreCase("wr")) {
            return Remounter.remount(path, mountType);
        } else {
            return false;
        }

    }

    /**
     * Run a binary which exit in <i>"/system/bin/"</i>
     * 
     * @param binaryName the file name of binary, containing params if
     *            necessary.
     * @return the operation result.
     */
    public OperationResult runBinBinary(String binaryName) {
        if (TextUtils.isEmpty(binaryName)) {
            return OperationResult.FAILED;
        }
        return runBinary("/system/bin/" + binaryName);
    }

    /**
     * Run a binary file.
     * 
     * @param path the file path of binary, containing params if necessary.
     * @return the operation result.
     */
    public OperationResult runBinary(String path) {
        if (TextUtils.isEmpty(path)) {
            return OperationResult.FAILED;
        }

        OperationResult result = OperationResult.FAILED;

        Command commandImpl = new Command(path) {

            @Override
            public void onUpdate(int id, String message) {
            }

            @Override
            public void onFinished(int id) {

            }

            @Override
            public void onFailed(int id, int errorCode) {

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
     * Run a command in default shell.
     * 
     * @param command the command string.
     * @return the operation result.
     */
    public OperationResult runCommand(String command) {
        if (TextUtils.isEmpty(command)) {
            return OperationResult.FAILED;
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

    public boolean isProcessRunning(String processName) {
        return false;
    }

    public boolean killProcess(String processName) {
        return false;
    }

    public void restartDevice() {

    }

    private static boolean accessRoot = false;

    private boolean accessRoot() {

        boolean result = false;
        accessRoot = false;

        Command commandImpl = new Command("id") {

            @Override
            public void onUpdate(int id, String message) {
                if (message != null && message.toLowerCase().contains("uid=0")) {
                    accessRoot = true;
                }
            }

            @Override
            public void onFinished(int id) {

            }

            @Override
            public void onFailed(int id, int errorCode) {

            }

        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
            result = accessRoot;
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            result = false;
        } catch (PermissionException e) {
            e.printStackTrace();
            result = false;
        }

        return result;

    }
}
