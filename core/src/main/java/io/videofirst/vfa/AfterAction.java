package io.videofirst.vfa;

import io.videofirst.vfa.model.VfaAction;

/**
 * Interface which can be added to action classes to provide an after callback method.
 *
 * @author Bob Marks
 */
public interface AfterAction {

    /**
     * Optional after method.
     */
    void after(VfaAction action);

}
