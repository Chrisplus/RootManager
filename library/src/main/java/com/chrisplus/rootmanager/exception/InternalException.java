
package com.chrisplus.rootmanager.exception;

/**
 * This exception will be throwed when Root Manager internal error occured.
 * 
 * @author Chris Jiang
 */
public class InternalException extends Exception {

    private static final long serialVersionUID = -4431771251773644144L;

    public InternalException(Throwable th) {
        super(th);
    }
}
