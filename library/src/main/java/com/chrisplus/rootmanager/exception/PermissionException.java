package com.chrisplus.rootmanager.exception;

/**
 * This exception will be throwed when Root permission error occurs.
 *
 * @author Chris Jiang
 */
public class PermissionException extends Exception {

    private static final long serialVersionUID = -8713947214162841310L;

    public PermissionException(String error) {
        super(error);
    }
}
