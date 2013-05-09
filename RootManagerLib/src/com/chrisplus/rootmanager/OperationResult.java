
package com.chrisplus.rootmanager;

/**
 * This class is used to store a root operation result which contains the result
 * of execution and details information.
 * 
 * @author Chris Jiang
 */
public enum OperationResult {

    RUNCOMMAND_SUCCESS(90, "Command Executed Successfully"),
    RUNCOMMAND_FAILED_TIMEOUT(401, "Run Command Timeout"),
    RUNCOMMAND_FAILED_DENIED(402, "Run Command Permission Denied"),
    RUNCOMMAND_FAILED_INTERRUPTED(403, "Run Command Interrupted"),

    INSTALL_SUCCESS(80, "Application installed Successfully"),
    INSTALL_FAILED_NOSPACE(404, "Install Failed because of no enough space"),
    INSTALL_FAILED_WRONGCONTAINER(405, "Install Failed Wrong container"),
    INSTALL_FAILED_WRONGCER(406, "Install Failed Wrong Cer or version"),
    INSTALL_FIALED(407, "Install Failed"),

    UNINSTALL_SUCCESS(70, "Application uninstall Successfully"),
    UNINSTALL_FAILED(408, "Uninstall App Failed");

    private int statusCode;
    private String message;

    private OperationResult(int sc, String msg) {
        statusCode = sc;
        message = msg;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean getResult() {
        if (statusCode <= 100) {
            return true;
        } else {
            return false;
        }
    }
}
