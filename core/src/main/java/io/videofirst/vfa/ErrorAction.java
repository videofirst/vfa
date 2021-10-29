package io.videofirst.vfa;

import io.videofirst.vfa.model.VfaAction;

/**
 * Interface which can be added to action classes to provide a callback method when an error occurs.
 *
 * @author Bob Marks
 */
public interface ErrorAction {

    /**
     * Optional after method.
     */
    void error(VfaAction action);

}
