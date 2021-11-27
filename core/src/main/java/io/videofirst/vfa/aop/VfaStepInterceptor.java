package io.videofirst.vfa.aop;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.videofirst.vfa.Step;
import io.videofirst.vfa.StepOptions;
import io.videofirst.vfa.enums.StepType;
import io.videofirst.vfa.model.VfaStep;
import io.videofirst.vfa.model.VfaTextParameters;
import io.videofirst.vfa.service.VfaService;
import io.videofirst.vfa.util.VfaUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class VfaStepInterceptor implements MethodInterceptor<Object, Object> {

    private static final String METHOD_STEP_START_REGEX = "^(given|when|then|and|but)_";
    private static final Pattern METHOD_STEP_AND_TEXT_VALIDATOR = Pattern.compile(METHOD_STEP_START_REGEX + "\\w+");

    @Inject
    private VfaService vfaService;

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {

        Method method = context.getTargetMethod();
        String methodName = context.getMethodName();

        StepType stepType = getStepType(method);
        String stepText = getStepText(method);
        LinkedHashMap<String, Object> params = VfaUtils.getParamMapFromMethodContext(context);
        StepOptions stepOptions = getStepOptions(method);

        // Create param text object and validate
        VfaTextParameters textParameters = VfaTextParameters.parse(stepText, new ArrayList(params.values()),
            new ArrayList(params.keySet()));
        validateStepParameters(methodName, stepText, textParameters);

        // Create VfaStep object
        VfaStep step = VfaStep.builder()
            .type(stepType)
            .text(stepText)
            .textParameters(textParameters)
            .options(stepOptions)
            .build();

        vfaService.before(step);

        Object object = context.proceed();

        vfaService.after(step);
        return object;
    }

    // Private methods

    private boolean isStepTypeFromMethod() {
        return vfaService.getCurrentScenario().getStepType() != null;
    }

    private StepType getStepType(Method method) {
        // Step type from a method e.g. given() would get precedence
        StepType stepTypeFromMethod = vfaService.getCurrentScenario().getStepType();
        if (stepTypeFromMethod != null) {
            return stepTypeFromMethod;
        } else {
            String methodName = method.getName();
            validateMethodName(methodName);
            return StepType.valueOf(methodName.substring(0, methodName.indexOf("_")));
        }
    }

    private String getStepText(Method method) {
        Step stepAnnotation = method.getAnnotation(Step.class);
        if (!stepAnnotation.value().trim().isEmpty()) {
            return stepAnnotation.value().trim();
        } else if (!stepAnnotation.text().trim().isEmpty()) {
            return stepAnnotation.text().trim();
        } else {
            String methodName = method.getName();
            validateMethodName(methodName);
            // ignore first part of method
            String stepText = methodName.replaceAll(METHOD_STEP_START_REGEX, "");
            return VfaUtils.underScoresToSentence(stepText, false);
        }
    }

    private void validateStepParameters(String methodName, String text, VfaTextParameters textParameters) {
        // FIXME TODO
        // Ensure parameter indexes are not greater than number of parameters in method
        //if (textParameters != null && textParameters.initIndexes().size() > textParameters.getValues().size()) {
        //    throw new VfaException("Not enough parameters - step text [ " + text + " ] has [ " +
        //            textParameters.initIndexes().size() + " ] but method [ " + methodName + " only has [ " +
        //            textParameters.getValues().size() + " ] parameters");
        //}
    }

    private void validateMethodName(String methodName) {
        // only check method name is the step type has not been set via a step type method e.g. given()
        if (!isStepTypeFromMethod()) {
            // If stepType isn't set then ensure method name starts with e.g. "given_"
            Matcher matcher = METHOD_STEP_AND_TEXT_VALIDATOR.matcher(methodName);
            if (!matcher.find()) {
                throw new RuntimeException(
                    "Invalid method [ " + methodName
                        + " ] - must start with  [ given, when, then, but, and ], " +
                        "followed with an underscore and at least one character");
            }
        }
    }

    private StepOptions getStepOptions(Method method) {
        Step stepAnnotation = method.getAnnotation(Step.class);
        StepOptions stepOptions = new StepOptions();
        if (stepAnnotation != null && stepAnnotation.addQuotes().length > 0) {
            stepOptions.setAddQuotes(stepAnnotation.addQuotes()[0]);
        }
        return stepOptions;
    }

}