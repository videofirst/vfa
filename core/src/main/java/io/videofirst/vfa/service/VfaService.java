package io.videofirst.vfa.service;

import io.micronaut.context.annotation.Context;
import io.videofirst.vfa.StepOptions;
import io.videofirst.vfa.enums.StepType;
import io.videofirst.vfa.enums.VfaStatus;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.logger.VfaLogger;
import io.videofirst.vfa.model.VfaAction;
import io.videofirst.vfa.model.VfaError;
import io.videofirst.vfa.model.VfaFeature;
import io.videofirst.vfa.model.VfaScenario;
import io.videofirst.vfa.model.VfaStep;
import io.videofirst.vfa.model.VfaTextParameters;
import io.videofirst.vfa.model.VfaTime;
import io.videofirst.vfa.properties.VfaExceptionsProperties;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Highest level service which (should) delegate to lower level services.
 * <p>
 * Note, uses ThreadLocal approach so can be accessed easily from anywhere.
 * <p>
 * TODO (1) - Maybe add an additional service for model transformation as a lot of that is happening in here.
 * e.g. VfaModelService?
 * <p>
 * TODO (2) - There is 3 ThreadLocal objects - refactor to one?
 * <p>
 * TODO (3) - Add interface??
 * <p>
 * TODO (4) - Add validation service? e.g. VfaValidationService.
 */
@Context // load immediately
public class VfaService {

    private static ThreadLocal<VfaFeature> currentFeature = new ThreadLocal<>();
    private static ThreadLocal<VfaScenario> currentScenario = new ThreadLocal<>();
    private static ThreadLocal<Stack<VfaAction>> currentActionStack = new ThreadLocal<>();

    @Inject
    private VfaLogger logger;

    @Inject
    private VfaReportsService reportsService;

    @Inject
    private VfaExceptionService exceptionService;

    @Inject
    private VfaExceptionsProperties exceptionsProperties;

    // Before methods

    public void before(VfaFeature feature) {
        currentFeature.set(feature);
        feature.setTime(VfaTime.start());

        logger.before(feature);
        reportsService.initFeatureFolder(feature);
    }

    public void before(VfaScenario scenario) {
        VfaFeature currentFeature = getCurrentFeature();
        currentScenario.set(scenario);
        currentFeature.addScenario(scenario);

        // reset the action stack
        currentActionStack.set(new Stack<>());
        scenario.setTime(VfaTime.start());
        logger.before(scenario);
    }

    public void before(VfaStep step) {
        VfaScenario currentScenario = getCurrentScenario();

        // See if previous step needs to be finished
        List<VfaStep> steps = currentScenario.getSteps();
        if (steps != null && !steps.isEmpty()) {
            // check if last one has finished
            VfaStep previousStep = steps.get(steps.size() - 1);
            after(previousStep);
        }

        currentScenario.addStep(step);
        currentScenario.setStepType(null); // reset back again

        step.setTime(VfaTime.start());

        logger.before(step);
    }

    public void before(VfaAction action) {
        VfaStep currentStep = getCurrentStep();
        currentStep.setTotalActions(currentStep.getTotalActions() + 1);

        // Check the current action and if it has finished or not
        Stack<VfaAction> actionStack = currentActionStack.get();

        boolean noParentAction = actionStack.isEmpty();
        if (noParentAction) {
            currentStep.addAction(action);   // just add action to step
        } else {
            VfaAction parentAction = actionStack.peek(); // otherwise, add this action to parent action + link to step
            parentAction.addAction(action, currentStep);
        }

        currentActionStack.get().push(action);
        action.setTime(VfaTime.start());

        logger.before(action);
    }

    // After methods

    public void after(VfaAction action) {
        action.setTime(action.getTime().finish());

        logger.after(action);
        currentActionStack.get().pop();
    }

    public void after(VfaStep step) {
        VfaTime time = step.getTime();
        if (time == null) {  // this should never happen - indicates fundamental issue with code
            throw new VfaException("Step has no time set");
        }
        if (!step.getTime().isFinished()) {
            step.setTime(time.finish());
        }
        if (step.getStatus() == null) {
            step.setStatus(VfaStatus.passed);
        }
        logger.after(step);
    }

    public void after(VfaScenario scenario) {
        try {
            // Check if last step of this scenario needs to be closed
            List<VfaStep> steps = scenario.getSteps();
            if (steps == null || steps.isEmpty()) {
                throw new VfaException(
                    "Scenario must contain at one more steps (Given, When, Then etc)");
            }

            // check if last one has finished
            VfaStep previousStep = steps.get(steps.size() - 1);
            after(previousStep);

            // Validate - ensure last step contains at least one action
            if (previousStep.getActions() == null || previousStep.getActions().isEmpty()) {
                throw new VfaException("The last step must have at least one action");
            }
        } catch (Throwable t) {
            handleThrowable(t);
        } finally {
            if (!scenario.getTime().isFinished()) {
                scenario.setTime(scenario.getTime().finish());
            }

            logger.after(scenario);
        }
    }

    public void after(VfaFeature feature) {
        try {
            if (!feature.getTime().isFinished()) {
                feature.setTime(feature.getTime().finish());
            }
            reportsService.saveFeature(feature);
        } catch (Throwable t) {
            handleThrowable(t);
        } finally {
            logger.after(feature);
        }
    }

    // Other useful methods

    public void setStepType(StepType stepType) {
        VfaScenario scenario = getCurrentScenario();
        scenario.setStepType(stepType);
    }

    public void setStepText(String text, StepOptions options, Object... parameterValues) {
        if (text == null) {
            throw new VfaException("Step text cannot be null");
        }
        VfaScenario scenario = getCurrentScenario();
        if (scenario.getStepType() == null) {
            throw new VfaException(
                "Please set a step (Given, When, Then etc) before setting step text");
        }
        List<Object> paramValues = new ArrayList<>(Arrays.asList(parameterValues));
        VfaTextParameters textParameters = VfaTextParameters.parse(text, paramValues);

        // Create VfaStep object and continue
        VfaStep step = VfaStep.builder()
            .type(scenario.getStepType())
            .options(options)
            .text(text)
            .textParameters(textParameters)
            .build();
        before(step);
    }

    public VfaFeature getCurrentFeature() {
        VfaFeature feature = currentFeature.get();
        if (feature == null) {
            throw new VfaException(
                "No feature set - cannot continue"); // shouldn't happen in theory
        }
        return feature;
    }

    public VfaScenario getCurrentScenario() {
        VfaScenario scenario = currentScenario.get();
        if (scenario == null) {
            throw new VfaException("No scenario set - cannot continue");
        }
        return scenario;
    }

    public VfaStep getCurrentStep() {
        VfaScenario scenario = getCurrentScenario();

        // Ensure at least one step exists, otherwise throw exception
        List<VfaStep> steps = scenario.getSteps();
        if (steps == null || steps.isEmpty()) {
            throw new VfaException(
                "Please set a step (Given, When, Then etc) before executing an action");
        }

        // Retrieve last step
        VfaStep stepModel = steps.get(steps.size() - 1);
        return stepModel;
    }

    public void handleThrowable(Throwable throwable) {
        VfaScenario scenario = getCurrentScenario();
        exceptionService.handleThrowable(scenario, throwable);
    }

    public VfaError getVfaError(Throwable throwable) {
        return exceptionService.getVfaError(throwable);
    }

}