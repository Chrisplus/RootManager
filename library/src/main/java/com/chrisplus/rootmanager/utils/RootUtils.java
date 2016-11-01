package com.chrisplus.rootmanager.utils;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Command;

import android.os.Build;
import android.os.Looper;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * This class is a set of methods used in {@link RootManager}.
 *
 * @author Chris
 */
public class RootUtils {

    private final static String TAG = "RootManager";

    private static int cmdID = 0;

    /**
     * Avoid instantiating the util class
     */
    private RootUtils() {

    }

    /**
     * To check if the caller is on the ui thread, throw exception if it calls
     * on UI thread.
     */
    public static void checkUIThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Please do not call this fuction on UI thread");
        }
    }

    /**
     * Get the environment path.
     *
     * @return the environment path in a list.
     */
    public static List<String> getPath() {
        return Arrays.asList(System.getenv("PATH").split(":"));
    }

    /**
     * Output log to logcat as the debug level.
     *
     * @param message The message will be printed.
     */
    public static void Log(String message) {
        Log.d(TAG, message);
    }

    /**
     * Output log to logcat as the debug level with extend log tag.
     * <p>
     * The example: LibTag::YourExtendTag, Log Message.
     * </p>
     *
     * @param extendTag Your extend tag.
     * @param message   The message will be printed.
     */
    public static void Log(String extendTag, String message) {
        Log.d(TAG + "::" + extendTag, message);
    }

    /**
     * Get a command Id for each {@link Command}.
     *
     * @return the actual ID.
     */
    public static int generateCommandID() {
        cmdID = cmdID + 1;
        RootUtils.Log("Return a command id " + cmdID);
        return cmdID;
    }

    /**
     * Check if command need patch.
     *
     * @return whether the device need to be set a new SDK location.
     */
    public static boolean isJellyBeanMR1() {
        return android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * Check is the sdk is Jelly Bean (16) and upper
     *
     * @return true if Jelly Bean and upper
     */
    public static boolean isJellyBeanUpper() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Check if Android 4.4 and upper.
     *
     * @return true for 4.4 upper.
     */
    public static boolean isKitKatUpper() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static String getArchName() {
        return Build.CPU_ABI;
    }
}
