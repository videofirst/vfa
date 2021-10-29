package io.videofirst.vfa.exceptions;

/**
 * Vfa assertion error which extends from the java.lang.AssertionError.
 *
 * @author Bob Marks
 */
public class VfaAssertionError extends AssertionError {

    public VfaAssertionError(Throwable throwable) {
        super(throwable);
    }

    public VfaAssertionError(String message, Throwable throwable) {
        super(message, throwable);
    }

    public VfaAssertionError(String message) {
        super(message);
    }

}
