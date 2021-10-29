package io.videofirst.vfa;

import io.videofirst.vfa.model.VfaAction;

/**
 * Interface which can be added to action classes to provide a before callback method.
 *
 * @author Bob Marks
 */
public interface BeforeAction {

    /**
     * Before method.
     */
    void before(VfaAction action);

}
