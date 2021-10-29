package io.videofirst.vfa.enums;

/**
 * VFA status.  Trying to keep this fairly simple / small number of errors.
 *
 * @author Bob Marks
 */
public enum VfaStatus {

    passed,
    failed,
    error,
    ignored;

    public boolean isErrorOrFail() {
        return this == error || this == failed;
    }

}
