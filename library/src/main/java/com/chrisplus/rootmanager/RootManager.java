package com.chrisplus.rootmanager;

import android.text.TextUtils;

import com.chrisplus.rootmanager.container.Command;
import com.chrisplus.rootmanager.container.Result;
import com.chrisplus.rootmanager.container.Result.ResultBuilder;
import com.chrisplus.rootmanager.container.Shell;
import com.chrisplus.rootmanager.exception.PermissionException;
import com.chrisplus.rootmanager.utils.Remounter;
import com.chrisplus.rootmanager.utils.RootUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class RootManager {

    private static RootManager instance;

    private static boolean accessRoot = false;

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
     * Check if the device is rooted.
     *
     * @return this device is rooted or not.
     * @deprecated use {@link #isRootAvailable()} instead.
     */
    public boolean hasRooted() {
        if (hasRooted == null) {
            for (String path : Constants.SU_BINARY_DIRS) {
                File su = new File(path + "/su");
                if (su.exists()) {
                    hasRooted = true;
                    break;
                } else {
                    hasRooted = false;
                }
            }
        }

        return hasRooted;
    }

    /**
     * Check if the device is rooted
     *
     * @return this device is rooted or not.
     */
    public boolean isRootAvailable() {
        if (hasRooted == null || hasRooted == false) {
            hasRooted = false;
            List<String> paths = RootUtils.getPath();
            for (String path : paths) {
                if (path.endsWith(File.pathSeparator)) {
                    path = path + File.pathSeparator;
                }
                File su = new File(path + "/su");

                if (su.exists()) {
                    hasRooted = true;
                    break;
                }
            }
        }

        return hasRooted;
    }

    /**
     * Try to obtain the root access.
     *
     * @return the app has been granted the permission or not.
     */
    public boolean obtainPermission() {
        if (hasGivenPermission) {
            return hasGivenPermission;
        }

        if (System.currentTimeMillis() - lastPermissionCheck > Constants.PERMISSION_EXPIRE_TIME) {
            hasGivenPermission = accessRoot();
            lastPermissionCheck = System.currentTimeMillis();
        }

        return hasGivenPermission;
    }

    /**
     * Try to obtain the root permission on unrooted phones.
     *
     * @return the app has been granted the root permission or not.
     */
    public boolean obtainPermissionOnUnrooted() {
        return false;
    }

    /**
     * Install an app on the device.
     * <p>
     * Do NOT call this function on UI thread, {@link IllegalStateException} will be thrown
     * otherwise.
     * </p>
     *
     * @param apkPath the APK file path i.e., <I>"/sdcard/Tech_test.apk"</I> is OK. ASCII is
     *                supported.
     * @return the result {@link Result} of running the command.
     */
    public Result installPackage(String apkPath) {
        return installPackage(apkPath, "a");
    }

    /**
     * Install a app on the specific location.
     * <p>
     * Do NOT call this function on UI thread, {@link IllegalStateException} will be thrown
     * otherwise.
     * </p>
     *
     * @param apkPath         the APK file path i.e., <I>"/sdcard/Tech_test.apk"</I> is OK. ASCII
     *                        is supported.
     * @param installLocation the location of this installation.
     *                        <ul>
     *                        <li>auto: choose the install  location automatically.</li>
     *                        <li>ex: install the app on sdcard.</li>
     *                        <li>in: install the app on ram</li>
     *                        </ul>
     * @return the result {@link Result} of running the command.
     */
    public Result installPackage(String apkPath, String installLocation) {

        RootUtils.checkUIThread();

        final ResultBuilder builder = Result.newBuilder();

        if (TextUtils.isEmpty(apkPath)) {
            return builder.setFailed().build();
        }

        String command = Constants.COMMAND_INSTALL;
        if (RootUtils.isJellyBeanMR1()) {
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

        final StringBuilder infoSb = new StringBuilder();
        Command commandImpl = new Command(command) {

            @Override
            public void onUpdate(int id, String message) {
                infoSb.append(message + "\n");
            }

            @Override
            public void onFinished(int id) {
                String finalInfo = infoSb.toString();
                if (TextUtils.isEmpty(finalInfo)) {
                    builder.setInstallFailed();
                } else {
                    setInstallPackageResult(builder, finalInfo);
                }
            }

        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            builder.setCommandFailedInterrupted();
        } catch (IOException e) {
            e.printStackTrace();
            builder.setCommandFailed();
        } catch (TimeoutException e) {
            e.printStackTrace();
            builder.setCommandFailedTimeout();
        } catch (PermissionException e) {
            e.printStackTrace();
            builder.setCommandFailedDenied();
        }

        return builder.build();
    }

    /**
     * Install an app on the specific location asynchronously.
     *
     * @param apkPath the APK file path i.e., <I>"/sdcard/Tech_test.apk"</I> is OK. ASCII
     *                is supported.
     * @return the result {@link Result} of running the command.
     */
    public Maybe<Result> observeInstallPackage(final String apkPath) {
        return observeInstallPackage(apkPath, "a");
    }

    /**
     * Install a app on the specific location asynchronously.
     *
     * @param apkPath         the APK file path i.e., <I>"/sdcard/Tech_test.apk"</I> is OK. ASCII
     *                        is supported.
     * @param installLocation the location of this installation.
     *                        <ul>
     *                        <li>auto: choose the install  location automatically.</li>
     *                        <li>ex: install the app on sdcard.</li>
     *                        <li>in: install the app on ram</li>
     *                        </ul>
     * @return the result {@link Result} of running the command.
     */
    public Maybe<Result> observeInstallPackage(final String apkPath, final String
            installLocation) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> observableEmitter)
                    throws Exception {

                if (TextUtils.isEmpty(apkPath)) {
                    observableEmitter.onError(new IllegalArgumentException());
                }

                String command = Constants.COMMAND_INSTALL;
                if (RootUtils.isJellyBeanMR1()) {
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

                final Command commandImpl = new Command(command) {

                    @Override
                    public void onUpdate(int id, String message) {
                        observableEmitter.onNext(message);
                    }

                    @Override
                    public void onFinished(int id) {
                        observableEmitter.onComplete();
                    }
                };

                observableEmitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        if (!commandImpl.isFinished()) {
                            commandImpl.terminate();
                        }
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });

                try {
                    Shell.startRootShell().add(commandImpl).waitForFinish();
                } catch (Exception e) {
                    observableEmitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String message) throws Exception {
                        return !TextUtils.isEmpty(message) && (message.contains("success") ||
                                message.contains("Success") || message.contains("failed") ||
                                message.contains("FAILED"));
                    }
                })
                .toList()
                .map(new Function<List<String>, Result>() {
                    @Override
                    public Result apply(List<String> messages) throws Exception {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String next : messages) {
                            stringBuilder.append(next).append("\n");
                        }

                        Result.ResultBuilder resultBuilder = Result.newBuilder();
                        String finalMessage = stringBuilder.toString();
                        setInstallPackageResult(resultBuilder, finalMessage);
                        return resultBuilder.build();
                    }
                })
                .toMaybe();

    }


    /**
     * Uninstall an app by its package name.
     * <p>
     * Do NOT call this function on UI thread, {@link IllegalStateException} will be thrown
     * otherwise.
     * </p>
     *
     * @param packageName the app's package name.
     * @return the result {@link Result} of running the command.
     */
    public Result uninstallPackage(String packageName) {
        RootUtils.checkUIThread();

        final ResultBuilder builder = Result.newBuilder();

        if (TextUtils.isEmpty(packageName)) {
            return builder.setFailed().build();
        }

        String command = Constants.COMMAND_UNINSTALL + packageName;
        final StringBuilder infoSb = new StringBuilder();

        Command commandImpl = new Command(command) {

            @Override
            public void onUpdate(int id, String message) {
                infoSb.append(message + "\n");
            }

            @Override
            public void onFinished(int id) {
                String finalInfo = infoSb.toString();
                if (TextUtils.isEmpty(finalInfo)) {
                    builder.setUninstallFailed();
                } else {
                    if (finalInfo.contains("Success") || finalInfo.contains("success")) {
                        builder.setUninstallSuccess();
                    } else {
                        builder.setUninstallFailed();
                    }
                }
            }
        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            builder.setCommandFailedInterrupted();
        } catch (IOException e) {
            e.printStackTrace();
            builder.setCommandFailed();
        } catch (TimeoutException e) {
            e.printStackTrace();
            builder.setCommandFailedTimeout();
        } catch (PermissionException e) {
            e.printStackTrace();
            builder.setCommandFailedDenied();
        }

        return builder.build();
    }

    /**
     * Uninstall an app by its package name.
     *
     * @param packageName the app's package name.
     * @return the result {@link Result} of running the command.
     */
    public Maybe<Result> observeUninstallPackage(final String packageName) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> observableEmitter)
                    throws Exception {
                if (TextUtils.isEmpty(packageName)) {
                    observableEmitter.onError(new IllegalArgumentException());
                }

                String command = Constants.COMMAND_UNINSTALL + packageName;

                final Command commandImpl = new Command(command) {
                    @Override
                    public void onUpdate(int id, String message) {
                        observableEmitter.onNext(message);
                    }

                    @Override
                    public void onFinished(int id) {
                        observableEmitter.onComplete();
                    }
                };

                observableEmitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        if (!commandImpl.isFinished()) {
                            commandImpl.terminate();
                        }
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });

                try {
                    Shell.startRootShell().add(commandImpl).waitForFinish();
                } catch (Exception e) {
                    observableEmitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String message) throws Exception {
                        return !TextUtils.isEmpty(message);
                    }
                })
                .toList()
                .map(new Function<List<String>, Result>() {
                    @Override
                    public Result apply(List<String> messages) throws Exception {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String next : messages) {
                            stringBuilder.append(next).append("\n");
                        }

                        Result.ResultBuilder resultBuilder = Result.newBuilder();
                        String finalMessage = stringBuilder.toString();
                        setUninstallPackageResult(resultBuilder, finalMessage);
                        return resultBuilder.build();
                    }
                })
                .toMaybe();
    }

    /**
     * Uninstall a system app by its path.
     * <p>
     * Do NOT call this function on UI thread, {@link IllegalStateException} will be thrown
     * otherwise.
     * </p>
     *
     * @param apkPath the source apk path of the system app.
     * @return the result {@link Result} of running the command.
     */
    public Result uninstallSystemApp(String apkPath) {
        RootUtils.checkUIThread();

        ResultBuilder builder = Result.newBuilder();
        if (TextUtils.isEmpty(apkPath)) {
            return builder.setFailed().build();
        }

        if (remount(Constants.PATH_SYSTEM, "rw")) {
            File apkFile = new File(apkPath);
            if (apkFile.exists()) {
                return runCommand("rm '" + apkPath + "'");
            }
        }

        return builder.setFailed().build();
    }


    /**
     * Uninstall a system app by its path.
     *
     * @param apkPath the source apk path of the system app.
     * @return the result {@link Result} of running the command.
     */
    public Maybe<Result> observeUninstallSystemApp(final String apkPath) {

        return Maybe.create(new MaybeOnSubscribe<Result>() {
            @Override
            public void subscribe(MaybeEmitter<Result> maybeEmitter) throws Exception {
                if (TextUtils.isEmpty(apkPath)) {
                    maybeEmitter.onError(new IllegalArgumentException());
                }

                ResultBuilder builder = Result.newBuilder();
                if (remount(Constants.PATH_SYSTEM, "rw")) {
                    File apkFile = new File(apkPath);
                    if (apkFile.exists()) {
                        maybeEmitter.onSuccess(runCommand("rm '" + apkPath + "'"));
                    } else {
                        maybeEmitter.onSuccess(builder.setFailed().build());
                    }
                } else {
                    maybeEmitter.onSuccess(builder.setFailed().build());
                }

                maybeEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
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

        return copyFile(filePath, Constants.PATH_SYSTEM_BIN);
    }

    /**
     * Remove a binary from <I>"/system/bin/"</I>
     *
     * @param fileName, name of target file.
     * @return the operation result.
     */
    public boolean removeBinary(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }

        File file = new File(Constants.PATH_SYSTEM_BIN + fileName);
        if (!file.exists()) {
            return false;
        }

        if (remount(Constants.PATH_SYSTEM, "rw")) {
            return runCommand("rm '" + Constants.PATH_SYSTEM_BIN + fileName + "'").getResult();
        } else {
            return false;
        }

    }

    /**
     * Copy a file into the destination dir.
     *
     * @param source         the source file path.
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
     * Remount file system.
     *
     * @param path      the path you want to remount
     * @param mountType the mount type, including, <i>"ro", read only, "rw" , read and write</i>
     * @return the operation result.
     */
    public boolean remount(String path, String mountType) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(mountType)) {
            return false;
        }

        if (mountType.equalsIgnoreCase("rw") || mountType.equalsIgnoreCase("ro")) {
            return Remounter.remount(path, mountType);
        } else {
            return false;
        }

    }


    /**
     * Run a binary file in the root shell.
     *
     * @param path the file path of binary with parameters if necessary.
     * @return the result {@link Result} of running the command.
     */
    public Result runBinary(String path) {
        return runCommand(path);
    }

    /**
     * Run a binary file in the root shell.
     *
     * @param path the file path of binary with containing parameters if necessary.
     * @return the output messages wrapped in {@link Observable}
     */
    public Observable<String> obverseRunBinary(String path) {
        return observeRunCommand(path);
    }

    /**
     * Run raw commands in the root shell.
     *
     * @param command the command string.
     * @return the result {@link Result} of running the command.
     */
    public Result runCommand(String command) {

        final ResultBuilder builder = Result.newBuilder();
        if (TextUtils.isEmpty(command)) {
            return builder.setFailed().build();
        }

        final StringBuilder infoSb = new StringBuilder();
        Command commandImpl = new Command(command) {

            @Override
            public void onUpdate(int id, String message) {
                infoSb.append(message + "\n");
            }

            @Override
            public void onFinished(int id) {
                builder.setCustomMessage(infoSb.toString());
            }

        };

        try {
            Shell.startRootShell().add(commandImpl).waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
            builder.setCommandFailedInterrupted();
        } catch (IOException e) {
            e.printStackTrace();
            builder.setCommandFailed();
        } catch (TimeoutException e) {
            e.printStackTrace();
            builder.setCommandFailedTimeout();
        } catch (PermissionException e) {
            e.printStackTrace();
            builder.setCommandFailedDenied();
        }

        return builder.build();
    }

    /**
     * Run raw commands in the root shell
     *
     * @param command the command with parameters if necessary.
     * @return the output messages wrapped in {@link Observable}
     */
    public Observable<String> observeRunCommand(final String command) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> observableEmitter)
                    throws Exception {
                if (TextUtils.isEmpty(command)) {
                    observableEmitter.onError(new IllegalArgumentException());
                }

                final Command commandImpl = new Command(command) {
                    @Override
                    public void onUpdate(int id, String message) {
                        observableEmitter.onNext(message);
                    }

                    @Override
                    public void onFinished(int id) {
                        observableEmitter.onComplete();
                    }
                };

                observableEmitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        if (!commandImpl.isFinished()) {
                            commandImpl.terminate();
                        }
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
                try {
                    Shell.startRootShell().add(commandImpl).waitForFinish();
                } catch (Exception e) {
                    observableEmitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * Take a screenshot
     *
     * @param path the path with file name and extend name.
     * @return the operation result.
     */
    public boolean screenCap(String path) {

        if (TextUtils.isEmpty(path)) {
            return false;
        }
        Result res = runCommand(Constants.COMMAND_SCREENCAP + path);
        RootUtils.Log((res == null) + "");

        return res.getResult();
    }


    /**
     * Record the screen for 30s. This function is ONLY supported on Android 4.4 and upper.
     *
     * @param path the path with file name and extend name.
     * @return the operation result.
     * @deprecated use
     */
    public boolean screenRecord(String path) {
        return screenRecord(path, Constants.SCREENRECORD_BITRATE_DEFAULT,
                Constants.SCREENRECORD_TIMELIMIT_DEFAULT);
    }

    /**
     * Record screen. This function is ONLY supported on Android 4.4 and upper.
     *
     * @param path    the path with file name and extend name.
     * @param bitRate the bate rate in bps, i.e., 4000000 for 4M bps.
     * @param time    the recording time in seconds
     * @return the operation result.
     */

    public boolean screenRecord(String path, long bitRate, long time) {

        if (!RootUtils.isKitKatUpper() || TextUtils.isEmpty(path)) {
            return false;
        }
        Result res = runCommand(Constants.COMMAND_SCREENRECORD + " --verbose" + " --bit-rate " +
                bitRate
                + " --time-limit " + time + " " + path);
        return res.getResult();
    }

    public Observable<String> observeScreenRecord(final String path, long bitRate, long time) {
        return observeRunCommand(Constants.COMMAND_SCREENRECORD + " --verbose" + " --bit-rate " +
                bitRate + " --time-limit " + time + " " + path);
    }

    /**
     * Check if a process is running.
     *
     * @param processName the name of process. For user app, the process name is
     *                    its package name.
     * @return whether this process is running.
     */
    public boolean isProcessRunning(String processName) {

        if (TextUtils.isEmpty(processName)) {
            return false;
        }
        Result infos = runCommand(Constants.COMMAND_PS);
        return infos.getMessage().contains(processName);
    }

    /**
     * Kill a process by its name.
     *
     * @param processName the name of this process. For user apps, the process
     *                    name is its package name.
     * @return the operation result.
     */
    public boolean killProcessByName(String processName) {
        if (TextUtils.isEmpty(processName)) {
            return false;
        }
        Result res = runCommand(Constants.COMMAND_PIDOF + processName);

        if (!TextUtils.isEmpty(res.getMessage())) {
            return killProcessById(res.getMessage());
        } else {
            return false;
        }
    }

    /**
     * Kill a process by its process id.
     *
     * @param processID PID of the target process.
     * @return the operation result.
     */
    public boolean killProcessById(String processID) {
        if (TextUtils.isEmpty(processID)) {
            return false;
        }

        Result res = runCommand(Constants.COMMAND_KILL + processID);
        return res.getResult();
    }

    /**
     * Restart the device.
     */
    public void restartDevice() {
        killProcessByName("zygote");
    }

    private boolean accessRoot() {

        boolean result = false;
        accessRoot = false;
        String getSuCommands[] = {"su", "id"};
        final StringBuilder messageBuilder = new StringBuilder();
        Command commandImpl = new Command(getSuCommands) {

            @Override
            public void onUpdate(int id, String message) {
                messageBuilder.append(message);
            }

            @Override
            public void onFinished(int id) {
                String finalMesssage = messageBuilder.toString();
                if (finalMesssage != null && finalMesssage.toLowerCase().contains("uid=0")) {
                    accessRoot = true;
                }
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

    private void setInstallPackageResult(ResultBuilder resultBuilder, String message) {
        if (message.contains("success") || message.contains("Success")) {
            resultBuilder.setCommandSuccess();
        } else if (message.contains("FAILED_INSUFFICIENT_STORAGE")) {
            resultBuilder.setInsallFailedNoSpace();
        } else if (message.contains("FAILED_INCONSISTENT_CERTIFICATES")) {
            resultBuilder.setInstallFailedWrongCer();
        } else if (message.contains("FAILED_CONTAINER_ERROR")) {
            resultBuilder.setInstallFailedWrongCer();
        } else {
            resultBuilder.setInstallFailed();
        }
    }

    private void setUninstallPackageResult(ResultBuilder resultBuilder, String message) {
        if (!TextUtils.isEmpty(message) && (message.contains("Success") || message.contains
                ("success"))) {
            resultBuilder.setUninstallSuccess();
        } else {
            resultBuilder.setUninstallFailed();
        }
    }

}
