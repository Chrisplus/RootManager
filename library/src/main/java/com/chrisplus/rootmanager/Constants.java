package com.chrisplus.rootmanager;

public class Constants {

    /**
     * The set of SU location
     */
    public static final String[] SU_BINARY_DIRS = {
            "/system/bin", "/system/sbin", "/system/xbin",
            "/vendor/bin", "/sbin"
    };

    /**
     * The expire time for granted permission, ten minutes
     */
    public static final long PERMISSION_EXPIRE_TIME = 1000 * 60 * 10;

    /**
     * The command string for install app
     */
    public static final String COMMAND_INSTALL = "pm install -r ";

    /**
     * The patch for some android versions and devices. Install process may fail
     * without this patch.
     */
    public static final String COMMAND_INSTALL_PATCH = "LD_LIBRARY_PATH=/vendor/lib:/system/lib ";

    /**
     * Install apps on SD-card.
     */
    public static final String COMMAND_INSTALL_LOCATION_EXTERNAL = "-s ";

    /**
     * Install apps on phone RAM.
     */
    public static final String COMMAND_INSTALL_LOCATION_INTERNAL = "-f ";

    /**
     * The command string for uninstall app.
     */
    public static final String COMMAND_UNINSTALL = "pm uninstall ";

    /**
     * The command string for screen cap.
     */
    public static final String COMMAND_SCREENCAP = "screencap ";

    /**
     * The command string for processing show.
     */
    public static final String COMMAND_PS = "ps";

    /**
     * The command string for kill process.
     */
    public static final String COMMAND_KILL = "kill ";

    /**
     * The command string for find pid of a process.
     */
    public static final String COMMAND_PIDOF = "pidof ";

    /**
     * The command string for screen record.
     */
    public static final String COMMAND_SCREENRECORD = "screenrecord ";

    /**
     * The default command timeout is 5 min.
     */
    public static final int COMMAND_TIMEOUT = 1000 * 60 * 5;

    /**
     * The path of system
     */
    public static final String PATH_SYSTEM = "/system/";

    /**
     * The path of system bin
     */
    public static final String PATH_SYSTEM_BIN = "/system/bin/";

    /**
     * The default recording bit rate
     */

    public static final long SCREENRECORD_BITRATE_DEFAULT = 4000000L;

    /**
     * The default recording time limit
     */
    public static final int SCREENRECORD_TIMELIMIT_DEFAULT = 30;

}
