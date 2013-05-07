/*
 * This file is part of the RootKit Project: http://code.google.com/p/RootKit/
 * Copyright (c) 2012 Stephen Erickson, Chris Ravenscroft, Dominik Schuermann, Adam Shanks
 * This code is dual-licensed under the terms of the Apache License Version 2.0 and
 * the terms of the General Public License (GPL) Version 2.
 * You may use this code according to either of these licenses as is most appropriate
 * for your project on a case-by-case basis.
 * The terms of each license can be found in the root directory of this project's repository as well
 * as at:
 * * http://www.apache.org/licenses/LICENSE-2.0
 * * http://www.gnu.org/licenses/gpl-2.0.txt
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these Licenses is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See each License for the specific language governing permissions and
 * limitations under that License.
 */

package com.chrisplus.rootmanager.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.wandoujia.phoenix2.rootkit.RootKitConstants;
import com.wandoujia.phoenix2.rootkit.RootKitController;
import com.wandoujia.phoenix2.rootkit.containers.Mount;
import com.wandoujia.phoenix2.rootkit.containers.Permissions;
import com.wandoujia.phoenix2.rootkit.containers.ShellOperationResult;
import com.wandoujia.phoenix2.rootkit.exceptions.RootDeniedException;
import com.wandoujia.phoenix2.rootkit.execution.Command;
import com.wandoujia.phoenix2.rootkit.execution.CommandCapture;
import com.wandoujia.phoenix2.rootkit.execution.Shell;

public final class RootKitInternalMethods {

    // --------------------
    // # Internal methods #
    // --------------------
    boolean instantiated = false;

    protected RootKitInternalMethods() {
    }

    public static void getInstance() {
        // this will allow RootKit to be the only one to get an instance of this
        // class.
        RootKitController.setRkim(new RootKitInternalMethods());
    }

    public Permissions getPermissions(String line) {

        String[] lineArray = line.split(" ");
        String rawPermissions = lineArray[0];

        if (rawPermissions.length() == 10
                && (rawPermissions.charAt(0) == '-'
                        || rawPermissions.charAt(0) == 'd' || rawPermissions
                        .charAt(0) == 'l')
                && (rawPermissions.charAt(1) == '-' || rawPermissions.charAt(1) == 'r')
                && (rawPermissions.charAt(2) == '-' || rawPermissions.charAt(2) == 'w')) {
            RootKitController.log(rawPermissions);

            Permissions permissions = new Permissions();

            permissions.setType(rawPermissions.substring(0, 1));

            RootKitController.log(permissions.getType());

            permissions.setUserPermissions(rawPermissions.substring(1, 4));

            RootKitController.log(permissions.getUserPermissions());

            permissions.setGroupPermissions(rawPermissions.substring(4, 7));

            RootKitController.log(permissions.getGroupPermissions());

            permissions.setOtherPermissions(rawPermissions.substring(7, 10));

            RootKitController.log(permissions.getOtherPermissions());

            StringBuilder finalPermissions = new StringBuilder();
            finalPermissions.append(parseSpecialPermissions(rawPermissions));
            finalPermissions.append(parsePermissions(permissions.getUserPermissions()));
            finalPermissions.append(parsePermissions(permissions.getGroupPermissions()));
            finalPermissions.append(parsePermissions(permissions.getOtherPermissions()));

            permissions.setPermissions(Integer.parseInt(finalPermissions.toString()));

            return permissions;
        }

        return null;
    }

    public int parsePermissions(String permission) {
        int tmp;
        if (permission.charAt(0) == 'r') {
            tmp = 4;
        } else {
            tmp = 0;
        }

        RootKitController.log("permission " + tmp);
        RootKitController.log("character " + permission.charAt(0));

        if (permission.charAt(1) == 'w') {
            tmp += 2;
        } else {
            tmp += 0;
        }

        RootKitController.log("permission " + tmp);
        RootKitController.log("character " + permission.charAt(1));

        if (permission.charAt(2) == 'x') {
            tmp += 1;
        } else {
            tmp += 0;
        }

        RootKitController.log("permission " + tmp);
        RootKitController.log("character " + permission.charAt(2));

        return tmp;
    }

    public int parseSpecialPermissions(String permission) {
        int tmp = 0;
        if (permission.charAt(2) == 's') {
            tmp += 4;
        }

        if (permission.charAt(5) == 's') {
            tmp += 2;
        }

        if (permission.charAt(8) == 't') {
            tmp += 1;
        }

        RootKitController.log("special permissions " + tmp);

        return tmp;
    }

