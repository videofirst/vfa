package io.videofirst.vfa.logger;

import io.videofirst.vfa.model.VfaAction;
import io.videofirst.vfa.model.VfaFeature;
import io.videofirst.vfa.model.VfaScenario;
import io.videofirst.vfa.model.VfaStep;

/**
 * Methods which are called as part of logging.
 */
public interface VfaLogger {

    void before(VfaFeature feature);

    void before(VfaScenario scenario);

    void before(VfaStep step);

    void before(VfaAction action);

    void after(VfaAction action);

    void after(VfaStep step);

    void after(VfaScenario scenario);

    void after(VfaFeature feature);

    void error(VfaScenario error);

}