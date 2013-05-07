
package com.chrisplus.rootmanager;

public class Constants {

    /**
     * The set of su location I know by now.
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
     * The patch for some android version and devices. Install may fail without
     * this patch.
     */
    public static final String COMMAND_INSTALL_PATCH = "LD_LIBRARY_PATH=/vendor/lib:/system/lib ";

    /**
     * Install apps on sdcard.
     */
    public static final String COMMAND_INSTALL_LOCATION_EXTERNAL = "-s ";

    /**
     * Install apps on phone ram.
     */
    public static final String COMMAND_INSTALL_LOCATION_INTERNAL = "-f ";

    /**
     * The command string for uninstall app.
     */
    public static final String COMMAND_UNINSTALL = "pm uninstall ";

}
