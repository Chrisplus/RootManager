
package com.chrisplus.rootmanager.exception;

/**
 * This exception will be throwed when Root Manager internal error occured.
 * 
 * @author Chris Jiang
 */
public class RootManagerInternalException extends Exception {

    private static final long serialVersionUID = -4431771251773644144L;

    public RootManagerInternalException(Throwable th) {
        super(th);
    }
}