    /**
     * Copys a file to a destination. Because cp is not available on all android
     * devices, we have a fallback on the cat command
     * 
     * @param source example: /data/data/org.adaway/files/hosts
     * @param destination example: /system/etc/hosts
     * @param remountAsRw remounts the destination as read/write before writing
     *            to it
     * @param preserveFileAttributes tries to copy file attributes from source
     *            to destination, if only cat is available only permissions are
     *            preserved
     * @return true if it was successfully copied
     */
    /*
     * public boolean copyFile(String source, String destination, boolean
     * remountAsRw, boolean preserveFileAttributes) { boolean result = true; try
     * { // mount destination as rw before writing to it if (remountAsRw) {
     * RootKit.remount(destination, "RW"); } // if cp is available and has
     * appropriate permissions if (checkUtil("cp")) {
     * RootKitController.log("cp command is available!"); if
     * (preserveFileAttributes) { CommandCapture command = new CommandCapture(0,
     * "cp -fp " + source + " " + destination);
     * Shell.startRootShell().add(command).waitForFinish(); } else {
     * CommandCapture command = new CommandCapture(0, "cp -f " + source + " " +
     * destination); Shell.startRootShell().add(command).waitForFinish(); } }
     * else { if (checkUtil("busybox") && hasUtil("cp", "busybox")) {
     * RootKitController.log("busybox cp command is available!"); if
     * (preserveFileAttributes) { CommandCapture command = new CommandCapture(0,
     * "busybox cp -fp " + source + " " + destination);
     * Shell.startRootShell().add(command).waitForFinish(); } else {
     * CommandCapture command = new CommandCapture(0, "busybox cp -f " + source
     * + " " + destination);
     * Shell.startRootShell().add(command).waitForFinish(); } } else { // if cp
     * is not available use cat // if cat is available and has appropriate
     * permissions if (checkUtil("cat")) {
     * RootKitController.log("cp is not available, use cat!"); int
     * filePermission = -1; if (preserveFileAttributes) { // get permissions of
     * source before overwriting Permissions permissions =
     * getFilePermissionsSymlinks(source); filePermission =
     * permissions.getPermissions(); } CommandCapture command; // copy with cat
     * command = new CommandCapture(0, "cat " + source + " > " + destination);
     * Shell.startRootShell().add(command).waitForFinish(); if
     * (preserveFileAttributes) { // set premissions of source to destination
     * command = new CommandCapture(0, "chmod " + filePermission + " " +
     * destination); Shell.startRootShell().add(command).waitForFinish(); } }
     * else { result = false; } } } // mount destination back to ro if
     * (remountAsRw) { RootKit.remount(destination, "RO"); } } catch (Exception
     * e) { e.printStackTrace(); result = false; } return result; }
     */

    /**
     * Use this to check whether or not a file exists on the filesystem.
     * 
     * @param file String that represent the file, including the full path to
     *            the file and its name.
     * @return a boolean that will indicate whether or not the file exists.
     */
    /*
     * public boolean exists(final String file) { final List<String> result =
     * new ArrayList<String>(); Command command = new Command(0, "ls " + file) {
     * @Override public void output(int arg0, String arg1) {
     * RootKitController.log(arg1); result.add(arg1); } }; try { //Try not to
     * open a new shell if one is open. if (!Shell.isAnyShellOpen())
     * Shell.startShell().add(command).waitForFinish(); else
     * Shell.getOpenShell().add(command).waitForFinish(); } catch (Exception e)
     * { return false; } for (String line : result) { if
     * (line.trim().equals(file)) { return true; } } try {
     * RootKit.closeShell(false); } catch (Exception e) {} result.clear(); try {
     * Shell.startRootShell().add(command).waitForFinish(); } catch (Exception
     * e) { return false; } //Avoid concurrent modification... List<String>
     * final_result = new ArrayList<String>(); final_result.addAll(result); for
     * (String line : final_result) { if (line.trim().equals(file)) { return
     * true; } } return false; }
     */

