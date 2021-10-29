package io.videofirst.vfa.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Bob Marks
 */
public class VfaSilentException extends RuntimeException {

    public VfaSilentException(Throwable throwable) {
        super(throwable);
    }

    public VfaSilentException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public VfaSilentException(String message) {
        super(message);
    }

    @Override
    public void printStackTrace() {
    }

    @Override
    public void printStackTrace(PrintStream ps) {
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
    }

    @Override
    public String getMessage() {
        return null;
    }

}
