package io.videofirst.vfa.exceptions;

public class VfaException extends RuntimeException {

    public VfaException(Throwable throwable) {
        super(throwable);
    }

    public VfaException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public VfaException(String message) {
        super(message);
    }

}