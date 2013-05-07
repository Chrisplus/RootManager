
package com.chrisplus.rootmanager;

import android.text.TextUtils;

/**
 * This class is used to store a root operation result which contains the result
 * of execution and details information.
 * 
 * @author Chris Jiang
 */
public class OperationResult {
    private String message;
    private boolean result;

    public String getMessage() {
        return message;
    }

    public boolean getResult() {
        return result;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private OperationResult() {

    }

    public final static class Builder {
        private String msg = null;
        private Boolean res = null;

        public Builder setResult(boolean result) {
            res = result;
            return this;
        }

        public Builder setMessage(String message) {
            msg = message;
            return this;
        }

        public OperationResult build() {
            if (res != null && !TextUtils.isEmpty(msg)) {
                OperationResult result = new OperationResult();
                result.message = msg;
                result.result = res;

                return result;
            }

            return null;
        }
    }
}
