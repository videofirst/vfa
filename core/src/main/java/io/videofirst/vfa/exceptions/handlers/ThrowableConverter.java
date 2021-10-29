package io.videofirst.vfa.exceptions.handlers;

/**
 * Interface used to convert a throwable to another throwable.  This is useful when a client library has a messy
 * exception message and can be used to tidy it up for multiple calls.
 *
 * @author Bob Marks
 */
public interface ThrowableConverter {

    /**
     * Convert an exception to another exception.
     */
    Throwable convertThrowable(Throwable throwable);

}
