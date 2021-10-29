package io.videofirst.vfa.web.exception;

import io.videofirst.vfa.exceptions.VfaAssertionError;

/**
 * VFA web exception.
 *
 * @author Bob Marks
 */
public class VfaWebAssertionError extends VfaAssertionError {

    public VfaWebAssertionError(String message, Throwable throwable) {
        super(message, throwable);
    }

}