    /**
     * @return long Size, converted to kilobytes (from xxx or xxxm or xxxk etc.)
     */
    public long getConvertedSpace(String spaceStr) {
        try {
            double multiplier = 1.0;
            char c;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < spaceStr.length(); i++) {
                c = spaceStr.charAt(i);
                if (!Character.isDigit(c) && c != '.') {
                    if (c == 'm' || c == 'M') {
                        multiplier = 1024.0;
                    } else if (c == 'g' || c == 'G') {
                        multiplier = 1024.0 * 1024.0;
                    }
                    break;
                }
                sb.append(spaceStr.charAt(i));
            }
            return (long) Math.ceil(Double.valueOf(sb.toString()) * multiplier);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * This method will return the inode number of a file. This method is
     * dependent on having a version of ls that supports the -i parameter.
     * 
     * @param String path to the file that you wish to return the inode number
     * @return String The inode number for this file or "" if the inode number
     *         could not be found.
     */
    public String getInode(String file) {
        try {
            Command command = new Command(RootKitConstants.GI, "/data/local/ls -i " + file) {

                @Override
                public void output(int id, String line) {
                    if (id == RootKitConstants.GI) {
                        if (!line.trim().equals("")
                                && Character.isDigit((char) line.trim().substring(0, 1)
                                        .toCharArray()[0])) {
                            InternalVariables.inode = line.trim().split(" ")[0].toString();
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            command.waitForFinish();

            return InternalVariables.inode;
        } catch (Exception ignore) {
            return "";
        }
    }

    /**
     * @return <code>true</code> if your app has been given root access.
     * @throws TimeoutException if this operation times out. (cannot determine
     *             if access is given)
     */
    public boolean isAccessGiven() {
        try {
            RootKitController.log("Checking for Root access");
            InternalVariables.accessGiven = false;

            Command command = new Command(RootKitConstants.IAG, "id") {
                @Override
                public void output(int id, String line) {
                    if (id == RootKitConstants.IAG) {
                        Set<String> ID = new HashSet<String>(Arrays.asList(line.split(" ")));
                        for (String userid : ID) {
                            RootKitController.log(userid);

                            if (userid.toLowerCase().contains("uid=0")) {
                                InternalVariables.accessGiven = true;
                                RootKitController.log("Access Given");
                                break;
                            }
                        }
                        if (!InternalVariables.accessGiven) {
                            RootKitController.log("Access Denied?");
                        }
                    }
                }
            };

            Shell.startRootShell().add(command);
            command.waitForFinish();

            if (InternalVariables.accessGiven) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isNativeToolsReady(int nativeToolsId, Context context) {
        RootKitController.log("Preparing Native Tools");
        InternalVariables.nativeToolsReady = false;

        Installer installer;
        try {
            installer = new Installer(context);
        } catch (IOException ex) {
            return false;
        }

        if (installer.isBinaryInstalled("nativetools")) {
            InternalVariables.nativeToolsReady = true;
        } else {
            InternalVariables.nativeToolsReady = installer.installBinary(nativeToolsId,
                    "nativetools", "700");
        }
        return InternalVariables.nativeToolsReady;
    }

    /**
     * This will return an ArrayList of the class Mount. The class mount
     * contains the following property's: device mountPoint type flags
     * <p/>
     * These will provide you with any information you need to work with the
     * mount points.
     * 
     * @return <code>ArrayList<Mount></code> an ArrayList of the class Mount.
     * @throws Exception if we cannot return the mount points.
     */
    public ArrayList<Mount> getMounts() throws Exception {
        LineNumberReader lnr = null;
        lnr = new LineNumberReader(new FileReader("/proc/mounts"));
        String line;
        ArrayList<Mount> mounts = new ArrayList<Mount>();
        while ((line = lnr.readLine()) != null) {

            RootKitController.log(line);

            String[] fields = line.split(" ");
            mounts.add(new Mount(new File(fields[0]), // device
                    new File(fields[1]), // mountPoint
                    fields[2], // fstype
                    fields[3] // flags
            ));
        }
        InternalVariables.mounts = mounts;

        if (InternalVariables.mounts != null) {
            return InternalVariables.mounts;
        } else {
            throw new Exception();
        }
    }

    /**
     * This will tell you how the specified mount is mounted. rw, ro, etc...
     * <p/>
     * 
     * @param The mount you want to check
     * @return <code>String</code> What the mount is mounted as.
     * @throws Exception if we cannot determine how the mount is mounted.
     */
    public String getMountedAs(String path) throws Exception {
        InternalVariables.mounts = getMounts();
        if (InternalVariables.mounts != null) {
            for (Mount mount : InternalVariables.mounts) {
                if (path.contains(mount.getMountPoint().getAbsolutePath())) {
                    RootKitController.log((String) mount.getFlags().toArray()[0]);
                    return (String) mount.getFlags().toArray()[0];
                }
            }

            throw new Exception();
        } else {
            throw new Exception();
        }
    }

    /**
     * Get the space for a desired partition.
     * 
     * @param path The partition to find the space for.
     * @return the amount if space found within the desired partition. If the
     *         space was not found then the value is -1
     * @throws TimeoutException
     */
    public long getSpace(String path) {
        InternalVariables.getSpaceFor = path;
        boolean found = false;
        RootKitController.log("Looking for Space");
        try {
            final Command command = new Command(RootKitConstants.GS, "df " + path) {

                @Override
                public void output(int id, String line) {
                    if (id == RootKitConstants.GS) {
                        if (line.contains(InternalVariables.getSpaceFor.trim())) {
                            InternalVariables.space = line.split(" ");
                        }
                    }
                }
            };

            Shell.startRootShell().add(command);
            command.waitForFinish();
        } catch (Exception e) {
        }

        if (InternalVariables.space != null) {
            RootKitController.log("First Method");

            for (String spaceSearch : InternalVariables.space) {

                RootKitController.log(spaceSearch);

                if (found) {
                    return getConvertedSpace(spaceSearch);
                } else if (spaceSearch.equals("used,")) {
                    found = true;
                }
            }

            // Try this way
            int count = 0, targetCount = 3;

            RootKitController.log("Second Method");

            if (InternalVariables.space[0].length() <= 5) {
                targetCount = 2;
            }

            for (String spaceSearch : InternalVariables.space) {

                RootKitController.log(spaceSearch);
                if (spaceSearch.length() > 0) {
                    RootKitController.log(spaceSearch + ("Valid"));
                    if (count == targetCount) {
                        return getConvertedSpace(spaceSearch);
                    }
                    count++;
                }
            }
        }
        RootKitController.log("Returning -1, space could not be determined.");
        return -1;
    }

    /**
     * Checks if there is enough Space on SDCard
     * 
     * @param updateSize size to Check (long)
     * @return <code>true</code> if the Update will fit on SDCard,
     *         <code>false</code> if not enough space on SDCard. Will also
     *         return <code>false</code>, if the SDCard is not mounted as
     *         read/write
     */
    public boolean hasEnoughSpaceOnSdCard(long updateSize) {
        RootKitController.log("Checking SDcard size and that it is mounted as RW");
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (updateSize < availableBlocks * blockSize);
    }

    /**
     * This method can be used to unpack a binary from the raw resources folder
     * and store it in /data/data/app.package/files/ This is typically useful if
     * you provide your own C- or C++-based binary. This binary can then be
     * executed using sendShell() and its full path.
     * 
     * @param context the current activity's <code>Context</code>
     * @param sourceId resource id; typically <code>R.raw.id</code>
     * @param destName destination file name; appended to
     *            /data/data/app.package/files/
     * @param mode chmod value for this file
     * @return a <code>boolean</code> which indicates whether or not we were
     *         able to create the new file.
     */
    public boolean installBinary(Context context, int sourceId, String destName, String mode) {
        Installer installer;

        try {
            installer = new Installer(context);
        } catch (IOException ex) {
            return false;
        }

        return (installer.installBinary(sourceId, destName, mode));
    }

    /**
     * This method can be used to to check if a process is running
     * 
     * @param processName name of process to check
     * @return <code>true</code> if process was found
     * @throws TimeoutException (Could not determine if the process is running)
     */
    public boolean isProcessRunning(final String processName) {
        RootKitController.log("Checks if process is running: " + processName);

        ShellOperationResult result;
        try {
            result = RootKitController.runCommand("ps", true);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (RootDeniedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        if (result != null) {
            Log.d("ChrisPono", result.getInfo());
            if (result.getInfo().contains(processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method can be used to kill a running process
     * 
     * @param processName name of process to kill
     * @return <code>true</code> if process was found and killed successfully
     */
    /*
     * public boolean killProcess(final String processName) {
     * RootKitController.log("Killing process " + processName); boolean
     * processKilled = false; try { Result result = new Result() {
     * @Override public void process(String line) throws Exception {
     * if(line.contains(processName)) { Matcher psMatcher =
     * InternalVariables.psPattern.matcher(line); try { if(psMatcher.find()) {
     * String pid = psMatcher.group(1); // concatenate to existing pids, to use
     * later in // kill if(getData() != null) { setData(getData() + " " + pid);
     * } else { setData(pid); } RootKitController.log("Found pid: " + pid); }
     * else { RootKitController.log("Matching in ps command failed!"); } } catch
     * (Exception e) { RootKitController.log("Error with regex!");
     * e.printStackTrace(); } } }
     * @Override public void onFailure(Exception ex) { setError(1); }
     * @Override public void onComplete(int diag) { }
     * @Override public void processError(String arg0) throws Exception { } };
     * RootKit.sendShell(new String[] { "ps" }, 1, result, -1);
     * if(result.getError() == 0) { // get all pids in one string, created in
     * process method String pids = (String) result.getData(); // kill processes
     * if(pids != null) { try { // example: kill -9 1234 1222 5343
     * RootKit.sendShell(new String[] { "kill -9 " + pids }, 1, -1);
     * processKilled = true; } catch (Exception e) {
     * RootKitController.log(e.getMessage()); } } } } catch (Exception e) {
     * RootKitController.log(e.getMessage()); } return processKilled; }
     */

}
