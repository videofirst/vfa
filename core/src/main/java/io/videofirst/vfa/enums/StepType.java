package io.videofirst.vfa.enums;

import static io.videofirst.vfa.util.VfaUtils.capFirst;
import static java.lang.String.format;

/**
 * Types of step i.e. Given, When, Then, And, But, None.
 */
public enum StepType {

    // main step types

    given(true),
    when(true),
    then(true),

    // other step types

    and(false),
    but(false),
    none(false);

    private boolean main;

    StepType(boolean main) {
        this.main = main;
    }

    /**
     * Convert type to a label i.e. capitalise the first letter and right align.
     */
    public String label() {
        String titledStep = capFirst(this.toString());
        return format("%5s", titledStep);
    }

    public boolean isMain() {
        return this.main;
    }

}