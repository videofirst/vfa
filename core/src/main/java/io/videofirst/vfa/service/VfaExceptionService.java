package io.videofirst.vfa.service;

import io.micronaut.context.annotation.Context;
import io.videofirst.vfa.enums.VfaStatus;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.exceptions.VfaSilentException;
import io.videofirst.vfa.logger.VfaLogger;
import io.videofirst.vfa.model.VfaError;
import io.videofirst.vfa.model.VfaScenario;
import io.videofirst.vfa.properties.VfaExceptionsProperties;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Exception service.
 */
@Context // load immediately
public class VfaExceptionService {

    @Inject
    private VfaLogger logger;

    @Inject
    private VfaExceptionsProperties exceptionsProperties;

    public VfaError getVfaError(Throwable throwable) {
        List<String> stackTrace = exceptionsProperties.getFilteredStackTrace(throwable);
        return VfaError.builder()
            .message(throwable.getMessage())
            .stackTrace(stackTrace)
            .throwable(throwable)
            .build();
    }

    public void handleThrowable(VfaScenario scenario, Throwable throwable) {

        boolean notLogged = scenario == null || scenario.getError() == null;
        if (notLogged) {
            VfaError error = getVfaError(throwable);
            scenario.setError(error);
            scenario.setStatus(VfaStatus.error);
            logger.error(scenario);
        }

        boolean showFull = this.exceptionsProperties.isShowFull();
        if (showFull) {
            throw new VfaException(throwable);
        } else {
            throw new VfaSilentException(throwable);
        }
    }

}